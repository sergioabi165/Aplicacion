package mx.edu.inventario.ui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.Timer;

/** Componentes visuales reutilizables con bordes suaves y microinteracciones. */
public final class UiKit {

    private UiKit() {
    }

    public static class RoundedPanel extends JPanel {
        private final int radio;
        private Color colorFondo;

        public RoundedPanel(java.awt.LayoutManager layout, Color fondo, int radio) {
            super(layout);
            this.radio = radio;
            this.colorFondo = fondo;
            setOpaque(false);
        }

        public void setColorFondo(Color color) {
            colorFondo = color;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(colorFondo);
            g.fillRoundRect(0, 0, getWidth(), getHeight(), radio, radio);
            g.dispose();
            super.paintComponent(graphics);
        }
    }

    public static class HoverButton extends JButton {
        private final Color normal;
        private final Color hover;
        private final int radio;
        private float progreso;
        private boolean entrando;
        private final Timer animacion;

        public HoverButton(String texto, Color normal, Color hover, int radio) {
            super(texto);
            this.normal = normal;
            this.hover = hover;
            this.radio = radio;
            setOpaque(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setForeground(colorDeTexto(normal));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            animacion = new Timer(16, e -> animar());
            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { entrando = true; iniciar(); }
                @Override public void mouseExited(MouseEvent e) { entrando = false; iniciar(); }
            });
        }

        private void iniciar() {
            if (!animacion.isRunning()) {
                animacion.start();
            }
        }

        private void animar() {
            progreso += entrando ? 0.12f : -0.12f;
            progreso = Math.max(0f, Math.min(1f, progreso));
            repaint();
            if ((entrando && progreso >= 1f) || (!entrando && progreso <= 0f)) {
                animacion.stop();
            }
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(mezclar(normal, hover, progreso));
            g.fillRoundRect(0, 0, getWidth(), getHeight(), radio, radio);
            g.dispose();
            super.paintComponent(graphics);
        }

        private Color mezclar(Color desde, Color hasta, float t) {
            int r = (int) (desde.getRed() + (hasta.getRed() - desde.getRed()) * t);
            int g = (int) (desde.getGreen() + (hasta.getGreen() - desde.getGreen()) * t);
            int b = (int) (desde.getBlue() + (hasta.getBlue() - desde.getBlue()) * t);
            return new Color(r, g, b);
        }

        private Color colorDeTexto(Color fondo) {
            double luminosidad = 0.2126 * fondo.getRed() + 0.7152 * fondo.getGreen()
                    + 0.0722 * fondo.getBlue();
            return luminosidad > 165 ? new Color(42, 31, 82) : Color.WHITE;
        }
    }
}
