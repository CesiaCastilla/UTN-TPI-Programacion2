-- =============================================================================
-- TRABAJO FINAL INTEGRADOR: Bases de Datos I + Programación II
-- SISTEMA: Food Store (Gestión de Pedidos de Comida)
-- SCRIPT: 08_transacciones.sql (Transacciones, manejo de errores y retry ante deadlock)
-- INTEGRANTE 2: Etapa 5 - Concurrencia y transacciones
-- =============================================================================
-- Mecanismo: procedimiento almacenado sp_confirmar_pedido que envuelve la
-- confirmación de un pedido (descuento de stock por cada detalle + cambio de
-- estado) en una transacción explícita (START TRANSACTION/COMMIT/ROLLBACK).
-- Si MySQL detecta un deadlock (error 1213 / SQLSTATE 40001), la transacción
-- se reintenta hasta 2 veces con un backoff breve creciente. Todo resultado
-- (éxito, deadlock, stock insuficiente, fallo definitivo) queda registrado en
-- la tabla log_transacciones para poder auditarlo con un SELECT.
-- Requisito: ejecutar después de 01_esquema.sql, 02_catalogo.sql y
-- 03_carga_masiva.sql (necesita pedidos y productos ya cargados).
-- Idempotente: DROP TABLE/PROCEDURE IF EXISTS antes de crear.
-- =============================================================================

USE food_store;

DROP TABLE IF EXISTS log_transacciones;
DROP PROCEDURE IF EXISTS sp_confirmar_pedido;

-- =============================================================================
-- Tabla de logging de errores y resultados de las transacciones
-- =============================================================================
CREATE TABLE log_transacciones (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    pedido_id BIGINT NOT NULL,
    intento INT NOT NULL,
    resultado VARCHAR(30) NOT NULL,
    detalle VARCHAR(255),
    creado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_log_resultado CHECK (
        resultado IN ('OK', 'DEADLOCK', 'STOCK_INSUFICIENTE', 'FALLO_DEFINITIVO')
    )
);

-- =============================================================================
-- Procedimiento: sp_confirmar_pedido
-- Confirma un pedido PENDIENTE: descuenta el stock de cada producto de su
-- detalle y pasa el pedido a estado CONFIRMADO, todo dentro de una única
-- transacción. Bloquea las filas involucradas (FOR UPDATE) para que dos
-- confirmaciones concurrentes que compiten por el mismo stock se serialicen
-- o, si MySQL detecta un deadlock, se reintenten automáticamente.
-- =============================================================================
DELIMITER //

