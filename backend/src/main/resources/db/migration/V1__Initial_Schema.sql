-- V1__Initial_Schema.sql
-- Esquema inicial para LogiFlow (Fase 1)

-- Roles
CREATE TABLE IF NOT EXISTS roles (
  id BIGSERIAL PRIMARY KEY,
  nombre VARCHAR(50) UNIQUE NOT NULL,
  descripcion TEXT,
  fecha_creacion TIMESTAMP WITHOUT TIME ZONE DEFAULT now(),
  fecha_actualizacion TIMESTAMP WITHOUT TIME ZONE DEFAULT now()
);

-- Usuarios
CREATE TABLE IF NOT EXISTS users (
  id BIGSERIAL PRIMARY KEY,
  correo VARCHAR(255) UNIQUE NOT NULL,
  contrasena VARCHAR(255) NOT NULL,
  nombre_completo VARCHAR(255) NOT NULL,
  telefono_contacto VARCHAR(50),
  activo BOOLEAN DEFAULT TRUE,
  rol_id BIGINT NOT NULL REFERENCES roles(id),
  fecha_creacion TIMESTAMP WITHOUT TIME ZONE DEFAULT now(),
  fecha_actualizacion TIMESTAMP WITHOUT TIME ZONE DEFAULT now()
);

-- Repartidores (relación opcional con users)
CREATE TABLE IF NOT EXISTS repartidores (
  id BIGSERIAL PRIMARY KEY,
  usuario_id BIGINT NOT NULL REFERENCES users(id),
  licencia VARCHAR(100),
  estado VARCHAR(50) DEFAULT 'ACTIVO',
  fecha_creacion TIMESTAMP WITHOUT TIME ZONE DEFAULT now(),
  fecha_actualizacion TIMESTAMP WITHOUT TIME ZONE DEFAULT now()
);

-- Vehículos
CREATE TABLE IF NOT EXISTS vehicles (
  id BIGSERIAL PRIMARY KEY,
  placa VARCHAR(50) UNIQUE NOT NULL,
  tipo VARCHAR(50) NOT NULL,
  capacidad INTEGER NOT NULL,
  estado VARCHAR(50) NOT NULL DEFAULT 'DISPONIBLE',
  costo_por_km NUMERIC(12,2) NOT NULL,
  propietario_id BIGINT,
  fecha_creacion TIMESTAMP WITHOUT TIME ZONE DEFAULT now(),
  fecha_actualizacion TIMESTAMP WITHOUT TIME ZONE DEFAULT now()
);

-- Pedidos de entrega
CREATE TABLE IF NOT EXISTS delivery_orders (
  id BIGSERIAL PRIMARY KEY,
  numero_pedido VARCHAR(100) UNIQUE NOT NULL,
  id_cliente BIGINT NOT NULL,
  origen TEXT NOT NULL,
  destino TEXT NOT NULL,
  tipo_entrega VARCHAR(50) NOT NULL,
  peso INTEGER NOT NULL,
  estado VARCHAR(50) NOT NULL DEFAULT 'RECIBIDO',
  id_repartidor_asignado BIGINT,
  id_vehiculo_asignado BIGINT,
  zona VARCHAR(100),
  cost_estimado NUMERIC(12,2),
  notas TEXT,
  fecha_creacion TIMESTAMP WITHOUT TIME ZONE DEFAULT now(),
  fecha_actualizacion TIMESTAMP WITHOUT TIME ZONE DEFAULT now()
);

-- Facturas
CREATE TABLE IF NOT EXISTS invoices (
  id BIGSERIAL PRIMARY KEY,
  numero_factura VARCHAR(100) UNIQUE NOT NULL,
  id_pedido_entrega BIGINT NOT NULL,
  id_cliente BIGINT NOT NULL,
  estado VARCHAR(50) NOT NULL DEFAULT 'BORRADOR',
  subtotal NUMERIC(12,2) NOT NULL,
  impuesto NUMERIC(12,2) DEFAULT 0,
  total NUMERIC(12,2) NOT NULL,
  descripcion TEXT,
  fecha_creacion TIMESTAMP WITHOUT TIME ZONE DEFAULT now(),
  fecha_actualizacion TIMESTAMP WITHOUT TIME ZONE DEFAULT now()
);

