package mx.edu.inventario.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import mx.edu.inventario.model.Producto;

/** Resumen visual del estado actual del inventario. */
public class DashboardPanel extends JPanel {

    private static final Color AZUL = new Color(36, 10, 155);
    private static final Color ROJO = new Color(190, 55, 62);
    private static final Color AMBAR = new Color(221, 151, 45);
    private static final Color VERDE = new Color(45, 143, 92);

    private final JLabel total = crearValor();
    private final JLabel unidades = crearValor();
    private final JLabel agotados = crearValor();
    private final JLabel criticos = crearValor();
    private final StockChart chart = new StockChart();

    public DashboardPanel() {
        setOpaque(false);
        setLayout(new GridLayout(1, 5, 10, 0));
        add(crearTarjeta("PRODUCTOS", total, AZUL));
        add(crearTarjeta("UNIDADES", unidades, AZUL));
        add(crearTarjeta("AGOTADOS", agotados, ROJO));
        add(crearTarjeta("STOCK CRÍTICO", criticos, AMBAR));
        add(chart);
        setPreferredSize(new Dimension(900, 92));
    }

    public void actualizar(List<Producto> productos) {
        int suma = productos.stream().mapToInt(Producto::getExistencia).sum();
        int sinStock = (int) productos.stream().filter(p -> p.getExistencia() == 0).count();
        int bajo = (int) productos.stream().filter(p -> p.getExistencia() > 0 && p.getExistencia() <= 5).count();
        int advertencia = (int) productos.stream().filter(p -> p.getExistencia() > 5 && p.getExistencia() <= 10).count();
        int optimo = productos.size() - sinStock - bajo - advertencia;
        total.setText(String.valueOf(productos.size()));
        unidades.setText(String.valueOf(suma));
        agotados.setText(String.valueOf(sinStock));
        criticos.setText(String.valueOf(bajo));
        chart.actualizar(sinStock + bajo, advertencia, optimo);
    }

    private JPanel crearTarjeta(String titulo, JLabel valor, Color acento) {
        JPanel panel = new JPanel(new java.awt.BorderLayout(4, 2));
        panel.setBackground(new Color(253, 252, 255));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 5, 0, 0, acento),
                BorderFactory.createEmptyBorder(12, 14, 10, 10)));
        JLabel etiqueta = new JLabel(titulo);
        etiqueta.setForeground(new Color(91, 105, 112));
        etiqueta.setFont(etiqueta.getFont().deriveFont(Font.BOLD, 11f));
        panel.add(etiqueta, java.awt.BorderLayout.NORTH);
        panel.add(valor, java.awt.BorderLayout.CENTER);
        return panel;
    }

    private static JLabel crearValor() {
        JLabel label = new JLabel("0");
        label.setForeground(new Color(30, 42, 48));
        label.setFont(label.getFont().deriveFont(Font.BOLD, 25f));
        return label;
    }

    private static class StockChart extends JPanel {
        private int critico;
        private int advertencia;
        private int optimo;

        StockChart() {
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createEmptyBorder(10, 12, 8, 12));
        }

        void actualizar(int critico, int advertencia, int optimo) {
            this.critico = critico;
            this.advertencia = advertencia;
            this.optimo = optimo;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(new Color(91, 105, 112));
            g.setFont(getFont().deriveFont(Font.BOLD, 11f));
            g.drawString("ESTADO DEL STOCK", 10, 15);
            int max = Math.max(1, Math.max(critico, Math.max(advertencia, optimo)));
            dibujarBarra(g, "Crítico", critico, max, 31, ROJO);
            dibujarBarra(g, "Alerta", advertencia, max, 49, AMBAR);
            dibujarBarra(g, "Óptimo", optimo, max, 67, VERDE);
        }

        private void dibujarBarra(Graphics g, String texto, int valor, int max, int y, Color color) {
            g.setFont(getFont().deriveFont(10f));
            g.setColor(new Color(80, 92, 99));
            g.drawString(texto, 10, y + 9);
            int ancho = Math.max(2, (getWidth() - 75) * valor / max);
            g.setColor(color);
            g.fillRoundRect(56, y, ancho, 9, 6, 6);
            g.drawString(String.valueOf(valor), Math.min(getWidth() - 15, 60 + ancho), y + 9);
        }
    }
}
