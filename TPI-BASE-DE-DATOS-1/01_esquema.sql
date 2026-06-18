-- =============================================================================
-- TRABAJO FINAL INTEGRADOR: Bases de Datos I + Programación II
-- SISTEMA: Food Store (Gestión de Pedidos de Comida)
-- SCRIPT: 01_esquema.sql (Creación de Base de Datos y Tablas)
-- INTEGRANTE 1: Setup y Estructura Base
-- =============================================================================

-- 1. Creación y uso de la Base de Datos
CREATE DATABASE IF NOT EXISTS food_store;
USE food_store;

-- 2. Eliminación de tablas en orden inverso de dependencias (Garantiza Idempotencia)
DROP TABLE IF EXISTS detalles_pedidos;
DROP TABLE IF EXISTS pedidos;
DROP TABLE IF EXISTS productos;
DROP TABLE IF EXISTS usuarios;
DROP TABLE IF EXISTS categorias;

-- =============================================================================
-- TABLA: categorias (Módulo Integrante 1)
-- =============================================================================
CREATE TABLE categorias (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE,
    -- Campos heredados de la clase Base (Java)
    eliminado TINYINT(1) NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- =============================================================================
-- TABLA: usuarios (Módulo Integrante 2)
-- =============================================================================
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

-- =============================================================================
-- TABLA: productos (Módulo Integrante 2)
-- =============================================================================
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

-- =============================================================================
-- TABLA: pedidos (Módulo Integrante 3)
-- =============================================================================
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

-- =============================================================================
-- TABLA: detalles_pedidos (Módulo Integrante 3)
-- =============================================================================
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