use food_store;

-- CONSULTA 1: LISTADO DE LOS PRODUCTOS MAS VENDIDOS POR CATEGORIA.
-- La utilidad de esta consulta seria tener en cuenta cuales son los productos
-- que tienen alta rotacion para las tomas de decisiones de negociaciones comerciales.
SELECT p.id as producto_id, p.nombre as producto, SUM(dp.cantidad) as cantidad_vendida, c.nombre as categoria
FROM productos p
INNER JOIN categorias c ON p.categoria_id = c.id
INNER JOIN detalles_pedidos dp ON dp.producto_id = p.id
group by p.id, p.nombre, c.nombre
ORDER BY cantidad_vendida DESC;

-- CONSULTA 2: LISTAR LOS PEDIDOS POR USUARIO CON SU DETALLE
-- La utilidad de esta consulta es mostrar como se interactuan las tablas principales
-- para la empresa le da valor si necesitan saber si los usuarios que con mayor frecuencia consumen el producto
-- y asi poder brindar algun beneficio o valor agregado a la empresa.

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

-- CONSULTA 3: LISTAR LOS CLIENTES CON MAS FRECUENCIA Y ALTA FACTURACION (MAYOR A 100)
-- Esta consulta se relaciona a la anterior pero ahora teniendo en cuenta no solo los que 
-- que tinene mayor frecuencia si no tambien los que tienen mayor facturacion, especificando
-- un monton minimo.

SELECT u.id, 
u.nombre as Nombre,
SUM(p.total) as total_facturado
from usuarios u
join pedidos p on u.id = p.usuario_id
where p.estado in ('CONFIRMADO', 'TERMINADO')
group by u.id, u.nombre
having total_facturado > 1000
order by total_facturado desc;

-- CONSULTA 4: LISTADO DE PRODUCTOS QUE TIENEN EL STOCK POR DEBAJO DEL PROMEDIO.
-- Esta cosulta nos ayuda a observaar cuales son aquellos productos que necesita
-- reponer su stock teniendo en cuenta el promedio de lo que se utiliza.
select id, nombre, stock
from productos
where stock < (select AVG(stock) from productos)
order by stock asc;

