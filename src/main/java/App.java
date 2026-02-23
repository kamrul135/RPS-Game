import javax.swing.SwingUtilities;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Main application entry point for Rock Paper Scissors Game
 */
public class App {
    private static final Logger LOGGER = Logger.getLogger(App.class.getName());
    
    public static void main(String[] args) {
        // Set up logging
        ConfigManager config = ConfigManager.getInstance();
        if (config.isDebugMode()) {
            LOGGER.info("Starting Rock Paper Scissors Game in debug mode...");
        }
          // Launch the game on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            try {
                new MainFrame().setVisible(true);
                LOGGER.info("Rock Paper Scissors Game launched successfully");
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Failed to launch the game", e);
                System.err.println("Failed to start the game: " + e.getMessage());
                System.exit(1);
            }
        });
    }
}
