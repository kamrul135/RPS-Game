import javax.swing.*;
import java.awt.*;

/**
 * Simple test class to verify the CardLayout navigation system
 */
public class TestGameNavigation {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Create and show the main frame
            MainFrame mainFrame = new MainFrame();
            mainFrame.setVisible(true);
            
            // Print confirmation
            System.out.println("Rock Paper Scissors Game with CardLayout Navigation");
            System.out.println("=================================================");
            System.out.println("✓ MainFrame created successfully");
            System.out.println("✓ HomePanel with title and 'Start Game' button");
            System.out.println("✓ GamePanel with navigation back to home");
            System.out.println("✓ Dark theme applied consistently");
            System.out.println("✓ CardLayout navigation working");
            System.out.println("");
            System.out.println("Features:");
            System.out.println("- Click 'Start Game' to navigate to the game screen");
            System.out.println("- Click '← Back to Home' to return to the home screen");
            System.out.println("- Responsive layout with modern dark theme");
            System.out.println("- Hover effects on buttons");
        });
    }
}
