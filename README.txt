============================================================
TRABAJO FINAL INTEGRADOR - BASES DE DATOS I
============================================================

INTEGRANTES:
- Alfredo Castillo
- Cesia Catilla
- Rocio Aguero

VERSION DE MYSQL UTULIZADA:
- MySQL Server 8.0

============================================================
ORDEN DE EJECUCIÓN DE SCRIPTS SQL
============================================================

Se deben ejecutar los datos en el siguiente orden:

1. 01_esquema.sql (creacion de tablas y restricciones)

CREATE DATABASE IF NOT EXISTS food_store;
USE food_store;

DROP TABLE IF EXISTS detalles_pedidos;
DROP TABLE IF EXISTS pedidos;
DROP TABLE IF EXISTS productos;
DROP TABLE IF EXISTS usuarios;
DROP TABLE IF EXISTS categorias;

CREATE TABLE categorias (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE,
    -- Campos heredados de la clase Base (Java)
    eliminado TINYINT(1) NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE usuarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(150) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    rol VARCHAR(20) NOT NULL,
    -- Campos heredados de la clase Base (Java)
    eliminado TINYINT(1) NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    -- Restricción Check para simular el Enum de Java (ADMIN, USUARIO)
    CONSTRAINT chk_usuario_rol CHECK (rol IN ('ADMIN', 'USUARIO'))
);

CREATE TABLE productos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(150) NOT NULL,
    precio DECIMAL(10, 2) NOT NULL,
    stock INT NOT NULL,
    categoria_id BIGINT NOT NULL,
    -- Campos heredados de la clase Base (Java)
    eliminado TINYINT(1) NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    -- Restricciones de integridad de negocio
    CONSTRAINT chk_producto_precio CHECK (precio >= 0),
    CONSTRAINT chk_producto_stock CHECK (stock >= 0),
    -- Relación N:1 con Categorías
    CONSTRAINT fk_productos_categorias FOREIGN KEY (categoria_id) 
        REFERENCES categorias(id)
);


CREATE TABLE pedidos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',
    forma_pago VARCHAR(30) NOT NULL,
    total DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    -- Campos heredados de la clase Base (Java)
    eliminado TINYINT(1) NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    -- Restricciones de integridad para Enums y montos
    CONSTRAINT chk_pedido_estado CHECK (estado IN ('PENDIENTE', 'CONFIRMADO', 'TERMINADO', 'CANCELADO')),
    CONSTRAINT chk_pedido_pago CHECK (forma_pago IN ('TARJETA', 'TRANSFERENCIA', 'EFECTIVO')),
    CONSTRAINT chk_pedido_total CHECK (total >= 0),
    -- Relación N:1 con Usuarios (Quién realiza el pedido)
    CONSTRAINT fk_pedidos_usuarios FOREIGN KEY (usuario_id) 
        REFERENCES usuarios(id)
);

CREATE TABLE detalles_pedidos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    pedido_id BIGINT NOT NULL,
    producto_id BIGINT NOT NULL,
    cantidad INT NOT NULL,
    precio_unitario DECIMAL(10, 2) NOT NULL,
    subtotal DECIMAL(10, 2) NOT NULL,
    -- Campos heredados de la clase Base (Java)
    eliminado TINYINT(1) NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    -- Restricciones de negocio
    CONSTRAINT chk_detalle_cantidad CHECK (cantidad > 0),
    CONSTRAINT chk_detalle_precio CHECK (precio_unitario >= 0),
    CONSTRAINT chk_detalle_subtotal CHECK (subtotal >= 0),
    -- Relaciones (Claves Foráneas)
    CONSTRAINT fk_detalles_pedidos FOREIGN KEY (pedido_id) 
        REFERENCES pedidos(id) ON DELETE CASCADE,
    CONSTRAINT fk_detalles_productos FOREIGN KEY (producto_id) 
        REFERENCES productos(id)
);

2. 02_catalogo.sql (Carga de datos semilla iniciales)

INSERT INTO categorias (nombre) VALUES 
('Hamburguesas'),
('Pizzas y Empanadas'),
('Bebidas'),
('Postres');

INSERT INTO usuarios (nombre, email, rol) VALUES 
('Carlos Admin', 'admin@foodstore.com', 'ADMIN'),
('Juan Perez', 'juan.perez@gmail.com', 'USUARIO'),
('Ana Gomez', 'ana.gomez@gmail.com', 'USUARIO');

3. 03_carga_masiva.sql (Generacion y carga de datos masivos)

SET SESSION cte_max_recursion_depth = 20000;

