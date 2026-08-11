package mx.edu.inventario.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.util.List;
import java.util.function.Consumer;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
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
        super(new FlowLayout(FlowLayout.LEFT, 10, 10));
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
        JButton tarjeta = new JButton();
        tarjeta.setPreferredSize(new Dimension(178, 184));
        tarjeta.setLayout(new java.awt.BorderLayout(0, 4));
        tarjeta.setBackground(Color.WHITE);
        tarjeta.setFocusPainted(false);
        tarjeta.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(colorStock(producto.getExistencia()), 2),
                BorderFactory.createEmptyBorder(5, 5, 6, 5)));

        JLabel imagen = new JLabel(icono(producto), SwingConstants.CENTER);
        imagen.setOpaque(true);
        imagen.setBackground(new Color(237, 236, 243));
        tarjeta.add(imagen, java.awt.BorderLayout.CENTER);

        String nombre = producto.getNombre().length() > 24
                ? producto.getNombre().substring(0, 22) + "…" : producto.getNombre();
        JLabel datos = new JLabel("<html><b>" + escapar(nombre) + "</b><br>"
                + "<font color='#777777'>" + escapar(producto.getSku()) + " · Stock " + producto.getExistencia() + "</font><br>"
                + "<font color='#240A9B'><b>$ " + String.format("%,.2f", producto.getPrecio()) + "</b></font></html>");
        datos.setFont(datos.getFont().deriveFont(11f));
        tarjeta.add(datos, java.awt.BorderLayout.SOUTH);
        tarjeta.setToolTipText(producto.getNombre() + " · " + estado(producto.getExistencia()));
        tarjeta.addActionListener(e -> onSelect.accept(producto));
        return tarjeta;
    }

    private javax.swing.Icon icono(Producto producto) {
        if (producto.getFoto() != null && !producto.getFoto().isBlank()
                && new File(producto.getFoto()).isFile()) {
            Image original = new ImageIcon(producto.getFoto()).getImage();
            return new ImageIcon(original.getScaledInstance(164, 112, Image.SCALE_SMOOTH));
        }
        return new PlaceholderIcon(producto.getNombre());
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

    private static class PlaceholderIcon implements javax.swing.Icon {
        private final String iniciales;

        PlaceholderIcon(String nombre) {
            String[] partes = nombre == null ? new String[0] : nombre.trim().split("\\s+");
            iniciales = partes.length == 0 ? "P" : partes.length == 1
                    ? partes[0].substring(0, 1).toUpperCase()
                    : (partes[0].substring(0, 1) + partes[1].substring(0, 1)).toUpperCase();
        }

        @Override
        public void paintIcon(java.awt.Component c, Graphics g, int x, int y) {
            g.setColor(new Color(231, 228, 248));
            g.fillRect(x, y, getIconWidth(), getIconHeight());
            g.setColor(MORADO);
            g.setFont(c.getFont().deriveFont(Font.BOLD, 32f));
            java.awt.FontMetrics fm = g.getFontMetrics();
            g.drawString(iniciales, x + (getIconWidth() - fm.stringWidth(iniciales)) / 2, y + 68);
        }

        @Override public int getIconWidth() { return 164; }
        @Override public int getIconHeight() { return 112; }
    }
}
