package mx.edu.inventario.ui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;

/** FlowLayout que calcula su altura para distribuir componentes en varias filas. */
public class ResponsiveWrapLayout extends FlowLayout {

    public ResponsiveWrapLayout(int align, int hgap, int vgap) {
        super(align, hgap, vgap);
    }

    @Override
    public Dimension preferredLayoutSize(Container target) {
        return calcularTamano(target, true);
    }

    @Override
    public Dimension minimumLayoutSize(Container target) {
        Dimension minimum = calcularTamano(target, false);
        minimum.width -= getHgap() + 1;
        return minimum;
    }

    private Dimension calcularTamano(Container target, boolean preferido) {
        synchronized (target.getTreeLock()) {
            int anchoDisponible = target.getWidth();
            if (anchoDisponible <= 0) {
                anchoDisponible = Integer.MAX_VALUE;
            }

            Insets insets = target.getInsets();
            int anchoMaximo = anchoDisponible - insets.left - insets.right - getHgap() * 2;
            Dimension total = new Dimension(0, 0);
            int anchoFila = 0;
            int altoFila = 0;

            for (Component componente : target.getComponents()) {
                if (!componente.isVisible()) {
                    continue;
                }
                Dimension tamano = preferido ? componente.getPreferredSize() : componente.getMinimumSize();
                if (anchoFila > 0 && anchoFila + getHgap() + tamano.width > anchoMaximo) {
                    agregarFila(total, anchoFila, altoFila);
                    anchoFila = 0;
                    altoFila = 0;
                }
                if (anchoFila > 0) {
                    anchoFila += getHgap();
                }
                anchoFila += tamano.width;
                altoFila = Math.max(altoFila, tamano.height);
            }
            agregarFila(total, anchoFila, altoFila);
            total.width += insets.left + insets.right + getHgap() * 2;
            total.height += insets.top + insets.bottom + getVgap() * 2;
            return total;
        }
    }

    private void agregarFila(Dimension total, int ancho, int alto) {
        total.width = Math.max(total.width, ancho);
        if (total.height > 0) {
            total.height += getVgap();
        }
        total.height += alto;
    }
}
