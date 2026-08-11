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

public class CategoriaDAO {

    public List<Categoria> listar() throws DataAccessException {
        String sql = "SELECT id, nombre, descripcion FROM categorias ORDER BY nombre";
        List<Categoria> lista = new ArrayList<>();
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(map(rs));
            }
            return lista;
        } catch (SQLException e) {
            throw new DataAccessException("No se pudieron consultar las categorías.", e);
        }
    }

    public void guardar(Categoria c) throws DataAccessException {
        String sql = "INSERT INTO categorias(nombre, descripcion) VALUES (?, ?)";
        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, c.getNombre());
            ps.setString(2, c.getDescripcion());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("No se pudo guardar la categoría. Verifique que el nombre no esté repetido.", e);
        }
    }

    public void actualizar(Categoria c) throws DataAccessException {
        String sql = "UPDATE categorias SET nombre = ?, descripcion = ? WHERE id = ?";
        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, c.getNombre());
            ps.setString(2, c.getDescripcion());
            ps.setInt(3, c.getId());
            if (ps.executeUpdate() == 0) {
                throw new DataAccessException("La categoría ya no existe.");
            }
        } catch (SQLException e) {
            throw new DataAccessException("No se pudo actualizar la categoría.", e);
        }
    }

    public void eliminar(int id) throws DataAccessException {
        String sql = "DELETE FROM categorias WHERE id = ?";
        try (Connection cn = DatabaseConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("No se puede eliminar una categoría que tiene productos asociados.", e);
        }
    }

    private Categoria map(ResultSet r) throws SQLException {
        return new Categoria(r.getInt("id"), r.getString("nombre"), r.getString("descripcion"));
    }
}