INSERT INTO usuarios (nombre, email, rol)
WITH RECURSIVE numeros AS (
    SELECT 1 AS n
    UNION ALL
    SELECT n + 1 FROM numeros WHERE n < 1000
)
SELECT
    CONCAT('Usuario Demo ', n),
    CONCAT('usuario.demo', n, '@foodstore-demo.com'),
    IF(n % 25 = 0, 'ADMIN', 'USUARIO')
FROM numeros;

INSERT INTO productos (nombre, precio, stock, categoria_id)
WITH RECURSIVE numeros AS (
    SELECT 1 AS n
    UNION ALL
    SELECT n + 1 FROM numeros WHERE n < 1000
),
cats AS (
    SELECT id, ROW_NUMBER() OVER (ORDER BY id) AS rn
    FROM categorias
    WHERE eliminado = 0
)
SELECT
    CONCAT('Producto Demo ', nu.n),
    ROUND(1 + RAND() * 49, 2),     -- precio entre 1.00 y 50.00 (respeta CHECK precio >= 0)
    FLOOR(RAND() * 200),           -- stock entre 0 y 199 (respeta CHECK stock >= 0)
    c.id
FROM numeros nu
JOIN cats c ON c.rn = (nu.n % (SELECT COUNT(*) FROM cats)) + 1;

INSERT INTO pedidos (usuario_id, estado, forma_pago, total)
WITH RECURSIVE numeros AS (
    SELECT 1 AS n
    UNION ALL
    SELECT n + 1 FROM numeros WHERE n < 4000
),
usrs AS (
    SELECT id, ROW_NUMBER() OVER (ORDER BY id) AS rn
    FROM usuarios
    WHERE eliminado = 0
)
SELECT
    u.id,
    ELT(FLOOR(RAND() * 4) + 1, 'PENDIENTE', 'CONFIRMADO', 'TERMINADO', 'CANCELADO'),
    ELT(FLOOR(RAND() * 3) + 1, 'TARJETA', 'TRANSFERENCIA', 'EFECTIVO'),
    0.00 -- se recalcula en el paso 5, una vez cargados los detalles
FROM numeros nu
JOIN usrs u ON u.rn = (nu.n % (SELECT COUNT(*) FROM usrs)) + 1;

INSERT INTO detalles_pedidos (pedido_id, producto_id, cantidad, precio_unitario, subtotal)
WITH RECURSIVE chico AS (
    SELECT 1 AS k
    UNION ALL
    SELECT k + 1 FROM chico WHERE k < 4
),
prods AS (
    SELECT id, precio, ROW_NUMBER() OVER (ORDER BY id) AS rn
    FROM productos
    WHERE eliminado = 0
)
SELECT pedido_id, producto_id, cantidad, precio_unitario, cantidad * precio_unitario
FROM (
    SELECT
        p.id AS pedido_id,
        pr.id AS producto_id,
        1 + FLOOR(RAND() * 5) AS cantidad,     -- cantidad entre 1 y 5 (respeta CHECK cantidad > 0)
        pr.precio AS precio_unitario
    FROM pedidos p
    JOIN chico c ON c.k <= (p.id % 4) + 1
    JOIN prods pr ON pr.rn = ((p.id * 7 + c.k) % (SELECT COUNT(*) FROM prods)) + 1
) AS base;


UPDATE pedidos p
JOIN (
    SELECT pedido_id, SUM(subtotal) AS total_calculado
    FROM detalles_pedidos
    WHERE eliminado = 0
    GROUP BY pedido_id
) d ON d.pedido_id = p.id
SET p.total = d.total_calculado;

SELECT 'usuarios' AS tabla, COUNT(*) AS total_registros FROM usuarios
UNION ALL
SELECT 'productos', COUNT(*) FROM productos
UNION ALL
SELECT 'pedidos', COUNT(*) FROM pedidos
UNION ALL
SELECT 'detalles_pedidos', COUNT(*) FROM detalles_pedidos;

SELECT COUNT(*) AS productos_con_categoria_invalida
FROM productos p LEFT JOIN categorias c ON c.id = p.categoria_id
WHERE c.id IS NULL;

SELECT COUNT(*) AS pedidos_con_usuario_invalido
FROM pedidos p LEFT JOIN usuarios u ON u.id = p.usuario_id
WHERE u.id IS NULL;

SELECT COUNT(*) AS detalles_con_pedido_invalido
FROM detalles_pedidos d LEFT JOIN pedidos p ON p.id = d.pedido_id
WHERE p.id IS NULL;

SELECT COUNT(*) AS detalles_con_producto_invalido
FROM detalles_pedidos d LEFT JOIN productos pr ON pr.id = d.producto_id
WHERE pr.id IS NULL;

SELECT COUNT(*) AS productos_con_categoria_invalida
FROM productos p LEFT JOIN categorias c ON c.id = p.categoria_id
WHERE c.id IS NULL;

