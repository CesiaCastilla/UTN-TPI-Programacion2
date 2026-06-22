-- =====================================================================
-- TRABAJO FINAL INTEGRADOR: BASES DE DATOS I (UTN)
-- SCRIPT: 07_seguridad.sql
-- RESPONSABLE: Alfredo
-- ETAPA 4: SEGURIDAD E INTEGRIDAD (Esquema Real: food_store)
-- =====================================================================

USE food_store; -- Nombre exacto de tu base de datos

-- =====================================================================
-- 1. USUARIO CON PRIVILEGIOS MÍNIMOS
-- =====================================================================
DROP USER IF EXISTS 'operador_foodstore'@'localhost';

-- Creamos el usuario del sistema con una contraseña segura
CREATE USER 'operador_foodstore'@'localhost' IDENTIFIED BY 'FoodStore2026!Secure';

-- Muestra inicial de permisos (Evidencia que arranca con privilegios CERO: 'GRANT USAGE')
SHOW GRANTS FOR 'operador_foodstore'@'localhost';


-- =====================================================================
-- 2. VISTAS PARA OCULTAR INFORMACIÓN SENSIBLE
-- =====================================================================
-- Vista 1: Usuarios públicos (Oculta campos críticos si los hubiera, muestra estructura básica)
CREATE OR REPLACE VIEW vw_usuarios_publico AS
SELECT id, nombre, email, rol
FROM usuarios
WHERE eliminado = 0;

-- Vista 2: Catálogo de productos público (Muestra solo lo disponible y oculta flags)
CREATE OR REPLACE VIEW vw_productos_disponibles AS
SELECT id, nombre, precio, stock, categoria_id
FROM productos
WHERE eliminado = 0 AND stock > 0;


-- =====================================================================
-- 3. ASIGNACIÓN DE PRIVILEGIOS ACOTADOS
-- =====================================================================
-- Otorgamos permisos únicamente sobre las vistas seguras
GRANT SELECT ON food_store.vw_usuarios_publico TO 'operador_foodstore'@'localhost';
GRANT SELECT ON food_store.vw_productos_disponibles TO 'operador_foodstore'@'localhost';

FLUSH PRIVILEGES;

-- Verificación de permisos otorgados (Aquí se verá el acceso restringido a las vistas)
SHOW GRANTS FOR 'operador_foodstore'@'localhost';


-- =====================================================================
-- 4. PROCEDIMIENTO ALMACENADO SEGURO (ANTI SQL-INJECTION)
-- =====================================================================
DROP PROCEDURE IF EXISTS sp_buscar_producto_seguro;

DELIMITER //

CREATE PROCEDURE sp_buscar_producto_seguro (
    IN p_nombre_buscar VARCHAR(100)
)
BEGIN
    -- Consulta parametrizada utilizando la vista segura y columnas reales (id)
    SELECT id, nombre, precio, stock 
    FROM food_store.vw_productos_disponibles
    WHERE nombre LIKE CONCAT('%', p_nombre_buscar, '%');
END //

DELIMITER ;

-- Concedemos permiso de ejecución al usuario operador
GRANT EXECUTE ON PROCEDURE food_store.sp_buscar_producto_seguro TO 'operador_foodstore'@'localhost';
FLUSH PRIVILEGES;


-- =====================================================================
-- 5. BANCO DE PRUEBAS PARA CAPTURAS DE PANTALLA (EVIDENCIAS CORREGIDAS)
-- =====================================================================

-- [CAPTURA A] PRUEBA DE INTEGRIDAD: Violación de Clave Primaria (PK Duplicada)
-- (Debe fallar con Error 1062: Duplicate entry '1' for key 'PRIMARY')
INSERT INTO categorias (id, nombre, eliminado) VALUES (1, 'Bebidas Nuevas', 0);


-- [CAPTURA B] PRUEBA DE INTEGRIDAD: Violación de Clave Foránea (FK Inexistente)
-- (Debe fallar con Error 1452: Cannot add or update a child row: a foreign key constraint fails)
INSERT INTO productos (nombre, precio, stock, categoria_id, eliminado) VALUES ('Producto Fantasma', 500.00, 10, 999, 0);


-- [CAPTURA C] PRUEBA DE INTEGRIDAD: Violación de Restricción CHECK (Precio Negativo)
-- (Debe fallar con Error 3819: Check constraint 'chk_producto_precio' is violated)
INSERT INTO productos (nombre, precio, stock, categoria_id, eliminado) VALUES ('Producto Gratis', -100.00, 5, 1, 0);


-- [CAPTURA D] PRUEBA ANTI-INYECCIÓN DOCUMENTADA
-- (Esta da tilde VERDE y devuelve una grilla vacía de forma segura)
CALL sp_buscar_producto_seguro('\' OR \'1\'=\'1');