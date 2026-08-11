package mx.edu.inventario.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

/** Comunica el nivel de inventario mediante texto y color accesible. */
public class StockCellRenderer extends DefaultTableCellRenderer {

    public StockCellRenderer() {
        setHorizontalAlignment(SwingConstants.CENTER);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean selected,
                                                    boolean focused, int row, int column) {
        super.getTableCellRendererComponent(table, value, selected, focused, row, column);
        int modelRow = table.convertRowIndexToModel(row);
        int stock = (Integer) table.getModel().getValueAt(modelRow, 4);
        String estado = stock == 0 ? "AGOTADO" : stock <= 5 ? "CRÍTICO" : stock <= 10 ? "ADVERTENCIA" : "ÓPTIMO";
        if (table.convertColumnIndexToModel(column) == 5) {
            setText(estado);
            setFont(getFont().deriveFont(Font.BOLD, 11f));
        }
        if (!selected) {
            setBackground(row % 2 == 0 ? Color.WHITE : new Color(244, 247, 248));
            setForeground(stock <= 5 ? new Color(176, 42, 49)
                    : stock <= 10 ? new Color(151, 94, 12) : new Color(34, 117, 73));
        }
        return this;
    }
}