SELECT COUNT(*) AS pedidos_con_usuario_invalido
FROM pedidos p LEFT JOIN usuarios u ON u.id = p.usuario_id
WHERE u.id IS NULL;

SELECT COUNT(*) AS detalles_con_pedido_invalido
FROM detalles_pedidos d LEFT JOIN pedidos p ON p.id = d.pedido_id
WHERE p.id IS NULL;

SELECT COUNT(*) AS detalles_con_producto_invalido
FROM detalles_pedidos d LEFT JOIN productos pr ON pr.id = d.producto_id
WHERE pr.id IS NULL;

SELECT COUNT(*) AS productos_fuera_de_rango FROM productos WHERE precio < 0 OR stock < 0;
SELECT COUNT(*) AS detalles_fuera_de_rango FROM detalles_pedidos WHERE cantidad <= 0 OR subtotal < 0;

4. 04_indices.sql (Indices de soporte para las consultas)

DELIMITER //
DROP PROCEDURE IF EXISTS crear_indice_si_no_existe //
CREATE PROCEDURE crear_indice_si_no_existe(
    IN p_tabla VARCHAR(64),
    IN p_indice VARCHAR(64),
    IN p_definicion VARCHAR(255)
)
BEGIN
    DECLARE v_existe INT;

    SELECT COUNT(*) INTO v_existe
    FROM information_schema.statistics
    WHERE table_schema = DATABASE()
      AND table_name = p_tabla
      AND index_name = p_indice;

    IF v_existe = 0 THEN
        SET @ddl = CONCAT('CREATE INDEX ', p_indice, ' ON ', p_tabla, ' (', p_definicion, ')');
        PREPARE stmt FROM @ddl;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
    END IF;
END //
DELIMITER ;

CALL crear_indice_si_no_existe('productos', 'idx_productos_categoria_id', 'categoria_id');
CALL crear_indice_si_no_existe('pedidos', 'idx_pedidos_usuario_id', 'usuario_id');
CALL crear_indice_si_no_existe('detalles_pedidos', 'idx_detalles_pedido_id', 'pedido_id');
CALL crear_indice_si_no_existe('detalles_pedidos', 'idx_detalles_producto_id', 'producto_id');

CALL crear_indice_si_no_existe('pedidos', 'idx_pedidos_estado', 'estado');

CALL crear_indice_si_no_existe('pedidos', 'idx_pedidos_created_at', 'created_at');

CALL crear_indice_si_no_existe('productos', 'idx_productos_categoria_eliminado', 'categoria_id, eliminado');

SELECT table_name, index_name, GROUP_CONCAT(column_name ORDER BY seq_in_index) AS columnas
FROM information_schema.statistics
WHERE table_schema = DATABASE()
  AND table_name IN ('categorias', 'usuarios', 'productos', 'pedidos', 'detalles_pedidos')
GROUP BY table_name, index_name
ORDER BY table_name, index_name;

5. 05_consultas.sql (Consultas con JOIN, GROUP BY, subconjunto)

SELECT p.id as producto_id, p.nombre as producto, SUM(dp.cantidad) as cantidad_vendida, c.nombre as categoria
FROM productos p
INNER JOIN categorias c ON p.categoria_id = c.id
INNER JOIN detalles_pedidos dp ON dp.producto_id = p.id
group by p.id, p.nombre, c.nombre
ORDER BY cantidad_vendida DESC;

SELECT u.nombre as usuario,
po.nombre as nombre_producto, 
p.estado as estado_pedido, 
p.forma_pago as forma_pago,
dp.cantidad as cantidad,
dp.precio_unitario as precio
FROM pedidos p 
inner JOIN usuarios u ON p.usuario_id = u.id
inner JOIN detalles_pedidos dp ON dp.pedido_id = p.id
inner JOIN productos po on dp.producto_id = po.id
order by u.nombre;

SELECT u.id, 
u.nombre as Nombre,
SUM(p.total) as total_facturado
from usuarios u
join pedidos p on u.id = p.usuario_id
where p.estado in ('CONFIRMADO', 'TERMINADO')
group by u.id, u.nombre
having total_facturado > 1000
order by total_facturado desc;

select id, nombre, stock
from productos
where stock < (select AVG(stock) from productos)
order by stock asc;

5. 05_explain.sql (Analisis de rendimiento)

EXPLAIN ANALYZE
SELECT u.nombre, SUM(p.total) 
FROM usuarios u 
JOIN pedidos p ON u.id = p.usuario_id 
WHERE p.estado = 'TERMINADO'
GROUP BY u.nombre;

CREATE INDEX idx_pedidos_estado ON pedidos(estado);

