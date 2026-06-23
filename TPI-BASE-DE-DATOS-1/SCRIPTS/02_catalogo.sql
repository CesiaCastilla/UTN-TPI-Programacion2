-- =============================================================================
-- SCRIPT: 02_catalogos.sql (Datos Semilla Iniciales)
-- INTEGRANTE 1: Carga Base para Pruebas
-- =============================================================================

USE food_store;

-- Insertar categorías base para que tus compañeros tengan productos que asociar
INSERT INTO categorias (nombre) VALUES 
('Hamburguesas'),
('Pizzas y Empanadas'),
('Bebidas'),
('Postres');

-- Insertar usuarios base (respetando el CHECK de roles: ADMIN o USUARIO)
INSERT INTO usuarios (nombre, email, rol) VALUES 
('Carlos Admin', 'admin@foodstore.com', 'ADMIN'),
('Juan Perez', 'juan.perez@gmail.com', 'USUARIO'),
('Ana Gomez', 'ana.gomez@gmail.com', 'USUARIO');