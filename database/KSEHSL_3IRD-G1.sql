CREATE DATABASE IF NOT EXISTS inventario;
USE inventario;

DROP TABLE IF EXISTS productos;
DROP TABLE IF EXISTS categorias;

CREATE TABLE categorias (
  id INT PRIMARY KEY AUTO_INCREMENT,
  nombre VARCHAR(150) NOT NULL UNIQUE,
  descripcion TEXT
);

CREATE TABLE productos (
  id INT PRIMARY KEY AUTO_INCREMENT,
  sku VARCHAR(50) NOT NULL UNIQUE,
  codigo_barras VARCHAR(100) UNIQUE,
  nombre VARCHAR(150) NOT NULL,
  precio DOUBLE NOT NULL CHECK(precio >= 0),
  existencia INT NOT NULL CHECK(existencia >= 0),
  ubicacion VARCHAR(100),
  foto VARCHAR(500),
  categoria_id INT NOT NULL,
  FOREIGN KEY (categoria_id) REFERENCES categorias(id) ON DELETE RESTRICT
);

INSERT INTO categorias(nombre, descripcion) VALUES
('Papelería', 'Materiales escolares y de oficina'),
('Tecnología', 'Accesorios y equipos electrónicos'),
('Limpieza', 'Productos para higiene y limpieza');

INSERT INTO productos(sku, codigo_barras, nombre, precio, existencia, ubicacion, foto, categoria_id) VALUES
('PAP-0001', '7501000000011', 'Cuaderno profesional', 58.50, 25, 'A1-E2-N3', NULL, 1),
('TEC-0001', '7501000000028', 'Memoria USB 64 GB', 189.00, 4, 'B2-E1-N1', NULL, 2),
('LIM-0001', '7501000000035', 'Desinfectante 1 L', 42.00, 0, 'C1-E3-N2', NULL, 3);
