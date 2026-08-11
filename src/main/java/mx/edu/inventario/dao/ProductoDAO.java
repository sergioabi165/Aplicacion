package mx.edu.inventario.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import mx.edu.inventario.db.DatabaseConnection;
import mx.edu.inventario.exception.DataAccessException;
import mx.edu.inventario.model.Categoria;
import mx.edu.inventario.model.Producto;

public class ProductoDAO {

    private static final String SELECT_BASE = """
            SELECT p.id, p.sku, p.codigo_barras, p.nombre, p.precio, p.existencia,
                   p.ubicacion, p.foto, c.id categoria_id, c.nombre categoria, c.descripcion
            FROM productos p
            JOIN categorias c ON c.id = p.categoria_id
            """;

    public List<Producto> listar(String criterio, Integer categoriaId) throws DataAccessException {
        String sql = SELECT_BASE + """
                WHERE (LOWER(p.nombre) LIKE LOWER(?) OR LOWER(COALESCE(p.sku, '')) LIKE LOWER(?)
                       OR LOWER(COALESCE(p.codigo_barras, '')) LIKE LOWER(?))
                  AND (? IS NULL OR p.categoria_id = ?)
                ORDER BY p.id DESC
                """;
        List<Producto> productos = new ArrayList<>();
        String filtro = "%" + (criterio == null ? "" : criterio.trim()) + "%";
        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, filtro);
            ps.setString(2, filtro);
            ps.setString(3, filtro);
            if (categoriaId == null) {
                ps.setNull(4, java.sql.Types.INTEGER);
                ps.setNull(5, java.sql.Types.INTEGER);
            } else {
                ps.setInt(4, categoriaId);
                ps.setInt(5, categoriaId);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    productos.add(map(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("No se pudieron consultar los productos.", e);
        }
        return productos;
    }

    public void guardar(Producto p) throws DataAccessException {
        String sql = """
                INSERT INTO productos(sku, codigo_barras, nombre, precio, existencia, ubicacion, foto, categoria_id)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;
        ejecutarGuardado(p, sql, false);
    }

    public void actualizar(Producto p) throws DataAccessException {
        String sql = """
                UPDATE productos SET sku = ?, codigo_barras = ?, nombre = ?, precio = ?, existencia = ?,
                       ubicacion = ?, foto = ?, categoria_id = ? WHERE id = ?
                """;
        ejecutarGuardado(p, sql, true);
    }

    public void eliminar(int id) throws DataAccessException {
        ejecutarActualizacion("DELETE FROM productos WHERE id = ?", List.of(id),
                "No se pudo eliminar el producto.");
    }

    public void reabastecer(List<Integer> ids, int cantidad) throws DataAccessException {
        ejecutarLote("UPDATE productos SET existencia = existencia + ? WHERE id = ?", ids, cantidad,
                "No se pudieron reabastecer los productos.");
    }

    public void cambiarPrecio(List<Integer> ids, double precio) throws DataAccessException {
        ejecutarLote("UPDATE productos SET precio = ? WHERE id = ?", ids, precio,
                "No se pudo cambiar el precio de los productos.");
    }

    private void ejecutarGuardado(Producto p, String sql, boolean actualizar) throws DataAccessException {
        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, p.getSku());
            ps.setString(2, textoNullable(p.getCodigoBarras()));
            ps.setString(3, p.getNombre());
            ps.setDouble(4, p.getPrecio());
            ps.setInt(5, p.getExistencia());
            ps.setString(6, textoNullable(p.getUbicacion()));
            ps.setString(7, textoNullable(p.getFoto()));
            ps.setInt(8, p.getCategoria().getId());
            if (actualizar) {
                ps.setInt(9, p.getId());
            }
            if (ps.executeUpdate() == 0) {
                throw new DataAccessException("El producto ya no existe.");
            }
        } catch (SQLException e) {
            throw new DataAccessException("No se pudo guardar. Verifique que SKU y código de barras no estén repetidos.", e);
        }
    }

    private void ejecutarLote(String sql, List<Integer> ids, Number valor, String mensaje)
            throws DataAccessException {
        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            cn.setAutoCommit(false);
            for (Integer id : ids) {
                if (valor instanceof Integer entero) {
                    ps.setInt(1, entero);
                } else {
                    ps.setDouble(1, valor.doubleValue());
                }
                ps.setInt(2, id);
                ps.addBatch();
            }
            ps.executeBatch();
            cn.commit();
        } catch (SQLException e) {
            throw new DataAccessException(mensaje, e);
        }
    }

    private void ejecutarActualizacion(String sql, List<Integer> ids, String mensaje) throws DataAccessException {
        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            for (Integer id : ids) {
                ps.setInt(1, id);
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (SQLException e) {
            throw new DataAccessException(mensaje, e);
        }
    }

    private String textoNullable(String valor) {
        return valor == null || valor.isBlank() ? null : valor.trim();
    }

    private Producto map(ResultSet rs) throws SQLException {
        Categoria categoria = new Categoria(rs.getInt("categoria_id"), rs.getString("categoria"),
                rs.getString("descripcion"));
        return new Producto(rs.getInt("id"), rs.getString("sku"), rs.getString("codigo_barras"),
                rs.getString("nombre"), rs.getDouble("precio"), rs.getInt("existencia"),
                rs.getString("ubicacion"), rs.getString("foto"), categoria);
    }
}
