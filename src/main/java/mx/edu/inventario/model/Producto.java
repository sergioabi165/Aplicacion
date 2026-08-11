package mx.edu.inventario.model;

public class Producto {

    private Integer id;
    private String sku;
    private String codigoBarras;
    private String nombre;
    private double precio;
    private int existencia;
    private String ubicacion;
    private String foto;
    private Categoria categoria;

    public Producto() {
    }

    public Producto(Integer id, String sku, String codigoBarras, String nombre, double precio,
                    int existencia, String ubicacion, String foto, Categoria categoria) {
        this.id = id;
        this.sku = sku;
        this.codigoBarras = codigoBarras;
        this.nombre = nombre;
        this.precio = precio;
        this.existencia = existencia;
        this.ubicacion = ubicacion;
        this.foto = foto;
        this.categoria = categoria;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getCodigoBarras() {
        return codigoBarras;
    }

    public void setCodigoBarras(String codigoBarras) {
        this.codigoBarras = codigoBarras;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public int getExistencia() {
        return existencia;
    }

    public void setExistencia(int existencia) {
        this.existencia = existencia;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }
}
