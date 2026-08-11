package mx.edu.inventario.service;

import java.util.List;
import mx.edu.inventario.dao.CategoriaDAO;
import mx.edu.inventario.dao.ProductoDAO;
import mx.edu.inventario.exception.DataAccessException;
import mx.edu.inventario.exception.ValidationException;
import mx.edu.inventario.model.Categoria;
import mx.edu.inventario.model.Producto;

/** Aplica reglas de negocio antes de persistir datos. */
public class InventarioService {

    private final CategoriaDAO categoriaDAO = new CategoriaDAO();
    private final ProductoDAO productoDAO = new ProductoDAO();

    public List<Categoria> categorias() throws DataAccessException {
        return categoriaDAO.listar();
    }

    public List<Producto> productos(String filtro) throws DataAccessException {
        return productoDAO.listar(filtro, null);
    }

    public List<Producto> productos(String filtro, Integer categoriaId) throws DataAccessException {
        return productoDAO.listar(filtro, categoriaId);
    }

    public void guardarCategoria(Categoria c) throws ValidationException, DataAccessException {
        if (c.getNombre() == null || c.getNombre().trim().isEmpty()) {
            throw new ValidationException("El nombre de la categoría es obligatorio.");
        }
        if (c.getId() == null) {
            categoriaDAO.guardar(c);
        } else {
            categoriaDAO.actualizar(c);
        }
    }

    public void eliminarCategoria(int id) throws DataAccessException {
        categoriaDAO.eliminar(id);
    }

    public void guardarProducto(Producto p) throws ValidationException, DataAccessException {
        if (p.getNombre() == null || p.getNombre().trim().isEmpty()) {
            throw new ValidationException("El nombre del producto es obligatorio.");
        }
        if (p.getSku() == null || p.getSku().trim().isEmpty()) {
            throw new ValidationException("El código SKU es obligatorio.");
        }
        if (p.getPrecio() < 0) {
            throw new ValidationException("El precio no puede ser negativo.");
        }
        if (p.getExistencia() < 0) {
            throw new ValidationException("La existencia no puede ser negativa.");
        }
        if (p.getCategoria() == null) {
            throw new ValidationException("Seleccione una categoría.");
        }
        if (p.getId() == null) {
            productoDAO.guardar(p);
        } else {
            productoDAO.actualizar(p);
        }
    }

    public void eliminarProducto(int id) throws DataAccessException {
        productoDAO.eliminar(id);
    }

    public void reabastecerProductos(List<Integer> ids, int cantidad)
            throws ValidationException, DataAccessException {
        if (ids.isEmpty()) {
            throw new ValidationException("Seleccione al menos un producto.");
        }
        if (cantidad <= 0) {
            throw new ValidationException("La cantidad debe ser mayor que cero.");
        }
        productoDAO.reabastecer(ids, cantidad);
    }

    public void cambiarPrecioProductos(List<Integer> ids, double precio)
            throws ValidationException, DataAccessException {
        if (ids.isEmpty()) {
            throw new ValidationException("Seleccione al menos un producto.");
        }
        if (precio < 0) {
            throw new ValidationException("El precio no puede ser negativo.");
        }
        productoDAO.cambiarPrecio(ids, precio);
    }
}
