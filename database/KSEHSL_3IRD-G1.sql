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
  nombre VARCHAR(150) NOT NULL,
  precio DOUBLE NOT NULL CHECK(precio >= 0),
  existencia INT NOT NULL CHECK(existencia >= 0),
  categoria_id INT NOT NULL,
  FOREIGN KEY (categoria_id) REFERENCES categorias(id) ON DELETE RESTRICT
);

INSERT INTO categorias(nombre, descripcion) VALUES
('Papelería', 'Materiales escolares y de oficina'),
('Tecnología', 'Accesorios y equipos electrónicos'),
('Limpieza', 'Productos para higiene y limpieza');

INSERT INTO productos(nombre, precio, existencia, categoria_id) VALUES
('Cuaderno profesional', 58.50, 25, 1),
('Memoria USB 64 GB', 189.00, 10, 2),
('Desinfectante 1 L', 42.00, 18, 3);
