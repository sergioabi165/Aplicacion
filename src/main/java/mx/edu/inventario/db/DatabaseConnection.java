package mx.edu.inventario.db;

import java.sql.Connection;
import java.sql.DriverManager;
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
                    nombre VARCHAR(150) NOT NULL,
                    precio DOUBLE NOT NULL CHECK (precio >= 0),
                    existencia INT NOT NULL CHECK (existencia >= 0),
                    categoria_id INT NOT NULL,
                    FOREIGN KEY (categoria_id) REFERENCES categorias(id) ON DELETE RESTRICT
                )
                """);
        } catch (SQLException e) {
            throw new DataAccessException("No se pudo preparar la base de datos.", e);
        }
    }
}
