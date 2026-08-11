package mx.edu.inventario.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/** Agrega un catálogo demostrativo sin modificar registros existentes. */
final class SampleDataSeeder {

    private static final CategoriaDemo[] CATEGORIAS = {
        new CategoriaDemo("Papelería", "Material escolar y de oficina"),
        new CategoriaDemo("Tecnología", "Equipos, periféricos y accesorios"),
        new CategoriaDemo("Limpieza", "Productos de higiene y limpieza"),
        new CategoriaDemo("Alimentos", "Comestibles y productos empacados"),
        new CategoriaDemo("Bebidas", "Bebidas frías, calientes y energéticas"),
        new CategoriaDemo("Herramientas", "Herramientas manuales y eléctricas"),
        new CategoriaDemo("Hogar", "Artículos para cocina y hogar"),
        new CategoriaDemo("Salud", "Cuidado personal y primeros auxilios"),
        new CategoriaDemo("Ropa", "Prendas y accesorios de trabajo"),
        new CategoriaDemo("Refacciones", "Piezas y consumibles de mantenimiento")
    };

    private static final ProductoDemo[] PRODUCTOS = {
        new ProductoDemo("PAP-0001", "7501000000011", "Cuaderno profesional", 58.50, 25, "A1-E2-N3", "Papelería"),
        new ProductoDemo("PAP-0002", "7501000000028", "Paquete de hojas carta", 112.00, 8, "A1-E1-N2", "Papelería"),
        new ProductoDemo("PAP-0003", "7501000000035", "Bolígrafo tinta azul", 9.50, 0, "A1-E3-N1", "Papelería"),
        new ProductoDemo("TEC-0001", "7501000000042", "Memoria USB 64 GB", 189.00, 4, "B1-E1-N1", "Tecnología"),
        new ProductoDemo("TEC-0002", "7501000000059", "Teclado inalámbrico", 449.90, 14, "B1-E2-N2", "Tecnología"),
        new ProductoDemo("TEC-0003", "7501000000066", "Mouse óptico USB", 159.00, 7, "B1-E2-N3", "Tecnología"),
        new ProductoDemo("LIM-0001", "7501000000073", "Desinfectante 1 L", 42.00, 18, "C1-E1-N1", "Limpieza"),
        new ProductoDemo("LIM-0002", "7501000000080", "Jabón líquido 500 ml", 36.50, 3, "C1-E1-N2", "Limpieza"),
        new ProductoDemo("LIM-0003", "7501000000097", "Bolsa para basura grande", 74.90, 31, "C1-E2-N1", "Limpieza"),
        new ProductoDemo("ALI-0001", "7501000000103", "Arroz 1 kg", 34.00, 22, "D1-E1-N1", "Alimentos"),
        new ProductoDemo("ALI-0002", "7501000000110", "Atún en lata", 28.50, 9, "D1-E1-N2", "Alimentos"),
        new ProductoDemo("ALI-0003", "7501000000127", "Galletas integrales", 39.90, 0, "D1-E2-N1", "Alimentos"),
        new ProductoDemo("BEB-0001", "7501000000134", "Agua natural 1 L", 18.00, 48, "D2-E1-N1", "Bebidas"),
        new ProductoDemo("BEB-0002", "7501000000141", "Café soluble 200 g", 126.00, 6, "D2-E2-N1", "Bebidas"),
        new ProductoDemo("BEB-0003", "7501000000158", "Bebida energética 473 ml", 49.00, 2, "D2-E1-N3", "Bebidas"),
        new ProductoDemo("HER-0001", "7501000000165", "Martillo de uña 16 oz", 219.00, 12, "E1-E1-N1", "Herramientas"),
        new ProductoDemo("HER-0002", "7501000000172", "Juego de desarmadores", 329.00, 5, "E1-E2-N1", "Herramientas"),
        new ProductoDemo("HER-0003", "7501000000189", "Taladro inalámbrico", 1899.00, 1, "E1-E3-N1", "Herramientas"),
        new ProductoDemo("HOG-0001", "7501000000196", "Recipiente hermético", 84.50, 16, "F1-E1-N1", "Hogar"),
        new ProductoDemo("HOG-0002", "7501000000202", "Sartén antiadherente", 399.00, 10, "F1-E2-N1", "Hogar"),
        new ProductoDemo("HOG-0003", "7501000000219", "Foco LED 12 W", 55.00, 27, "F1-E3-N1", "Hogar"),
        new ProductoDemo("SAL-0001", "7501000000226", "Botiquín básico", 289.00, 4, "G1-E1-N1", "Salud"),
        new ProductoDemo("SAL-0002", "7501000000233", "Gel antibacterial 250 ml", 49.50, 20, "G1-E1-N2", "Salud"),
        new ProductoDemo("SAL-0003", "7501000000240", "Cubrebocas paquete 20", 79.00, 0, "G1-E2-N1", "Salud"),
        new ProductoDemo("ROP-0001", "7501000000257", "Chaleco reflejante", 139.00, 11, "H1-E1-N1", "Ropa"),
        new ProductoDemo("ROP-0002", "7501000000264", "Guantes de trabajo", 89.00, 5, "H1-E1-N2", "Ropa"),
        new ProductoDemo("ROP-0003", "7501000000271", "Gorra de protección", 119.00, 17, "H1-E2-N1", "Ropa"),
        new ProductoDemo("REF-0001", "7501000000288", "Cinta aislante negra", 24.00, 35, "I1-E1-N1", "Refacciones"),
        new ProductoDemo("REF-0002", "7501000000295", "Fusible automotriz 10 A", 15.00, 8, "I1-E1-N2", "Refacciones"),
        new ProductoDemo("REF-0003", "7501000000301", "Lubricante multiusos", 98.00, 2, "I1-E2-N1", "Refacciones")
    };

    private SampleDataSeeder() {
    }

    static void insertar(Connection connection) throws SQLException {
        insertarCategorias(connection);
        insertarProductos(connection);
    }

    private static void insertarCategorias(Connection connection) throws SQLException {
        String sql = "INSERT IGNORE INTO categorias(nombre, descripcion) VALUES (?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            for (CategoriaDemo categoria : CATEGORIAS) {
                ps.setString(1, categoria.nombre());
                ps.setString(2, categoria.descripcion());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private static void insertarProductos(Connection connection) throws SQLException {
        String sql = """
                INSERT IGNORE INTO productos
                    (sku, codigo_barras, nombre, precio, existencia, ubicacion, foto, categoria_id)
                SELECT ?, ?, ?, ?, ?, ?, NULL, c.id
                FROM categorias c WHERE c.nombre = ?
                """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            for (ProductoDemo producto : PRODUCTOS) {
                ps.setString(1, producto.sku());
                ps.setString(2, producto.codigoBarras());
                ps.setString(3, producto.nombre());
                ps.setDouble(4, producto.precio());
                ps.setInt(5, producto.existencia());
                ps.setString(6, producto.ubicacion());
                ps.setString(7, producto.categoria());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private record CategoriaDemo(String nombre, String descripcion) { }

    private record ProductoDemo(String sku, String codigoBarras, String nombre, double precio,
                                int existencia, String ubicacion, String categoria) { }
}
