-- Crea la tabla usuario con los campos
CREATE DATABASE integrador;

USE integrador;

CREATE TABLE usuario (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL
);

INSERT INTO usuario (username, password, role) VALUES
('admin@servimanef.com', 'admin123', 'ADMIN'),
('trabajador1@servimanef.com', 'trabajador123', 'TRABAJADOR'),
('contador1@servimanef.com', 'contador123', 'CONTADOR');

-- Crea la tabla pedido con los campos
CREATE TABLE pedido (
    id INT PRIMARY KEY AUTO_INCREMENT,
    cliente VARCHAR(100) NOT NULL,
    servicio VARCHAR(100) NOT NULL,
    telefono VARCHAR(20) NOT NULL,
    empresa VARCHAR(100) NOT NULL,
    ruc VARCHAR(20) NOT NULL
);

SELECT * FROM usuario;