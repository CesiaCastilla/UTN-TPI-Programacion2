-- =============================================================================
-- TRABAJO FINAL INTEGRADOR: Bases de Datos I + Programación II
-- SISTEMA: Food Store (Gestión de Pedidos de Comida)
-- SCRIPT: 09_concurrencia_guiada.sql (Simulación guiada de concurrencia)
-- INTEGRANTE 2: Etapa 5 - Concurrencia y transacciones
-- =============================================================================
-- Este script NO se ejecuta de punta a punta en una sola conexión: es un
-- GUION para correr a mano en DOS sesiones de MySQL en paralelo (dos pestañas
-- de MySQL Workbench, o dos terminales con `mysql -u ... -p`), respetando el
-- orden de los pasos indicado entre paréntesis: (A1), (B1), (A2), (B2), etc.
-- Requisito: ejecutar después de 01_esquema.sql, 02_catalogo.sql y
-- 03_carga_masiva.sql.
-- =============================================================================

USE food_store;

-- =============================================================================
-- PASO 0 (cualquier sesión) — Elegir 4 productos de prueba con stock alto
-- Anotar los 4 ids devueltos: se usan como <<PRODUCTO_A>>, <<PRODUCTO_B>>,
-- 3 y 6 en los pasos siguientes. Se piden con stock
-- alto para que ninguna prueba falle por quedarse sin stock antes de tiempo.
-- =============================================================================
SELECT id, nombre, stock
FROM productos
WHERE stock > 50
ORDER BY id
LIMIT 4;

-- =============================================================================
-- PARTE 1 — Simulación de un deadlock real con dos sesiones
-- Idea: cada sesión toma el lock de una fila y después intenta tomar la fila
-- que ya tiene la otra, en orden cruzado. MySQL/InnoDB detecta el ciclo de
-- espera y aborta una de las dos transacciones con el error de deadlock.
-- =============================================================================

-- (A1) SESIÓN A — abre transacción y bloquea PRODUCTO_A
START TRANSACTION;
UPDATE productos SET stock = stock - 1 WHERE id = 1;

-- (B1) SESIÓN B — abre transacción y bloquea PRODUCTO_B
START TRANSACTION;
UPDATE productos SET stock = stock - 1 WHERE id = 2;

-- (A2) SESIÓN A — intenta tomar PRODUCTO_B (ya lo tiene B): queda esperando
UPDATE productos SET stock = stock - 1 WHERE id = 2;

-- (B2) SESIÓN B — intenta tomar PRODUCTO_A (ya lo tiene A): ciclo de espera
-- cerrado. MySQL detecta el deadlock y aborta una de las dos transacciones.
-- Resultado esperado en UNA de las dos sesiones (la víctima elegida por
-- InnoDB, no siempre es la misma):
--   ERROR 1213 (40001): Deadlock found when trying to get lock; try
--   restarting transaction
-- La transacción víctima queda automáticamente revertida por el servidor.
UPDATE productos SET stock = stock - 1 WHERE id = 1;

-- (A3 / B3) La sesión que NO recibió el error 1213 sigue con su transacción
-- abierta y ya tiene ambos locks: debe cerrarla explícitamente.
COMMIT;   -- o ROLLBACK, según lo que se quiera dejar registrado en la prueba

-- (A4 / B4 — en la sesión víctima del error 1213, por las dudas)
-- ROLLBACK; -- normalmente no hace falta: el deadlock ya revirtió la transacción

-- =============================================================================
-- PARTE 2 — Comparación de niveles de aislamiento
-- READ COMMITTED vs REPEATABLE READ, con un ejemplo simple y observable:
-- una sesión lee dos veces el mismo dato dentro de la misma transacción
-- mientras la otra sesión modifica y confirma ese dato en el medio.
-- =============================================================================

-- --- 2.a) REPEATABLE READ (nivel por defecto de InnoDB) ---------------------

-- (A1) SESIÓN A
SET SESSION TRANSACTION ISOLATION LEVEL REPEATABLE READ;
START TRANSACTION;
SELECT stock FROM productos WHERE id = 3;
-- Anotar el valor devuelto (ej. 120)

-- (B1) SESIÓN B — modifica y confirma el mismo producto
START TRANSACTION;
UPDATE productos SET stock = stock - 50 WHERE id = 3;
COMMIT;

-- (A2) SESIÓN A — repite la MISMA lectura, dentro de la MISMA transacción
SELECT stock FROM productos WHERE id = 3;
-- Resultado esperado: el MISMO valor que en (A1), aunque B ya hizo commit del
-- cambio. REPEATABLE READ mantiene una foto (snapshot) consistente de los
-- datos durante toda la transacción.

-- (A3) SESIÓN A — cierra la transacción y vuelve a leer
COMMIT;
SELECT stock FROM productos WHERE id = 3;
-- Ahora sí se ve el valor actualizado por B (la transacción de A ya terminó).

-- --- 2.b) READ COMMITTED ----------------------------------------------------

-- (A1) SESIÓN A
SET SESSION TRANSACTION ISOLATION LEVEL READ COMMITTED;
START TRANSACTION;
SELECT stock FROM productos WHERE id = 6;
-- Anotar el valor devuelto (ej. 80)

-- (B1) SESIÓN B — modifica y confirma el mismo producto
START TRANSACTION;
UPDATE productos SET stock = stock - 50 WHERE id = 6;
COMMIT;

-- (A2) SESIÓN A — repite la MISMA lectura, dentro de la MISMA transacción
SELECT stock FROM productos WHERE id = 6;
-- Resultado esperado: a diferencia de REPEATABLE READ, acá SÍ se ve el valor
-- ya actualizado por B, porque READ COMMITTED lee el último dato confirmado
-- en cada SELECT, sin mantener una foto fija de toda la transacción.

-- (A3) SESIÓN A — cierra la transacción
COMMIT;

-- Documentar acá los valores reales obtenidos en 2.a y 2.b en el informe breve
-- de 5-10 líneas (ver Anexo IA - Etapa 5 en el PDF final).

-- =============================================================================
-- PARTE 3 (opcional / integradora) — Retry automático ante deadlock
-- Usa sp_confirmar_pedido (08_transacciones.sql) desde dos sesiones para ver
-- el mecanismo de reintento en acción. Requiere dos pedidos PENDIENTES que
-- compartan productos en orden cruzado para forzar el deadlock; buscarlos con
-- la consulta de abajo antes de correr (A1)/(B1).
-- =============================================================================
SELECT dp1.pedido_id AS pedido_x, dp2.pedido_id AS pedido_y,
       dp1.producto_id AS producto_comun
FROM detalles_pedidos dp1
JOIN detalles_pedidos dp2
  ON dp1.producto_id = dp2.producto_id AND dp1.pedido_id < dp2.pedido_id
JOIN pedidos px ON px.id = dp1.pedido_id AND px.estado = 'PENDIENTE'
JOIN pedidos py ON py.id = dp2.pedido_id AND py.estado = 'PENDIENTE'
LIMIT 5;

-- (A1) SESIÓN A
-- CALL sp_confirmar_pedido(<<pedido_x>>);

-- (B1) SESIÓN B, lanzado casi al mismo tiempo que (A1)
-- CALL sp_confirmar_pedido(<<pedido_y>>);

-- Revisar el resultado en ambas sesiones y el detalle en log_transacciones:
SELECT * FROM log_transacciones ORDER BY id DESC LIMIT 10;
