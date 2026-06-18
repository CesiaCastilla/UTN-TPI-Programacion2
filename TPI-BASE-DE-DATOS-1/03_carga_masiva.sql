-- =============================================================================
-- TRABAJO FINAL INTEGRADOR: Bases de Datos I + Programación II
-- SISTEMA: Food Store (Gestión de Pedidos de Comida)
-- SCRIPT: 03_carga_masiva.sql (Generación y carga de datos masivos)
-- INTEGRANTE 2: Etapa 2 - Datos masivos
-- =============================================================================
-- Mecanismo: CTE recursivas (WITH RECURSIVE) para generar series numéricas +
-- ROW_NUMBER() para distribuir esas series de forma uniforme sobre las claves
-- foráneas existentes (categorias, usuarios, productos), evitando así FKs huérfanas.
-- Requisito: ejecutar después de 01_esquema.sql y 02_catalogo.sql.
-- Idempotente: al reejecutarse sobre un esquema recién creado (DROP+CREATE en
-- 01_esquema.sql) no quedan duplicados; si se reejecuta sobre datos ya cargados,
-- se debe volver a correr 01_esquema.sql primero para reiniciar las tablas.
-- =============================================================================

USE food_store;

-- Aumentamos el límite de recursión por defecto (1000) para poder generar
-- series numéricas más largas con WITH RECURSIVE.
SET SESSION cte_max_recursion_depth = 20000;

-- =============================================================================
-- 1. USUARIOS (1000 registros adicionales a los 3 de 02_catalogo.sql)
-- =============================================================================
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

-- =============================================================================
-- 2. PRODUCTOS (1000 registros), distribuidos entre las categorías existentes
-- =============================================================================
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

-- =============================================================================
-- 3. PEDIDOS (4000 registros), distribuidos entre los usuarios existentes
-- =============================================================================
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

-- =============================================================================
-- 4. DETALLES DE PEDIDOS (entre 1 y 4 por pedido, ~10.000 en total)
-- Cardinalidad mínima garantizada: cada pedido recibe al menos 1 detalle
-- mediante la condición c.k <= (p.id % 4) + 1.
-- =============================================================================
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

-- =============================================================================
-- 5. Recalcular el total de cada pedido en base a la suma de sus detalles
-- =============================================================================
UPDATE pedidos p
JOIN (
    SELECT pedido_id, SUM(subtotal) AS total_calculado
    FROM detalles_pedidos
    WHERE eliminado = 0
    GROUP BY pedido_id
) d ON d.pedido_id = p.id
SET p.total = d.total_calculado;

-- =============================================================================
-- 6. Cuadro de verificaciones (conteos, FKs huérfanas, rangos de dominio)
-- Ver descripción completa en TPI_PROGRAMACION_DB/Etapa2_Descripcion_Conceptual.md
-- =============================================================================
SELECT 'usuarios' AS tabla, COUNT(*) AS total_registros FROM usuarios
UNION ALL
SELECT 'productos', COUNT(*) FROM productos
UNION ALL
SELECT 'pedidos', COUNT(*) FROM pedidos
UNION ALL
SELECT 'detalles_pedidos', COUNT(*) FROM detalles_pedidos;

-- FKs huérfanas esperadas en 0
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

-- Cardinalidad mínima: pedidos sin ningún detalle (esperado: 0)
SELECT COUNT(*) AS pedidos_sin_detalles
FROM pedidos p
LEFT JOIN detalles_pedidos d ON d.pedido_id = p.id
WHERE d.id IS NULL;

-- Rangos de dominio fuera de lo esperado (esperado: 0 en todos los casos)
SELECT COUNT(*) AS productos_fuera_de_rango FROM productos WHERE precio < 0 OR stock < 0;
SELECT COUNT(*) AS detalles_fuera_de_rango FROM detalles_pedidos WHERE cantidad <= 0 OR subtotal < 0;
