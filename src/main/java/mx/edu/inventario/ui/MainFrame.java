package mx.edu.inventario.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import mx.edu.inventario.exception.DataAccessException;
import mx.edu.inventario.exception.ValidationException;
import mx.edu.inventario.model.Categoria;
import mx.edu.inventario.model.Producto;
import mx.edu.inventario.service.InventarioService;

public class MainFrame extends JFrame {

    private static final Color FONDO = new Color(245, 245, 248);
    private static final Color MORADO = new Color(36, 10, 155);
    private static final Color NARANJA = new Color(255, 107, 0);
    private static final Color AZUL = MORADO;
    private static final Color VERDE = new Color(45, 143, 92);

    private final InventarioService service = new InventarioService();
    private final DashboardPanel dashboard = new DashboardPanel();
    private final ProductGridPanel catalogo = new ProductGridPanel(this::seleccionarDesdeCatalogo);
    private final Map<Integer, Producto> productosPorId = new HashMap<>();

    private final JTextField txtSku = new JTextField();
    private final JTextField txtCodigo = new JTextField();
    private final JTextField txtNombre = new JTextField();
    private final JTextField txtPrecio = new JTextField();
    private final JTextField txtExistencia = new JTextField();
    private final JTextField txtUbicacion = new JTextField();
    private final JTextField txtFoto = new JTextField();
    private final JLabel vistaFoto = new JLabel("Sin fotografía", JLabel.CENTER);
    private final JTextField txtBuscar = new JTextField();
    private final JComboBox<Categoria> cmbCategoria = new JComboBox<>();
    private final JComboBox<Categoria> cmbFiltroCategoria = new JComboBox<>();

    private final DefaultTableModel modelo = new DefaultTableModel(
            new String[]{"ID", "SKU", "Producto", "Precio", "Stock", "Estado", "Ubicación", "Categoría", "Código"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }

        @Override
        public Class<?> getColumnClass(int column) {
            return switch (column) {
                case 0, 4 -> Integer.class;
                case 3 -> Double.class;
                default -> String.class;
            };
        }
    };
    private final JTable tabla = new JTable(modelo);
    private final TableRowSorter<DefaultTableModel> ordenador = new TableRowSorter<>(modelo);
    private Integer productoId;

