use food_store;

-- Sobre la consulta 3: Filtrando por estado del pedido

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

-- SE REALIZA CONSULTA DE LA VISTA, TENIENDO COMO CONDICION EL EMAIL

EXPLAIN ANALYZE
SELECT * FROM vista_pedidos_detallados WHERE email_cliente = 'usuario.demo500@foodstore-demo.com';

CREATE INDEX idx_usuarios_email ON usuarios(email);

EXPLAIN ANALYZE
SELECT * FROM vista_pedidos_detallados WHERE email_cliente = 'usuario.demo500@foodstore-demo.com';

-- Sobre la consulta 4, filtar el stock

EXPLAIN ANALYZE
SELECT * FROM productos WHERE stock < 10;

CREATE INDEX idx_productos_stock ON productos(stock);

EXPLAIN ANALYZE
SELECT * FROM productos FORCE INDEX (idx_productos_stock) WHERE stock < 10;
