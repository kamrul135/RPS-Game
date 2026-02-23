import javax.swing.*;
import java.awt.*;

/**
 * Main application frame with CardLayout for navigation between screens
 */
public class MainFrame extends JFrame {
    private CardLayout cardLayout;
    private JPanel cardPanel;
    private HomePanel homePanel;
    private GamePanel gamePanel;
    
    // Card names for navigation
    public static final String HOME_CARD = "HOME";
    public static final String GAME_CARD = "GAME";
    
    public MainFrame() {
        initializeFrame();
        createComponents();
        setupLayout();
        showHomeScreen();
    }
    
    private void initializeFrame() {
        setTitle("Rock Paper Scissors Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setResizable(true);
        
        // Set the frame background to match theme
        getContentPane().setBackground(ThemeManager.DARK_BG_PRIMARY);
    }
    
    private void createComponents() {
        // Initialize CardLayout and main panel
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setBackground(ThemeManager.DARK_BG_PRIMARY);
        
        // Create the panels
        homePanel = new HomePanel(this);
        gamePanel = new GamePanel(this);
        
        // Add panels to card layout
        cardPanel.add(homePanel, HOME_CARD);
        cardPanel.add(gamePanel, GAME_CARD);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        add(cardPanel, BorderLayout.CENTER);
    }
    
    /**
     * Navigate to the home screen
     */
    public void showHomeScreen() {
        cardLayout.show(cardPanel, HOME_CARD);
        homePanel.requestFocus();
    }
    
    /**
     * Navigate to the game screen
     */
    public void showGameScreen() {
        cardLayout.show(cardPanel, GAME_CARD);
        gamePanel.startNewGame();
        gamePanel.requestFocus();
    }
    
    /**
     * Get the current visible panel name
     */
    public String getCurrentCard() {
        for (Component comp : cardPanel.getComponents()) {
            if (comp.isVisible()) {
                if (comp == homePanel) return HOME_CARD;
                if (comp == gamePanel) return GAME_CARD;
            }
        }
        return HOME_CARD;
    }
}