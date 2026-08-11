package mx.edu.inventario;

import java.awt.Font;
import java.awt.Color;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import mx.edu.inventario.db.DatabaseConnection;
import mx.edu.inventario.ui.MainFrame;

/** Punto de entrada de la aplicación. */
public final class App {

    private App() {
    }

    public static void main(String[] args) {
        try {
            DatabaseConnection.initializeDatabase();
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            UIManager.put("defaultFont", new Font("Segoe UI", Font.PLAIN, 13));
            UIManager.put("ToolTip.font", new Font("Segoe UI", Font.PLAIN, 12));
            UIManager.put("Button.foreground", new Color(42, 31, 82));
            SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "No fue posible iniciar la aplicación: " + e.getMessage(),
                    "Error de inicio", JOptionPane.ERROR_MESSAGE);
        }
    }
}