-- =============================================================================
-- TRABAJO FINAL INTEGRADOR: Bases de Datos I + Programación II
-- SISTEMA: Food Store (Gestión de Pedidos de Comida)
-- SCRIPT: 04_indices.sql (Índices de soporte para las consultas de Etapa 3)
-- INTEGRANTE 2: Etapa 2 - Datos masivos
-- =============================================================================
-- Idempotente: MySQL no soporta "DROP INDEX IF EXISTS", por lo que cada índice
-- se crea de forma condicional consultando information_schema.statistics antes
-- de ejecutar el CREATE INDEX correspondiente, vía SQL dinámico (PREPARE/EXECUTE).
-- Requisito: ejecutar después de 01_esquema.sql (y opcionalmente 02_catalogo.sql /
-- 03_carga_masiva.sql, aunque los índices pueden crearse en cualquier momento).
-- =============================================================================

USE food_store;

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

-- =============================================================================
-- Índices sobre claves foráneas (joins frecuentes en Etapa 3)
-- =============================================================================
CALL crear_indice_si_no_existe('productos', 'idx_productos_categoria_id', 'categoria_id');
CALL crear_indice_si_no_existe('pedidos', 'idx_pedidos_usuario_id', 'usuario_id');
CALL crear_indice_si_no_existe('detalles_pedidos', 'idx_detalles_pedido_id', 'pedido_id');
CALL crear_indice_si_no_existe('detalles_pedidos', 'idx_detalles_producto_id', 'producto_id');

-- =============================================================================
-- Índices sobre campos de filtrado frecuente
-- =============================================================================
-- 'estado' tiene baja cardinalidad (solo 4 valores posibles repartidos en miles
-- de filas), por lo que un índice simple sobre esa columna sola "no aporta"
-- (Unidad 6 - Gestión de SGBD: un índice sobre una columna con pocos valores
-- únicos no filtra lo suficiente como para justificar su costo de mantenimiento).
-- En cambio, sí es útil como índice COMPUESTO junto con 'created_at', para el
-- patrón real "listar pedidos en estado X ordenados/filtrados por fecha"
-- (ej. pedidos PENDIENTES más recientes primero), donde la columna de baja
-- cardinalidad filtra un poco y created_at aporta el orden/rango.
CALL crear_indice_si_no_existe('pedidos', 'idx_pedidos_estado_created_at', 'estado, created_at');

-- Filtrado por fecha de creación sin filtro de estado (reportes por período)
CALL crear_indice_si_no_existe('pedidos', 'idx_pedidos_created_at', 'created_at');

-- Índice compuesto para el patrón "productos activos por categoría" (HU-PROD-01)
CALL crear_indice_si_no_existe('productos', 'idx_productos_categoria_eliminado', 'categoria_id, eliminado');

DROP PROCEDURE IF EXISTS crear_indice_si_no_existe;

-- =============================================================================
-- Verificación: listar los índices creados sobre las tablas del dominio
-- =============================================================================
SELECT table_name, index_name, GROUP_CONCAT(column_name ORDER BY seq_in_index) AS columnas
FROM information_schema.statistics
WHERE table_schema = DATABASE()
  AND table_name IN ('categorias', 'usuarios', 'productos', 'pedidos', 'detalles_pedidos')
GROUP BY table_name, index_name
ORDER BY table_name, index_name;
