package mx.edu.inventario.ui;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import mx.edu.inventario.exception.DataAccessException;
import mx.edu.inventario.exception.ValidationException;
import mx.edu.inventario.model.Categoria;
import mx.edu.inventario.service.InventarioService;

public class CategoriaDialog extends JDialog {

    private final InventarioService service;
    private final Runnable onChange;
    private final JTextField txtNombre = new JTextField();
    private final JTextField txtDescripcion = new JTextField();
    private final DefaultTableModel model = new DefaultTableModel(new String[]{"ID", "Nombre", "Descripción"}, 0) {
        @Override
        public boolean isCellEditable(int row, int col) {
            return false;
        }
    };
    private final JTable tabla = new JTable(model);
    private Integer categoriaId;

    public CategoriaDialog(Frame parent, InventarioService service, Runnable onChange) {
        super(parent, "Categorías", true);
        this.service = service;
        this.onChange = onChange;
        setSize(600, 400);
        setLocationRelativeTo(parent);
        construir();
        cargar();
    }

    private void construir() {
        setLayout(new BorderLayout(7, 7));

        JPanel form = new JPanel(new GridLayout(2, 2, 5, 5));
        form.setBorder(BorderFactory.createTitledBorder("Datos de categoría"));
        form.add(new JLabel("Nombre"));
        form.add(txtNombre);
        form.add(new JLabel("Descripción"));
        form.add(txtDescripcion);
        add(form, BorderLayout.NORTH);

        tabla.setAutoCreateRowSorter(true);
        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tabla.getSelectedRow() >= 0) {
                seleccionar(tabla.getSelectedRow());
            }
        });
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        JPanel botones = new JPanel();
        JButton nuevo = new JButton("Nuevo");
        JButton guardar = new JButton("Guardar");
        JButton eliminar = new JButton("Eliminar");
        nuevo.addActionListener(e -> limpiar());
        guardar.addActionListener(e -> guardar());
        eliminar.addActionListener(e -> eliminar());
        botones.add(nuevo);
        botones.add(guardar);
        botones.add(eliminar);
        add(botones, BorderLayout.SOUTH);
    }

    private void seleccionar(int fila) {
        int modelRow = tabla.convertRowIndexToModel(fila);
        categoriaId = (Integer) model.getValueAt(modelRow, 0);
        txtNombre.setText((String) model.getValueAt(modelRow, 1));
        txtDescripcion.setText((String) model.getValueAt(modelRow, 2));
    }

    private void cargar() {
        try {
            model.setRowCount(0);
            for (Categoria c : service.categorias()) {
                model.addRow(new Object[]{c.getId(), c.getNombre(), c.getDescripcion()});
            }
        } catch (DataAccessException e) {
            error(e.getMessage());
        }
    }

    private void guardar() {
        try {
            Categoria c = new Categoria(categoriaId, txtNombre.getText().trim(), txtDescripcion.getText().trim());
            service.guardarCategoria(c);
            limpiar();
            cargar();
            onChange.run();
        } catch (ValidationException | DataAccessException e) {
            error(e.getMessage());
        }
    }

    private void eliminar() {
        if (categoriaId == null) {
            error("Seleccione una categoría.");
            return;
        }
        int confirmacion = JOptionPane.showConfirmDialog(this, "¿Eliminar la categoría seleccionada?",
                "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirmacion != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            service.eliminarCategoria(categoriaId);
            limpiar();
            cargar();
            onChange.run();
        } catch (DataAccessException e) {
            error(e.getMessage());
        }
    }

    private void limpiar() {
        categoriaId = null;
        txtNombre.setText("");
        txtDescripcion.setText("");
        tabla.clearSelection();
    }

    private void error(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Aviso", JOptionPane.ERROR_MESSAGE);
    }
}