CREATE PROCEDURE sp_confirmar_pedido(IN p_pedido_id BIGINT)
BEGIN
    DECLARE v_intento INT DEFAULT 0;
    DECLARE v_max_intentos INT DEFAULT 3; -- 1 intento inicial + hasta 2 reintentos
    DECLARE v_exito TINYINT DEFAULT 0;
    DECLARE v_deadlock TINYINT DEFAULT 0;

    intentos: WHILE v_intento < v_max_intentos AND v_exito = 0 DO
        SET v_intento = v_intento + 1;
        SET v_deadlock = 0;

        BEGIN
            DECLARE v_estado_actual VARCHAR(20);
            DECLARE v_fin INT DEFAULT 0;
            DECLARE v_producto_id BIGINT;
            DECLARE v_cantidad INT;
            DECLARE v_stock_actual INT;
            DECLARE cur_detalles CURSOR FOR
                SELECT producto_id, cantidad
                FROM detalles_pedidos
                WHERE pedido_id = p_pedido_id AND eliminado = 0;

            DECLARE CONTINUE HANDLER FOR NOT FOUND SET v_fin = 1;
            DECLARE EXIT HANDLER FOR SQLSTATE '40001'
            BEGIN
                ROLLBACK;
                SET v_deadlock = 1;
                INSERT INTO log_transacciones (pedido_id, intento, resultado, detalle)
                VALUES (p_pedido_id, v_intento, 'DEADLOCK',
                        'Error 1213/SQLSTATE 40001: deadlock detectado por MySQL, se reintenta la transacción');
            END;

            START TRANSACTION;

            -- Bloquea la fila del pedido para evitar confirmaciones concurrentes del mismo pedido
            SELECT estado INTO v_estado_actual
            FROM pedidos
            WHERE id = p_pedido_id
            FOR UPDATE;

            OPEN cur_detalles;
            read_loop: LOOP
                FETCH cur_detalles INTO v_producto_id, v_cantidad;
                IF v_fin = 1 THEN
                    LEAVE read_loop;
                END IF;

                SELECT stock INTO v_stock_actual
                FROM productos
                WHERE id = v_producto_id
                FOR UPDATE;

                IF v_stock_actual < v_cantidad THEN
                    CLOSE cur_detalles;
                    ROLLBACK;
                    INSERT INTO log_transacciones (pedido_id, intento, resultado, detalle)
                    VALUES (p_pedido_id, v_intento, 'STOCK_INSUFICIENTE',
                            CONCAT('Producto ', v_producto_id, ' no tiene stock suficiente para la cantidad pedida'));
                    SIGNAL SQLSTATE '45000'
                        SET MESSAGE_TEXT = 'Stock insuficiente para confirmar el pedido';
                END IF;

                UPDATE productos SET stock = stock - v_cantidad WHERE id = v_producto_id;
            END LOOP;
            CLOSE cur_detalles;

            UPDATE pedidos SET estado = 'CONFIRMADO' WHERE id = p_pedido_id;

            COMMIT;
            SET v_exito = 1;
            INSERT INTO log_transacciones (pedido_id, intento, resultado, detalle)
            VALUES (p_pedido_id, v_intento, 'OK', 'Pedido confirmado correctamente');
        END;

        IF v_deadlock = 1 AND v_intento < v_max_intentos THEN
            DO SLEEP(0.3 * v_intento); -- backoff breve creciente: 0.3s, 0.6s
        END IF;
    END WHILE intentos;

    IF v_exito = 0 AND v_deadlock = 1 THEN
        INSERT INTO log_transacciones (pedido_id, intento, resultado, detalle)
        VALUES (p_pedido_id, v_intento, 'FALLO_DEFINITIVO',
                'Se agotaron los reintentos ante deadlocks sucesivos');
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'No se pudo confirmar el pedido tras reintentos por deadlock';
    END IF;
END //

DELIMITER ;

-- =============================================================================
-- Prueba 1: camino feliz — confirmar un pedido PENDIENTE con stock suficiente
-- =============================================================================
SET @pedido_test_ok = (SELECT id FROM pedidos WHERE estado = 'PENDIENTE' LIMIT 1);
CALL sp_confirmar_pedido(@pedido_test_ok);

SELECT * FROM log_transacciones WHERE pedido_id = @pedido_test_ok ORDER BY id DESC;
SELECT id, estado FROM pedidos WHERE id = @pedido_test_ok;

-- =============================================================================
-- Prueba 2: camino de error — stock insuficiente (forzado para la prueba)
-- Se lleva a 0 el stock de un producto de un pedido PENDIENTE distinto del
-- usado en la Prueba 1, para no afectar ese resultado, y se confirma que el
-- procedimiento hace ROLLBACK, registra el error y no modifica el estado.
-- =============================================================================
SET @pedido_test_fail = (
    SELECT dp.pedido_id
    FROM detalles_pedidos dp
    JOIN pedidos p ON p.id = dp.pedido_id
    WHERE p.estado = 'PENDIENTE' AND dp.pedido_id <> @pedido_test_ok
    LIMIT 1
);
SET @producto_fail = (
    SELECT producto_id FROM detalles_pedidos WHERE pedido_id = @pedido_test_fail LIMIT 1
);

UPDATE productos SET stock = 0 WHERE id = @producto_fail;

-- Se espera: ERROR 1644 (45000) Stock insuficiente para confirmar el pedido
CALL sp_confirmar_pedido(@pedido_test_fail);

SELECT * FROM log_transacciones WHERE pedido_id = @pedido_test_fail ORDER BY id DESC;
SELECT id, estado FROM pedidos WHERE id = @pedido_test_fail; -- debe seguir PENDIENTE (ROLLBACK)
