use food_store;

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


