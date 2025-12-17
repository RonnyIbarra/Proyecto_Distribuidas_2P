-- V2__Insert_Initial_Roles.sql
-- Insert initial roles for LogiFlow

INSERT INTO roles (name, description, created_at, updated_at) VALUES
('CLIENTE', 'Cliente que realiza pedidos', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('REPARTIDOR', 'Repartidor que entrega pedidos', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('SUPERVISOR', 'Supervisor de zona', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('GERENTE', 'Gerente de operaciones', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('ADMIN', 'Administrador del sistema', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (name) DO NOTHING;
