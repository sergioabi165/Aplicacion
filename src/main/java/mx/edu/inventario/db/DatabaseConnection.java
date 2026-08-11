package mx.edu.inventario.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import mx.edu.inventario.exception.DataAccessException;

/** Centraliza la conexión MySQL reutilizable y crea el esquema base. */
public final class DatabaseConnection {

    private static final String HOST = "localhost";
    private static final String PORT = "3306";
    private static final String DB_NAME = "inventario";
    private static final String USER = "root";
    private static final String PASSWORD = "";
    private static final String OPTIONS = "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String SERVER_URL = "jdbc:mysql://" + HOST + ":" + PORT + "/" + OPTIONS;
    private static final String URL = "jdbc:mysql://" + HOST + ":" + PORT + "/" + DB_NAME + OPTIONS;

    private DatabaseConnection() {
    }

    public static Connection getConnection() throws DataAccessException {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            throw new DataAccessException("No se pudo conectar con la base de datos.", e);
        }
    }

    public static void initializeDatabase() throws DataAccessException {
        crearBaseDeDatos();
        crearTablas();
    }

    private static void crearBaseDeDatos() throws DataAccessException {
        try (Connection server = DriverManager.getConnection(SERVER_URL, USER, PASSWORD);
             Statement st = server.createStatement()) {
            st.executeUpdate("CREATE DATABASE IF NOT EXISTS " + DB_NAME);
        } catch (SQLException e) {
            throw new DataAccessException("No se pudo crear la base de datos.", e);
        }
    }

    private static void crearTablas() throws DataAccessException {
        try (Connection c = getConnection(); Statement st = c.createStatement()) {
            st.executeUpdate("""
                CREATE TABLE IF NOT EXISTS categorias (
                    id INT PRIMARY KEY AUTO_INCREMENT,
                    nombre VARCHAR(150) NOT NULL UNIQUE,
                    descripcion TEXT
                )
                """);
            st.executeUpdate("""
                CREATE TABLE IF NOT EXISTS productos (
                    id INT PRIMARY KEY AUTO_INCREMENT,
                    sku VARCHAR(50) NOT NULL UNIQUE,
                    codigo_barras VARCHAR(100) UNIQUE,
                    nombre VARCHAR(150) NOT NULL,
                    precio DOUBLE NOT NULL CHECK (precio >= 0),
                    existencia INT NOT NULL CHECK (existencia >= 0),
                    ubicacion VARCHAR(100),
                    foto VARCHAR(500),
                    categoria_id INT NOT NULL,
                    FOREIGN KEY (categoria_id) REFERENCES categorias(id) ON DELETE RESTRICT
                )
                """);
            agregarColumnaSiFalta(c, "sku", "VARCHAR(50) NULL UNIQUE");
            agregarColumnaSiFalta(c, "codigo_barras", "VARCHAR(100) NULL UNIQUE");
            agregarColumnaSiFalta(c, "ubicacion", "VARCHAR(100) NULL");
            agregarColumnaSiFalta(c, "foto", "VARCHAR(500) NULL");
            st.executeUpdate("UPDATE productos SET sku = CONCAT('SKU-', LPAD(id, 5, '0')) "
                    + "WHERE sku IS NULL OR TRIM(sku) = ''");
            SampleDataSeeder.insertar(c);
        } catch (SQLException e) {
            throw new DataAccessException("No se pudo preparar la base de datos.", e);
        }
    }

    private static void agregarColumnaSiFalta(Connection connection, String columna, String definicion)
            throws SQLException {
        String consulta = """
                SELECT COUNT(*) FROM information_schema.columns
                WHERE table_schema = ? AND table_name = 'productos' AND column_name = ?
                """;
        try (PreparedStatement ps = connection.prepareStatement(consulta)) {
            ps.setString(1, DB_NAME);
            ps.setString(2, columna);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                if (rs.getInt(1) == 0) {
                    try (Statement st = connection.createStatement()) {
                        st.executeUpdate("ALTER TABLE productos ADD COLUMN " + columna + " " + definicion);
                    }
                }
            }
        }
    }
}
