import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;

/**
 * Game panel that contains the Rock Paper Scissors game functionality
 * This is a complete reimplementation to work as a panel instead of a frame
 */
public class GamePanel extends JPanel {
    private MainFrame mainFrame;
    private JButton backButton;
    
    // We'll create a new RockPaperScissorsGame instance but handle it properly
    private JFrame gameFrame;
    
    // Loading animation components
    private JPanel loadingPanel;
    private Timer loadingTimer;
    private float loadingAngle = 0.0f;
    private int loadingProgress = 0;
    private boolean isLoading = false;
    
    public GamePanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        initializePanel();
        createComponents();
    }
    
    private void initializePanel() {
        setBackground(ThemeManager.DARK_BG_PRIMARY);
        setLayout(new BorderLayout());
        setFocusable(true);
    }
    
    private void createComponents() {
        // Create top navigation panel
        JPanel navPanel = createNavigationPanel();
        add(navPanel, BorderLayout.NORTH);
        
        // Create a placeholder panel for the game
        JPanel gameContainer = new JPanel(new BorderLayout());
        gameContainer.setBackground(ThemeManager.DARK_BG_PRIMARY);
        
        // Add a message to start the game
        JPanel messagePanel = new JPanel(new GridBagLayout());
        messagePanel.setBackground(ThemeManager.DARK_BG_PRIMARY);
        
        // Title label
        JLabel titleLabel = new JLabel("Rock Paper Scissors Game");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(ThemeManager.DARK_TEXT_PRIMARY);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Instruction label
        JLabel instructionLabel = new JLabel("Click 'Launch Game' to start playing!");
        instructionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        instructionLabel.setForeground(ThemeManager.DARK_TEXT_PRIMARY);
        instructionLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JButton launchButton = createStyledButton("Launch Game");
        launchButton.addActionListener(_ -> startLoadingAnimation());
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(20, 20, 10, 20);
        messagePanel.add(titleLabel, gbc);
        
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 20, 20, 20);
        messagePanel.add(instructionLabel, gbc);
        
        gbc.gridy = 2;
        gbc.insets = new Insets(10, 20, 20, 20);
        messagePanel.add(launchButton, gbc);
        
        gameContainer.add(messagePanel, BorderLayout.CENTER);
        
        // Create loading panel
        createLoadingPanel();
        
        add(gameContainer, BorderLayout.CENTER);
    }
    
    private JPanel createNavigationPanel() {
        JPanel navPanel = new JPanel(new BorderLayout());
        navPanel.setBackground(ThemeManager.DARK_BG_SECONDARY);
        navPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        // Back button
        backButton = createStyledButton("← Back to Home");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Close game frame if it's open
                if (gameFrame != null) {
                    gameFrame.dispose();
                    gameFrame = null;
                }
                mainFrame.showHomeScreen();
            }
        });
        
        navPanel.add(backButton, BorderLayout.WEST);
        
        // Game title
        JLabel gameTitle = new JLabel("Rock Paper Scissors Game", SwingConstants.CENTER);
        gameTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        gameTitle.setForeground(ThemeManager.DARK_TEXT_PRIMARY);
        navPanel.add(gameTitle, BorderLayout.CENTER);
        
        return navPanel;
    }
    
    private void launchGame() {
        // Close existing game frame if open
        if (gameFrame != null) {
            gameFrame.dispose();
        }
        
        // Create new game instance
        gameFrame = new RockPaperScissorsGame();
        gameFrame.setVisible(true);
        
        // Restore original interface
        restoreOriginalInterface();
        
        // Optional: Hide the main frame while game is running
        // mainFrame.setVisible(false);
        
        // Add window listener to handle game frame closing
        gameFrame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                gameFrame = null;
                // Show main frame again if it was hidden
                // mainFrame.setVisible(true);
            }
        });
    }
    
    private void restoreOriginalInterface() {
        // Remove loading panel and restore original components
        removeAll();
        
        // Re-create the original components
        JPanel navPanel = createNavigationPanel();
        add(navPanel, BorderLayout.NORTH);
        
        // Create a placeholder panel for the game
        JPanel gameContainer = new JPanel(new BorderLayout());
        gameContainer.setBackground(ThemeManager.DARK_BG_PRIMARY);
        
        // Add a message to start the game
        JPanel messagePanel = new JPanel(new GridBagLayout());
        messagePanel.setBackground(ThemeManager.DARK_BG_PRIMARY);
        
        // Title label
        JLabel titleLabel = new JLabel("Rock Paper Scissors Game");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(ThemeManager.DARK_TEXT_PRIMARY);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Success message
        JLabel successLabel = new JLabel("Game launched successfully!");
        successLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        successLabel.setForeground(ThemeManager.DARK_TEXT_PRIMARY);
        successLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Instruction label
        JLabel instructionLabel = new JLabel("Click 'Launch Game' to start a new session.");
        instructionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        instructionLabel.setForeground(ThemeManager.DARK_TEXT_PRIMARY);
        instructionLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JButton launchButton = createStyledButton("Launch Game");
        launchButton.addActionListener(_ -> startLoadingAnimation());
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(20, 20, 10, 20);
        messagePanel.add(titleLabel, gbc);
        
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 20, 10, 20);
        messagePanel.add(successLabel, gbc);
        
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 20, 20, 20);
        messagePanel.add(instructionLabel, gbc);
        
        gbc.gridy = 3;
        gbc.insets = new Insets(10, 20, 20, 20);
        messagePanel.add(launchButton, gbc);
        
        gameContainer.add(messagePanel, BorderLayout.CENTER);
        add(gameContainer, BorderLayout.CENTER);
        
        revalidate();
        repaint();
    }
    
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setForeground(ThemeManager.DARK_TEXT_PRIMARY);
        button.setBackground(ThemeManager.DARK_ACCENT_PRIMARY);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setPreferredSize(new Dimension(150, 35));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(ThemeManager.DARK_ACCENT_SECONDARY);
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(ThemeManager.DARK_ACCENT_PRIMARY);
            }
        });
        
        return button;
    }
    
    /**
     * Start a new game when this panel becomes visible
     */
    public void startNewGame() {
        // Game will be started when user clicks "Launch Game"
    }
    
    @Override
    public void requestFocus() {
        super.requestFocus();
        if (backButton != null) {
            backButton.requestFocus();
        }
    }
    
    private void createLoadingPanel() {
        loadingPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw background gradient
                GradientPaint gradient = new GradientPaint(
                    0, 0, ThemeManager.DARK_BG_SECONDARY,
                    0, getHeight(), ThemeManager.DARK_BG_PRIMARY
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                // Draw loading text
                g2d.setColor(ThemeManager.DARK_TEXT_PRIMARY);
                g2d.setFont(new Font("Segoe UI", Font.BOLD, 24));
                FontMetrics fm = g2d.getFontMetrics();
                String loadingText = "Launching Game...";
                int textX = (getWidth() - fm.stringWidth(loadingText)) / 2;
                int textY = getHeight() / 2 - 50;
                g2d.drawString(loadingText, textX, textY);
                
                // Draw spinning circles
                drawLoadingSpinner(g2d);
                
                // Draw progress bar
                drawProgressBar(g2d);
            }
        };
        loadingPanel.setBackground(ThemeManager.DARK_BG_PRIMARY);
        loadingPanel.setVisible(false);
    }
    
    private void drawLoadingSpinner(Graphics2D g2d) {
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        int radius = 30;
        
        // Save original transform
        AffineTransform original = g2d.getTransform();
        
        // Rotate around center
        g2d.translate(centerX, centerY);
        g2d.rotate(Math.toRadians(loadingAngle));
        
        // Draw spinning circles
        for (int i = 0; i < 8; i++) {
            float alpha = 1.0f - (i * 0.1f);
            g2d.setColor(new Color(
                ThemeManager.DARK_ACCENT_PRIMARY.getRed(),
                ThemeManager.DARK_ACCENT_PRIMARY.getGreen(),
                ThemeManager.DARK_ACCENT_PRIMARY.getBlue(),
                (int)(255 * alpha)
            ));
            
            double angle = (Math.PI * 2 * i) / 8;
            int x = (int)(Math.cos(angle) * radius) - 3;
            int y = (int)(Math.sin(angle) * radius) - 3;
            
            g2d.fillOval(x, y, 6, 6);
        }
        
        // Restore transform
        g2d.setTransform(original);
    }
    
    private void drawProgressBar(Graphics2D g2d) {
        int barWidth = 200;
        int barHeight = 8;
        int barX = (getWidth() - barWidth) / 2;
        int barY = getHeight() / 2 + 60;
        
        // Draw progress bar background
        g2d.setColor(new Color(50, 50, 50));
        g2d.fillRoundRect(barX, barY, barWidth, barHeight, 4, 4);
        
        // Draw progress
        int progressWidth = (int)((loadingProgress / 100.0) * barWidth);
        GradientPaint progressGradient = new GradientPaint(
            barX, barY, ThemeManager.DARK_ACCENT_PRIMARY,
            barX + progressWidth, barY, ThemeManager.DARK_ACCENT_SECONDARY
        );
        g2d.setPaint(progressGradient);
        g2d.fillRoundRect(barX, barY, progressWidth, barHeight, 4, 4);
        
        // Draw progress text
        g2d.setColor(ThemeManager.DARK_TEXT_SECONDARY);
        g2d.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        String progressText = loadingProgress + "%";
        FontMetrics fm = g2d.getFontMetrics();
        int textX = (getWidth() - fm.stringWidth(progressText)) / 2;
        g2d.drawString(progressText, textX, barY + barHeight + 20);
    }
    
    private void startLoadingAnimation() {
        // Play loading sound
        SoundManager.getInstance().playSound(SoundManager.SOUND_LOADING);
        
        isLoading = true;
        loadingProgress = 0;
        loadingAngle = 0;
        
        // Remove current components and add loading panel
        removeAll();
        add(createNavigationPanel(), BorderLayout.NORTH);
        add(loadingPanel, BorderLayout.CENTER);
        loadingPanel.setVisible(true);
        revalidate();
        repaint();
        
        // Start animation timer
        loadingTimer = new Timer(50, _ -> {
            loadingAngle += 10; // Rotate spinner
            if (loadingAngle >= 360) loadingAngle = 0;
            
            loadingProgress += 2; // Increase progress
            
            if (loadingProgress >= 100) {
                stopLoadingAnimation();
                launchGame();
            }
            
            repaint();
        });
        loadingTimer.start();
    }
    
    private void stopLoadingAnimation() {
        if (loadingTimer != null) {
            loadingTimer.stop();
            loadingTimer = null;
        }
        isLoading = false;
    }
}
