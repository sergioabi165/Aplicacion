package mx.edu.inventario.ui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import mx.edu.inventario.exception.DataAccessException;
import mx.edu.inventario.exception.ValidationException;
import mx.edu.inventario.model.Categoria;
import mx.edu.inventario.model.Producto;
import mx.edu.inventario.service.InventarioService;

public class MainFrame extends JFrame {

    private final InventarioService service = new InventarioService();

    private final JTextField txtNombre = new JTextField();
    private final JTextField txtPrecio = new JTextField();
    private final JTextField txtExistencia = new JTextField();
    private final JTextField txtBuscar = new JTextField();
    private final JComboBox<Categoria> cmbCategoria = new JComboBox<>();

    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"ID", "Producto", "Precio", "Existencia", "Categoría"}, 0) {
        @Override
        public boolean isCellEditable(int row, int col) {
            return false;
        }
    };
    private final JTable tabla = new JTable(model);

    private Integer productoId;

    public MainFrame() {
        super("Sistema de Inventario");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(880, 580);
        setLocationRelativeTo(null);
        construir();
        cargar();
    }

    private void construir() {
        setLayout(new BorderLayout(10, 10));
        add(construirFormulario(), BorderLayout.NORTH);
        add(construirCentro(), BorderLayout.CENTER);
        add(construirBotones(), BorderLayout.SOUTH);
    }

    private JPanel construirFormulario() {
        JPanel form = new JPanel(new GridLayout(2, 5, 8, 5));
        form.setBorder(BorderFactory.createTitledBorder("Datos del producto"));

        form.add(new JLabel("Nombre"));
        form.add(new JLabel("Precio"));
        form.add(new JLabel("Existencia"));
        form.add(new JLabel("Categoría"));
        form.add(new JLabel(""));

        form.add(txtNombre);
        form.add(txtPrecio);
        form.add(txtExistencia);
        form.add(cmbCategoria);

        JButton categorias = new JButton("Gestionar categorías");
        categorias.addActionListener(e -> abrirCategorias());
        form.add(categorias);

        return form;
    }

    private JPanel construirCentro() {
        JPanel center = new JPanel(new BorderLayout(5, 5));

        JPanel search = new JPanel(new BorderLayout(5, 5));
        search.add(new JLabel("Buscar:"), BorderLayout.WEST);
        search.add(txtBuscar, BorderLayout.CENTER);
        JButton buscar = new JButton("Filtrar");
        buscar.addActionListener(e -> cargarProductos());
        txtBuscar.addActionListener(e -> cargarProductos());
        search.add(buscar, BorderLayout.EAST);
        center.add(search, BorderLayout.NORTH);

        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setAutoCreateRowSorter(true);
        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                seleccionar();
            }
        });
        center.add(new JScrollPane(tabla), BorderLayout.CENTER);

        return center;
    }

    private JPanel construirBotones() {
        JPanel buttons = new JPanel();
        for (String texto : new String[]{"Nuevo", "Guardar", "Eliminar", "Actualizar"}) {
            JButton boton = new JButton(texto);
            buttons.add(boton);
            switch (texto) {
                case "Nuevo" -> boton.addActionListener(e -> limpiar());
                case "Guardar", "Actualizar" -> boton.addActionListener(e -> guardar());
                case "Eliminar" -> boton.addActionListener(e -> eliminar());
                default -> throw new IllegalStateException("Botón no reconocido: " + texto);
            }
        }
        return buttons;
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
        cmbCategoria.removeAllItems();
        for (Categoria c : service.categorias()) {
            cmbCategoria.addItem(c);
        }
    }

    private void cargarProductos() {
        try {
            model.setRowCount(0);
            for (Producto p : service.productos(txtBuscar.getText())) {
                model.addRow(new Object[]{
                        p.getId(), p.getNombre(), String.format("$ %.2f", p.getPrecio()),
                        p.getExistencia(), p.getCategoria().getNombre()
                });
            }
        } catch (DataAccessException e) {
            error(e.getMessage());
        }
    }

    private void seleccionar() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            return;
        }
        int modelRow = tabla.convertRowIndexToModel(fila);
        productoId = (Integer) model.getValueAt(modelRow, 0);
        txtNombre.setText((String) model.getValueAt(modelRow, 1));
        txtPrecio.setText(model.getValueAt(modelRow, 2).toString().replace("$ ", ""));
        txtExistencia.setText(model.getValueAt(modelRow, 3).toString());

        String categoria = model.getValueAt(modelRow, 4).toString();
        for (int i = 0; i < cmbCategoria.getItemCount(); i++) {
            if (cmbCategoria.getItemAt(i).getNombre().equals(categoria)) {
                cmbCategoria.setSelectedIndex(i);
                break;
            }
        }
    }

    private void guardar() {
        try {
            Producto p = new Producto(
                    productoId,
                    txtNombre.getText().trim(),
                    Double.parseDouble(txtPrecio.getText().trim()),
                    Integer.parseInt(txtExistencia.getText().trim()),
                    (Categoria) cmbCategoria.getSelectedItem());
            service.guardarProducto(p);
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
            error("Seleccione un producto para eliminar.");
            return;
        }
        int confirmacion = JOptionPane.showConfirmDialog(this, "¿Eliminar el producto seleccionado?",
                "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirmacion != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            service.eliminarProducto(productoId);
            limpiar();
            cargarProductos();
        } catch (DataAccessException e) {
            error(e.getMessage());
        }
    }

    private void limpiar() {
        productoId = null;
        txtNombre.setText("");
        txtPrecio.setText("");
        txtExistencia.setText("");
        tabla.clearSelection();
        txtNombre.requestFocus();
    }

    private void abrirCategorias() {
        Runnable onChange = () -> {
            try {
                cargarCategorias();
            } catch (DataAccessException e) {
                error(e.getMessage());
            }
        };
        new CategoriaDialog(this, service, onChange).setVisible(true);
    }

    private void error(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Aviso", JOptionPane.ERROR_MESSAGE);
    }
}