-- Índices y constraints adicionales
CREATE INDEX IF NOT EXISTS idx_users_rol_id ON users(rol_id);
CREATE INDEX IF NOT EXISTS idx_vehicles_propietario_id ON vehicles(propietario_id);
CREATE INDEX IF NOT EXISTS idx_delivery_orders_cliente_id ON delivery_orders(id_cliente);
CREATE INDEX IF NOT EXISTS idx_delivery_orders_repartidor_id ON delivery_orders(id_repartidor_asignado);
CREATE INDEX IF NOT EXISTS idx_invoices_pedido_id ON invoices(id_pedido_entrega);

-- Insertar roles iniciales si no existen
INSERT INTO roles (nombre, descripcion, fecha_creacion, fecha_actualizacion)
VALUES
('CLIENTE', 'Cliente que realiza pedidos', now(), now()),
('REPARTIDOR', 'Repartidor que entrega pedidos', now(), now()),
('SUPERVISOR', 'Supervisor de zona', now(), now()),
('GERENTE', 'Gerente de operaciones', now(), now()),
('ADMIN', 'Administrador del sistema', now(), now())
ON CONFLICT (nombre) DO NOTHING;
-- V1__Initial_Schema.sql
-- Initial database schema for LogiFlow Phase 1

-- Create roles table
CREATE TABLE IF NOT EXISTS roles (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create users table
CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    phone_number VARCHAR(20) NOT NULL,
    active BOOLEAN DEFAULT true,
    role_id BIGINT NOT NULL REFERENCES roles(id),
    created_at BIGINT NOT NULL,
    updated_at BIGINT NOT NULL
);

-- Create vehicles table
CREATE TABLE IF NOT EXISTS vehicles (
    id SERIAL PRIMARY KEY,
    plate VARCHAR(20) NOT NULL UNIQUE,
    type VARCHAR(50) NOT NULL,
    capacity INTEGER NOT NULL,
    status VARCHAR(50) NOT NULL,
    base_cost_per_km NUMERIC(10, 2) NOT NULL,
    owner_id BIGINT NOT NULL,
    created_at BIGINT NOT NULL,
    updated_at BIGINT NOT NULL
);

-- Create delivery_orders table
CREATE TABLE IF NOT EXISTS delivery_orders (
    id SERIAL PRIMARY KEY,
    order_number VARCHAR(50) NOT NULL UNIQUE,
    customer_id BIGINT NOT NULL,
    origin VARCHAR(500) NOT NULL,
    destination VARCHAR(500) NOT NULL,
    delivery_type VARCHAR(50) NOT NULL,
    weight INTEGER NOT NULL,
    status VARCHAR(50) NOT NULL,
    assigned_rider_id BIGINT,
    assigned_vehicle_id BIGINT,
    zone VARCHAR(100) NOT NULL,
    estimated_cost NUMERIC(10, 2) NOT NULL,
    notes TEXT,
    created_at BIGINT NOT NULL,
    updated_at BIGINT NOT NULL
);

-- Create invoices table
CREATE TABLE IF NOT EXISTS invoices (
    id SERIAL PRIMARY KEY,
    invoice_number VARCHAR(50) NOT NULL UNIQUE,
    delivery_order_id BIGINT NOT NULL,
    customer_id BIGINT NOT NULL,
    status VARCHAR(50) NOT NULL,
    subtotal NUMERIC(10, 2) NOT NULL,
    tax NUMERIC(10, 2) NOT NULL,
    total NUMERIC(10, 2) NOT NULL,
    description TEXT,
    created_at BIGINT NOT NULL,
    updated_at BIGINT NOT NULL
);

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_role_id ON users(role_id);
CREATE INDEX IF NOT EXISTS idx_vehicles_owner_id ON vehicles(owner_id);
CREATE INDEX IF NOT EXISTS idx_vehicles_status ON vehicles(status);
CREATE INDEX IF NOT EXISTS idx_delivery_orders_customer_id ON delivery_orders(customer_id);
CREATE INDEX IF NOT EXISTS idx_delivery_orders_assigned_rider_id ON delivery_orders(assigned_rider_id);
CREATE INDEX IF NOT EXISTS idx_delivery_orders_zone ON delivery_orders(zone);
CREATE INDEX IF NOT EXISTS idx_delivery_orders_status ON delivery_orders(status);
CREATE INDEX IF NOT EXISTS idx_invoices_customer_id ON invoices(customer_id);
CREATE INDEX IF NOT EXISTS idx_invoices_delivery_order_id ON invoices(delivery_order_id);