    public MainFrame() {
        super("Control de almacén · Inventario");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1080, 680));
        setSize(1280, 760);
        setLocationRelativeTo(null);
        construir();
        cargar();
    }

    private void construir() {
        JPanel raiz = new JPanel(new BorderLayout());
        raiz.setBackground(FONDO);
        raiz.add(construirCabecera(), BorderLayout.NORTH);
        raiz.add(construirContenido(), BorderLayout.CENTER);
        setContentPane(raiz);
    }

    private JPanel construirCabecera() {
        JPanel panel = new JPanel(new BorderLayout(18, 0));
        panel.setBackground(MORADO);
        panel.setBorder(BorderFactory.createEmptyBorder(11, 18, 11, 18));
        panel.setPreferredSize(new Dimension(100, 66));

        JLabel marca = new JLabel("●  INVU STOCK");
        marca.setForeground(Color.WHITE);
        marca.setFont(marca.getFont().deriveFont(Font.BOLD, 24f));
        panel.add(marca, BorderLayout.WEST);

        JButton accion = boton("Registrar movimiento de inventario", NARANJA);
        accion.setFont(accion.getFont().deriveFont(Font.BOLD, 12f));
        accion.setPreferredSize(new Dimension(470, 34));
        accion.addActionListener(e -> reabastecerLote());
        JPanel centro = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 5));
        centro.setOpaque(false);
        centro.add(accion);
        panel.add(centro, BorderLayout.CENTER);

        JPanel accesos = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 2));
        accesos.setOpaque(false);
        accesos.add(botonCircular("↻", "Actualizar inventario", this::cargarProductos));
        accesos.add(botonCircular("⌕", "Buscar productos", () -> txtBuscar.requestFocus()));
        accesos.add(botonCircular("＋", "Nuevo producto", this::limpiar));
        panel.add(accesos, BorderLayout.EAST);
        return panel;
    }

    private JSplitPane construirContenido() {
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, construirEditor(), construirListado());
        split.setBorder(null);
        split.setDividerSize(2);
        split.setResizeWeight(0.25);
        split.setDividerLocation(315);
        return split;
    }

    private JPanel construirListado() {
        JPanel panel = tarjeta(new BorderLayout(0, 9));
        JPanel superior = new JPanel(new BorderLayout(0, 10));
        superior.setOpaque(false);
        superior.add(construirFiltros(), BorderLayout.NORTH);
        superior.add(dashboard, BorderLayout.CENTER);
        panel.add(superior, BorderLayout.NORTH);
        configurarTabla();
        JTabbedPane vistas = new JTabbedPane();
        vistas.setFont(vistas.getFont().deriveFont(Font.BOLD, 12f));
        vistas.setForeground(MORADO);
        JScrollPane scrollCatalogo = new JScrollPane(catalogo);
        scrollCatalogo.setBorder(null);
        scrollCatalogo.getVerticalScrollBar().setUnitIncrement(18);
        vistas.addTab("  Catálogo visual  ", scrollCatalogo);
        vistas.addTab("  Tabla de inventario  ", new JScrollPane(tabla));
        panel.add(vistas, BorderLayout.CENTER);
        panel.add(construirAccionesLote(), BorderLayout.SOUTH);
        return panel;
    }

    private JPanel construirFiltros() {
        JPanel filtros = new JPanel(new BorderLayout(8, 0));
        filtros.setOpaque(false);
        txtBuscar.putClientProperty("JTextField.placeholderText", "Nombre, SKU o código de barras");
        txtBuscar.addActionListener(e -> cargarProductos());
        filtros.add(txtBuscar, BorderLayout.CENTER);
        cmbFiltroCategoria.setPreferredSize(new Dimension(190, 30));
        cmbFiltroCategoria.addActionListener(e -> cargarProductos());
        filtros.add(cmbFiltroCategoria, BorderLayout.EAST);
        JButton buscar = boton("Buscar", NARANJA);
        buscar.addActionListener(e -> cargarProductos());
        filtros.add(buscar, BorderLayout.WEST);
        return filtros;
    }

    private void configurarTabla() {
        tabla.setRowSorter(ordenador);
        tabla.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        tabla.setRowHeight(34);
        tabla.setShowVerticalLines(false);
        tabla.setGridColor(new Color(222, 228, 230));
        tabla.getTableHeader().setBackground(MORADO);
        tabla.getTableHeader().setForeground(Color.WHITE);
        tabla.getTableHeader().setFont(tabla.getFont().deriveFont(Font.BOLD));
        tabla.getColumnModel().getColumn(4).setCellRenderer(new StockCellRenderer());
        tabla.getColumnModel().getColumn(5).setCellRenderer(new StockCellRenderer());
        DefaultTableCellRenderer precio = new DefaultTableCellRenderer() {
            @Override
            protected void setValue(Object value) {
                setText(value == null ? "" : String.format("$ %,.2f", value));
            }
        };
        precio.setHorizontalAlignment(JLabel.RIGHT);
        tabla.getColumnModel().getColumn(3).setCellRenderer(precio);
        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tabla.getSelectedRowCount() == 1) {
                seleccionarProducto();
            }
        });
    }

    private JPanel construirAccionesLote() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 7, 0));
        panel.setOpaque(false);
        panel.add(new JLabel("Acciones para seleccionados:"));
        JButton precio = boton("Cambiar precio", new Color(77, 101, 112));
        precio.addActionListener(e -> cambiarPrecioLote());
        JButton reabastecer = boton("Reabastecer", VERDE);
        reabastecer.addActionListener(e -> reabastecerLote());
        panel.add(precio);
        panel.add(reabastecer);
        return panel;
    }

    private JPanel construirEditor() {
        JPanel panel = tarjeta(new BorderLayout(0, 10));
        panel.setBackground(new Color(252, 247, 255));
        panel.setPreferredSize(new Dimension(315, 650));
        JLabel titulo = new JLabel("Ficha del producto");
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 17f));
        titulo.setForeground(MORADO);
        panel.add(titulo, BorderLayout.NORTH);

        JPanel campos = new JPanel(new GridBagLayout());
        campos.setOpaque(false);
        int fila = 0;
        agregarCampo(campos, "SKU *", txtSku, fila++);
        agregarCampo(campos, "Código de barras", txtCodigo, fila++);
        agregarCampo(campos, "Nombre *", txtNombre, fila++);
        agregarCampo(campos, "Precio *", txtPrecio, fila++);
        agregarCampo(campos, "Existencia *", txtExistencia, fila++);
        agregarCampo(campos, "Ubicación", txtUbicacion, fila++);
        agregarCampo(campos, "Categoría *", cmbCategoria, fila++);
        agregarCampoFoto(campos, fila);
        panel.add(campos, BorderLayout.CENTER);
        panel.add(construirBotonesEditor(), BorderLayout.SOUTH);
        return panel;
    }

    private JPanel construirBotonesEditor() {
        JPanel panel = new JPanel(new java.awt.GridLayout(2, 2, 7, 7));
        panel.setOpaque(false);
        JButton nuevo = boton("Nuevo", MORADO);
        JButton guardar = boton("Guardar cambios", NARANJA);
        JButton eliminar = boton("Eliminar", new Color(181, 50, 57));
        JButton categorias = boton("Categorías", MORADO);
        nuevo.addActionListener(e -> limpiar());
        guardar.addActionListener(e -> guardar());
        eliminar.addActionListener(e -> eliminar());
        categorias.addActionListener(e -> abrirCategorias());
        panel.add(nuevo);
        panel.add(guardar);
        panel.add(eliminar);
        panel.add(categorias);
        return panel;
    }

    private void agregarCampo(JPanel panel, String etiqueta, java.awt.Component campo, int fila) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = fila * 2;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(3, 0, 2, 0);
        panel.add(new JLabel(etiqueta), c);
        c.gridy++;
        c.insets = new Insets(0, 0, 7, 0);
        campo.setPreferredSize(new Dimension(200, 29));
        panel.add(campo, c);
    }

    private void agregarCampoFoto(JPanel panel, int fila) {
        JPanel selector = new JPanel(new BorderLayout(5, 5));
        selector.setOpaque(false);
        vistaFoto.setPreferredSize(new Dimension(200, 78));
        vistaFoto.setOpaque(true);
        vistaFoto.setBackground(new Color(239, 243, 244));
        vistaFoto.setForeground(new Color(91, 105, 112));
        vistaFoto.setBorder(BorderFactory.createLineBorder(new Color(215, 223, 226)));
        selector.add(vistaFoto, BorderLayout.CENTER);
        txtFoto.setEditable(false);
        txtFoto.setToolTipText("Ruta de la fotografía seleccionada");
        selector.add(txtFoto, BorderLayout.SOUTH);
        JButton elegir = boton("…", new Color(77, 101, 112));
        elegir.setToolTipText("Elegir fotografía del producto");
        elegir.addActionListener(e -> elegirFoto());
        selector.add(elegir, BorderLayout.EAST);
        agregarCampo(panel, "Fotografía", selector, fila);
    }

    private JPanel tarjeta(java.awt.LayoutManager layout) {
        JPanel panel = new JPanel(layout);
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        return panel;
    }

    private JButton boton(String texto, Color fondo) {
        JButton boton = new JButton(texto);
        boton.setBackground(fondo);
        boton.setForeground(Color.WHITE);
        boton.setFocusPainted(false);
        return boton;
    }

    private JButton botonCircular(String texto, String ayuda, Runnable accion) {
        JButton boton = new JButton(texto);
        boton.setPreferredSize(new Dimension(36, 36));
        boton.setToolTipText(ayuda);
        boton.setForeground(MORADO);
        boton.setBackground(Color.WHITE);
        boton.setFont(boton.getFont().deriveFont(Font.BOLD, 18f));
        boton.setFocusPainted(false);
        boton.setMargin(new Insets(0, 0, 0, 0));
        boton.addActionListener(e -> accion.run());
        return boton;
    }

    private void cargar() {
        try {
            cargarCategorias();
            cargarProductos();
        } catch (DataAccessException e) {
            error(e.getMessage());
        }
    }

    private void cargarCategorias() throws DataAccessException {
        Categoria seleccion = (Categoria) cmbCategoria.getSelectedItem();
        cmbCategoria.removeAllItems();
        cmbFiltroCategoria.removeAllItems();
        cmbFiltroCategoria.addItem(new Categoria(null, "Todas las categorías", ""));
        for (Categoria categoria : service.categorias()) {
            cmbCategoria.addItem(categoria);
            cmbFiltroCategoria.addItem(categoria);
            if (seleccion != null && seleccion.getId() != null && seleccion.getId().equals(categoria.getId())) {
                cmbCategoria.setSelectedItem(categoria);
            }
        }
    }

    private void cargarProductos() {
        if (cmbFiltroCategoria.getItemCount() == 0) {
            return;
        }
        try {
            Categoria categoria = (Categoria) cmbFiltroCategoria.getSelectedItem();
            Integer categoriaId = categoria == null ? null : categoria.getId();
            List<Producto> productos = service.productos(txtBuscar.getText(), categoriaId);
            productosPorId.clear();
            modelo.setRowCount(0);
            for (Producto p : productos) {
                productosPorId.put(p.getId(), p);
                modelo.addRow(new Object[]{p.getId(), p.getSku(), p.getNombre(), p.getPrecio(), p.getExistencia(), "",
                        valor(p.getUbicacion()), p.getCategoria().getNombre(), valor(p.getCodigoBarras())});
            }
            dashboard.actualizar(productos);
            catalogo.mostrar(productos);
        } catch (DataAccessException e) {
            error(e.getMessage());
        }
    }

    private void seleccionarProducto() {
        int filaVista = tabla.getSelectedRow();
        if (filaVista < 0) {
            return;
        }
        int filaModelo = tabla.convertRowIndexToModel(filaVista);
        productoId = (Integer) modelo.getValueAt(filaModelo, 0);
        Producto p = productosPorId.get(productoId);
        mostrarProducto(p);
    }

    private void seleccionarDesdeCatalogo(Producto producto) {
        tabla.clearSelection();
        mostrarProducto(producto);
    }

    private void mostrarProducto(Producto p) {
        productoId = p.getId();
        txtSku.setText(valor(p.getSku()));
        txtCodigo.setText(valor(p.getCodigoBarras()));
        txtNombre.setText(p.getNombre());
        txtPrecio.setText(String.valueOf(p.getPrecio()));
        txtExistencia.setText(String.valueOf(p.getExistencia()));
        txtUbicacion.setText(valor(p.getUbicacion()));
        txtFoto.setText(valor(p.getFoto()));
        actualizarVistaFoto(p.getFoto());
        seleccionarCategoria(p.getCategoria().getId());
    }

    private void guardar() {
        try {
            Producto producto = new Producto(productoId, txtSku.getText().trim(), txtCodigo.getText().trim(),
                    txtNombre.getText().trim(), Double.parseDouble(txtPrecio.getText().trim()),
                    Integer.parseInt(txtExistencia.getText().trim()), txtUbicacion.getText().trim(),
                    txtFoto.getText().trim(), (Categoria) cmbCategoria.getSelectedItem());
            service.guardarProducto(producto);
            limpiar();
            cargarProductos();
        } catch (NumberFormatException e) {
            error("Precio y existencia deben ser valores numéricos válidos.");
        } catch (ValidationException | DataAccessException e) {
            error(e.getMessage());
        }
    }

    private void eliminar() {
        if (productoId == null) {
            error("Seleccione un solo producto para eliminar.");
            return;
        }
        int respuesta = JOptionPane.showConfirmDialog(this,
                "Se eliminará el producto seleccionado. Esta acción no se puede deshacer.",
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (respuesta == JOptionPane.YES_OPTION) {
            try {
                service.eliminarProducto(productoId);
                limpiar();
                cargarProductos();
            } catch (DataAccessException e) {
                error(e.getMessage());
            }
        }
    }

    private void reabastecerLote() {
        String entrada = JOptionPane.showInputDialog(this, "Unidades a agregar a cada producto:", "Reabastecer", JOptionPane.PLAIN_MESSAGE);
        if (entrada == null) {
            return;
        }
        try {
            int cantidad = Integer.parseInt(entrada.trim());
            if (cantidad >= 100 && !confirmarOperacionGrande(cantidad)) {
                return;
            }
            service.reabastecerProductos(idsSeleccionados(), cantidad);
            cargarProductos();
        } catch (NumberFormatException e) {
            error("Ingrese una cantidad entera válida.");
        } catch (ValidationException | DataAccessException e) {
            error(e.getMessage());
        }
    }

    private void cambiarPrecioLote() {
        String entrada = JOptionPane.showInputDialog(this, "Nuevo precio para los productos seleccionados:",
                "Cambiar precio", JOptionPane.PLAIN_MESSAGE);
        if (entrada == null) {
            return;
        }
        try {
            service.cambiarPrecioProductos(idsSeleccionados(), Double.parseDouble(entrada.trim()));
            cargarProductos();
        } catch (NumberFormatException e) {
            error("Ingrese un precio numérico válido.");
        } catch (ValidationException | DataAccessException e) {
            error(e.getMessage());
        }
    }

    private List<Integer> idsSeleccionados() {
        List<Integer> ids = new ArrayList<>();
        for (int filaVista : tabla.getSelectedRows()) {
            ids.add((Integer) modelo.getValueAt(tabla.convertRowIndexToModel(filaVista), 0));
        }
        if (ids.isEmpty() && productoId != null) {
            ids.add(productoId);
        }
        return ids;
    }

    private boolean confirmarOperacionGrande(int cantidad) {
        return JOptionPane.showConfirmDialog(this,
                "Se agregarán " + cantidad + " unidades a cada producto seleccionado. ¿Continuar?",
                "Confirmar movimiento grande", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE)
                == JOptionPane.YES_OPTION;
    }

    private void elegirFoto() {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File archivo = chooser.getSelectedFile();
            txtFoto.setText(archivo.getAbsolutePath());
            actualizarVistaFoto(archivo.getAbsolutePath());
        }
    }

    private void actualizarVistaFoto(String ruta) {
        if (ruta == null || ruta.isBlank() || !new File(ruta).isFile()) {
            vistaFoto.setIcon(null);
            vistaFoto.setText("Sin fotografía");
            return;
        }
        ImageIcon original = new ImageIcon(ruta);
        Image escalada = original.getImage().getScaledInstance(190, 72, Image.SCALE_SMOOTH);
        vistaFoto.setText("");
        vistaFoto.setIcon(new ImageIcon(escalada));
    }

    private void seleccionarCategoria(Integer id) {
        for (int i = 0; i < cmbCategoria.getItemCount(); i++) {
            if (cmbCategoria.getItemAt(i).getId().equals(id)) {
                cmbCategoria.setSelectedIndex(i);
                return;
            }
        }
    }

    private void limpiar() {
        productoId = null;
        txtSku.setText("");
        txtCodigo.setText("");
        txtNombre.setText("");
        txtPrecio.setText("");
        txtExistencia.setText("");
        txtUbicacion.setText("");
        txtFoto.setText("");
        actualizarVistaFoto(null);
        tabla.clearSelection();
        txtSku.requestFocus();
    }

    private void abrirCategorias() {
        new CategoriaDialog(this, service, () -> {
            try {
                cargarCategorias();
                cargarProductos();
            } catch (DataAccessException e) {
                error(e.getMessage());
            }
        }).setVisible(true);
    }

    private String valor(String texto) {
        return texto == null ? "" : texto;
    }

    private void error(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "No se pudo completar la operación", JOptionPane.ERROR_MESSAGE);
    }
}
