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
('Limpieza', 'Productos para higiene y limpieza'),
('Alimentos', 'Comestibles y productos empacados'),
('Bebidas', 'Bebidas frías, calientes y energéticas'),
('Herramientas', 'Herramientas manuales y eléctricas'),
('Hogar', 'Artículos para cocina y hogar'),
('Salud', 'Cuidado personal y primeros auxilios'),
('Ropa', 'Prendas y accesorios de trabajo'),
('Refacciones', 'Piezas y consumibles de mantenimiento');

INSERT INTO productos(sku, codigo_barras, nombre, precio, existencia, ubicacion, foto, categoria_id) VALUES
('PAP-0001', '7501000000011', 'Cuaderno profesional', 58.50, 25, 'A1-E2-N3', NULL, 1),
('PAP-0002', '7501000000028', 'Paquete de hojas carta', 112.00, 8, 'A1-E1-N2', NULL, 1),
('PAP-0003', '7501000000035', 'Bolígrafo tinta azul', 9.50, 0, 'A1-E3-N1', NULL, 1),
('TEC-0001', '7501000000042', 'Memoria USB 64 GB', 189.00, 4, 'B1-E1-N1', NULL, 2),
('TEC-0002', '7501000000059', 'Teclado inalámbrico', 449.90, 14, 'B1-E2-N2', NULL, 2),
('TEC-0003', '7501000000066', 'Mouse óptico USB', 159.00, 7, 'B1-E2-N3', NULL, 2),
('LIM-0001', '7501000000073', 'Desinfectante 1 L', 42.00, 18, 'C1-E1-N1', NULL, 3),
('LIM-0002', '7501000000080', 'Jabón líquido 500 ml', 36.50, 3, 'C1-E1-N2', NULL, 3),
('LIM-0003', '7501000000097', 'Bolsa para basura grande', 74.90, 31, 'C1-E2-N1', NULL, 3),
('ALI-0001', '7501000000103', 'Arroz 1 kg', 34.00, 22, 'D1-E1-N1', NULL, 4),
('ALI-0002', '7501000000110', 'Atún en lata', 28.50, 9, 'D1-E1-N2', NULL, 4),
('ALI-0003', '7501000000127', 'Galletas integrales', 39.90, 0, 'D1-E2-N1', NULL, 4),
('BEB-0001', '7501000000134', 'Agua natural 1 L', 18.00, 48, 'D2-E1-N1', NULL, 5),
('BEB-0002', '7501000000141', 'Café soluble 200 g', 126.00, 6, 'D2-E2-N1', NULL, 5),
('BEB-0003', '7501000000158', 'Bebida energética 473 ml', 49.00, 2, 'D2-E1-N3', NULL, 5),
('HER-0001', '7501000000165', 'Martillo de uña 16 oz', 219.00, 12, 'E1-E1-N1', NULL, 6),
('HER-0002', '7501000000172', 'Juego de desarmadores', 329.00, 5, 'E1-E2-N1', NULL, 6),
('HER-0003', '7501000000189', 'Taladro inalámbrico', 1899.00, 1, 'E1-E3-N1', NULL, 6),
('HOG-0001', '7501000000196', 'Recipiente hermético', 84.50, 16, 'F1-E1-N1', NULL, 7),
('HOG-0002', '7501000000202', 'Sartén antiadherente', 399.00, 10, 'F1-E2-N1', NULL, 7),
('HOG-0003', '7501000000219', 'Foco LED 12 W', 55.00, 27, 'F1-E3-N1', NULL, 7),
('SAL-0001', '7501000000226', 'Botiquín básico', 289.00, 4, 'G1-E1-N1', NULL, 8),
('SAL-0002', '7501000000233', 'Gel antibacterial 250 ml', 49.50, 20, 'G1-E1-N2', NULL, 8),
('SAL-0003', '7501000000240', 'Cubrebocas paquete 20', 79.00, 0, 'G1-E2-N1', NULL, 8),
('ROP-0001', '7501000000257', 'Chaleco reflejante', 139.00, 11, 'H1-E1-N1', NULL, 9),
('ROP-0002', '7501000000264', 'Guantes de trabajo', 89.00, 5, 'H1-E1-N2', NULL, 9),
('ROP-0003', '7501000000271', 'Gorra de protección', 119.00, 17, 'H1-E2-N1', NULL, 9),
('REF-0001', '7501000000288', 'Cinta aislante negra', 24.00, 35, 'I1-E1-N1', NULL, 10),
('REF-0002', '7501000000295', 'Fusible automotriz 10 A', 15.00, 8, 'I1-E1-N2', NULL, 10),
('REF-0003', '7501000000301', 'Lubricante multiusos', 98.00, 2, 'I1-E2-N1', NULL, 10);
