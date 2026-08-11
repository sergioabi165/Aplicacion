package mx.edu.inventario.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.List;
import java.util.function.Consumer;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import mx.edu.inventario.model.Producto;

/** Catálogo visual de productos inspirado en una terminal de punto de venta. */
public class ProductGridPanel extends JPanel {

    private static final Color MORADO = new Color(36, 10, 155);
    private final Consumer<Producto> onSelect;

    public ProductGridPanel(Consumer<Producto> onSelect) {
        super(new FlowLayout(FlowLayout.LEFT, 14, 14));
        this.onSelect = onSelect;
        setBackground(new Color(242, 242, 244));
        setBorder(BorderFactory.createEmptyBorder(4, 4, 20, 4));
    }

    public void mostrar(List<Producto> productos) {
        removeAll();
        if (productos.isEmpty()) {
            JLabel vacio = new JLabel("No hay productos para mostrar");
            vacio.setForeground(new Color(120, 120, 126));
            vacio.setBorder(BorderFactory.createEmptyBorder(35, 25, 0, 0));
            add(vacio);
        } else {
            productos.forEach(producto -> add(crearTarjeta(producto)));
        }
        revalidate();
        repaint();
    }

    private JButton crearTarjeta(Producto producto) {
        JButton tarjeta = new UiKit.HoverButton("", Color.WHITE, new Color(247, 244, 255), 18);
        tarjeta.setPreferredSize(new Dimension(184, 118));
        tarjeta.setLayout(new java.awt.BorderLayout(0, 4));
        tarjeta.setBorder(BorderFactory.createEmptyBorder(7, 7, 8, 7));

        String nombre = producto.getNombre().length() > 24
                ? producto.getNombre().substring(0, 22) + "…" : producto.getNombre();
        String colorEstado = String.format("#%02x%02x%02x", colorStock(producto.getExistencia()).getRed(),
                colorStock(producto.getExistencia()).getGreen(), colorStock(producto.getExistencia()).getBlue());
        JLabel datos = new JLabel("<html><b>" + escapar(nombre) + "</b><br>"
                + "<font color='#777777'>" + escapar(producto.getSku()) + " · Stock " + producto.getExistencia() + "</font><br>"
                + "<font color='#240A9B'><b>$ " + String.format("%,.2f", producto.getPrecio()) + "</b></font>  "
                + "<font color='" + colorEstado + "'>● " + estado(producto.getExistencia()) + "</font></html>");
        datos.setFont(datos.getFont().deriveFont(11f));
        datos.setHorizontalAlignment(SwingConstants.LEFT);
        tarjeta.add(datos, java.awt.BorderLayout.CENTER);
        tarjeta.setToolTipText(producto.getNombre() + " · " + estado(producto.getExistencia()));
        tarjeta.addActionListener(e -> onSelect.accept(producto));
        return tarjeta;
    }

    private Color colorStock(int stock) {
        if (stock <= 5) {
            return new Color(218, 62, 70);
        }
        if (stock <= 10) {
            return new Color(244, 170, 40);
        }
        return new Color(72, 188, 112);
    }

    private String estado(int stock) {
        return stock == 0 ? "Agotado" : stock <= 5 ? "Stock crítico" : stock <= 10 ? "Advertencia" : "Stock óptimo";
    }

    private String escapar(String texto) {
        return texto == null ? "" : texto.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

}