EXPLAIN ANALYZE
SELECT u.nombre, SUM(p.total) 
FROM usuarios u 
JOIN pedidos p FORCE INDEX (idx_pedidos_estado) ON u.id = p.usuario_id 
WHERE p.estado = 'TERMINADO'
GROUP BY u.nombre;

EXPLAIN ANALYZE
SELECT * FROM vista_pedidos_detallados WHERE email_cliente = 'usuario.demo500@foodstore-demo.com';

CREATE INDEX idx_usuarios_email ON usuarios(email);

EXPLAIN ANALYZE
SELECT * FROM vista_pedidos_detallados WHERE email_cliente = 'usuario.demo500@foodstore-demo.com';

EXPLAIN ANALYZE
SELECT * FROM productos WHERE stock < 10;

CREATE INDEX idx_productos_stock ON productos(stock);

EXPLAIN ANALYZE
SELECT * FROM productos FORCE INDEX (idx_productos_stock) WHERE stock < 10;

6. 06_vistas.sql (Creacion de vistas del sistema)

create or replace view vista_pedidos_detallados as
select p.id,
u.nombre as nombre_cliente,
u.email as email_cliente,
p.forma_pago,
p.estado as estado_pedido,
p.total as total_pedido,
p.created_at as fecha_pedido,
dp.cantidad,
dp.precio_unitario,
dp.subtotal as suptotal_detalle
from pedidos p
join usuarios u on p.usuario_id = u.id
join detalles_pedidos dp on p.id = dp.pedido_id
join productos pro on dp.producto_id = pro.id

7. 07_seguridad.sql (Usuarios)

8. 08_transacciones.sql (Transacciones manejo de errores y retry ante deadLock)

DROP TABLE IF EXISTS log_transacciones;
DROP PROCEDURE IF EXISTS sp_confirmar_pedido;

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

SET @pedido_test_ok = (SELECT id FROM pedidos WHERE estado = 'PENDIENTE' LIMIT 1);
CALL sp_confirmar_pedido(@pedido_test_ok);

SELECT * FROM log_transacciones WHERE pedido_id = @pedido_test_ok ORDER BY id DESC;
SELECT id, estado FROM pedidos WHERE id = @pedido_test_ok;


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

CALL sp_confirmar_pedido(@pedido_test_fail);

SELECT * FROM log_transacciones WHERE pedido_id = @pedido_test_fail ORDER BY id DESC;
SELECT id, estado FROM pedidos WHERE id = @pedido_test_fail; -- debe seguir PENDIENTE (ROLLBACK)

9. 09_concurrencia_guiada.sql (Simulacion guiada de concurrencia)

SELECT id, nombre, stock
FROM productos
WHERE stock > 50
ORDER BY id
LIMIT 4;

START TRANSACTION;
UPDATE productos SET stock = stock - 1 WHERE id = <<PRODUCTO_A>>;

START TRANSACTION;
UPDATE productos SET stock = stock - 1 WHERE id = <<PRODUCTO_B>>;

UPDATE productos SET stock = stock - 1 WHERE id = <<PRODUCTO_B>>;

UPDATE productos SET stock = stock - 1 WHERE id = <<PRODUCTO_A>>;

COMMIT;

SET SESSION TRANSACTION ISOLATION LEVEL REPEATABLE READ;
START TRANSACTION;
SELECT stock FROM productos WHERE id = <<PRODUCTO_C>>;

START TRANSACTION;
UPDATE productos SET stock = stock - 50 WHERE id = <<PRODUCTO_C>>;
COMMIT;

SELECT stock FROM productos WHERE id = <<PRODUCTO_C>>;

COMMIT;
SELECT stock FROM productos WHERE id = <<PRODUCTO_C>>;

SET SESSION TRANSACTION ISOLATION LEVEL READ COMMITTED;
START TRANSACTION;
SELECT stock FROM productos WHERE id = <<PRODUCTO_D>>;

START TRANSACTION;
UPDATE productos SET stock = stock - 50 WHERE id = <<PRODUCTO_D>>;
COMMIT;

SELECT stock FROM productos WHERE id = <<PRODUCTO_D>>;

COMMIT;

SELECT dp1.pedido_id AS pedido_x, dp2.pedido_id AS pedido_y,
       dp1.producto_id AS producto_comun
FROM detalles_pedidos dp1
JOIN detalles_pedidos dp2
  ON dp1.producto_id = dp2.producto_id AND dp1.pedido_id < dp2.pedido_id
JOIN pedidos px ON px.id = dp1.pedido_id AND px.estado = 'PENDIENTE'
JOIN pedidos py ON py.id = dp2.pedido_id AND py.estado = 'PENDIENTE'
LIMIT 5;

SELECT * FROM log_transacciones ORDER BY id DESC LIMIT 10;





