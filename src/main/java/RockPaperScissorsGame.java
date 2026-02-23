import javax.swing.*;
import javax.swing.Timer;
import javax.swing.table.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.RowFilter;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import javax.sound.sampled.*;
import java.io.*;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Rock Paper Scissors Game
 * Version: 1.0
 * A simple game implementation with PvC and PvP modes.
 * @author Md Kamrul Hasan
 */
public class RockPaperScissorsGame extends JFrame implements ActionListener {
    private static final long serialVersionUID = 1L;  // Add serialVersionUID
    private JButton rockBtn, paperBtn, scissorsBtn, startBtn;  // removed resetBtn

    private JLabel statusLabel, scoreLabel;
    private AnimatedScoreboard animatedScoreboard;
    private CountdownOverlay countdownOverlay; // Add countdown overlay
    private JComboBox<String> modeSelector;
    private String player1Name = "Player"; // Default for PvC mode
    private String player2Name = "Player 2"; // Add player2Name field
    private int player1Score = 0, player2Score = 0, roundCount = 0;  // Changed from playerScore, computerScore


    private GameManager gameManager = new GameManager(); // Add GameManager for AI
    private boolean isPvCMode = true; // Will be set when user selects mode
    private String p1Move = null;
    private String p2Move = null;
    private boolean inputReceived = false;
    private String keyPressed = null;

    private int totalRounds = 3; // only best of 3 now
    private int pvcWins = 0, pvcLosses = 0, pvcDraws = 0;
    private int pvpP1Wins = 0, pvpP2Wins = 0, pvpDraws = 0;


    // UI Components
    private GradientPaintPanel topPanel, centerPanel, bottomPanel;
    private JLabel letsPlayLabel, clockLabel, userTitle, computerTitle, userChoiceLabel, computerChoiceLabel;
    private JPanel aiDifficultyPanel, gamePanel;
    private JComboBox<String> roundSelector;
    private Timer animationTimer, clockTimer;

    // Game state and visual properties
    // Color fields removed - now using ThemeManager for premium colors
    private double animationAngle = 0.0;
    private int p1KeyPresses = 0, p2KeyPresses = 0;
    private boolean gameInProgress = false; // Track if game round is in progress
    private boolean acceptingInput = false; // Track if we should accept player input
    
    // Debug flag
    private static final boolean DEBUG_MODE = false;
    private static final String LOG_PREFIX = "[RPS Game] "; // Prefix for log messages

    // Fields for modeSelector placeholder behavior - removed as no longer needed

    // Theme management
    private boolean isDarkMode = true; // Default to dark mode
    private String themeMode = "dark"; // Options: "dark", "light", "neon", "synthwave"

    // Add game history storage
    // private java.util.List<GameRecord> gameHistory = new java.util.ArrayList<>();

    // Using external ThemeManager class for organization

    private static class GradientPaintPanel extends JPanel {
        private Color startColor;
        private Color endColor;

        public GradientPaintPanel(Color startColor, Color endColor) {
            this.startColor = startColor;
            this.endColor = endColor;
        }
        
        public void setStartColor(Color startColor) {
            this.startColor = startColor;
            repaint();
        }
        
        public void setEndColor(Color endColor) {
            this.endColor = endColor;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            int width = getWidth();
            int height = getHeight();
            GradientPaint gp = new GradientPaint(0, 0, startColor, 0, height, endColor);
            g2d.setPaint(gp);
            g2d.fillRect(0, 0, width, height);
        }
    }

    private void updateTheme() {
        // Apply theme based on themeMode
        switch (themeMode) {
            case "dark" -> applyDarkTheme();
            case "light" -> applyLightTheme();
            case "neon" -> applyNeonTheme();
            case "synthwave" -> applySynthwaveTheme();
            default -> applyDarkTheme();
        }
        
        // Force repaint of the entire UI
        SwingUtilities.updateComponentTreeUI(this);
    }
    
    private void applyDarkTheme() {
        // Apply the premium dark theme using the ThemeManager class
        topPanel.setStartColor(ThemeManager.DARK_TOP_GRADIENT_START);
        topPanel.setEndColor(ThemeManager.DARK_TOP_GRADIENT_END);
        
        centerPanel.setStartColor(ThemeManager.DARK_PANEL_START);
        centerPanel.setEndColor(ThemeManager.DARK_PANEL_END);
        
        bottomPanel.setStartColor(ThemeManager.DARK_BOTTOM_GRADIENT_START);
        bottomPanel.setEndColor(ThemeManager.DARK_BOTTOM_GRADIENT_END);
        
        // Update text colors with premium styling
        letsPlayLabel.setForeground(ThemeManager.DARK_BORDER_GOLD); // Premium gold
        statusLabel.setForeground(ThemeManager.DARK_ACCENT_PRIMARY); // Brilliant sky blue
        scoreLabel.setForeground(ThemeManager.DARK_TEXT_PRIMARY); // Pure white
        clockLabel.setForeground(ThemeManager.DARK_BORDER_PREMIUM); // Electric blue
        
        // Update player names with premium colors
        userTitle.setForeground(ThemeManager.DARK_ACCENT_SECONDARY); // Rose gold
        computerTitle.setForeground(ThemeManager.DARK_ACCENT_PRIMARY); // Brilliant sky blue
    }
    
    private void applyLightTheme() {
        // Apply the light theme using the ThemeManager class
        topPanel.setStartColor(ThemeManager.LIGHT_TOP_GRADIENT_START);
        topPanel.setEndColor(ThemeManager.LIGHT_TOP_GRADIENT_END);
        
        centerPanel.setStartColor(ThemeManager.LIGHT_PANEL_START);
        centerPanel.setEndColor(ThemeManager.LIGHT_PANEL_END);
        
        bottomPanel.setStartColor(ThemeManager.LIGHT_BOTTOM_GRADIENT_START);
        bottomPanel.setEndColor(ThemeManager.LIGHT_BOTTOM_GRADIENT_END);
        
        // Update text colors
        letsPlayLabel.setForeground(new Color(220, 20, 60)); // Crimson in light mode
        statusLabel.setForeground(new Color(0, 128, 0)); // Green
        scoreLabel.setForeground(ThemeManager.LIGHT_TEXT_PRIMARY);
        clockLabel.setForeground(ThemeManager.LIGHT_ACCENT_PRIMARY);
        
        // Update player names color
        userTitle.setForeground(new Color(199, 21, 133)); // MediumVioletRed
        computerTitle.setForeground(new Color(0, 0, 205)); // MediumBlue
    }
    
    private void applyNeonTheme() {
        // Apply the neon theme using the ThemeManager class
        topPanel.setStartColor(ThemeManager.NEON_TOP_GRADIENT_START);
        topPanel.setEndColor(ThemeManager.NEON_TOP_GRADIENT_END);
        
        centerPanel.setStartColor(ThemeManager.NEON_PANEL_START);
        centerPanel.setEndColor(ThemeManager.NEON_PANEL_END);
        
        bottomPanel.setStartColor(ThemeManager.NEON_BOTTOM_GRADIENT_START);
        bottomPanel.setEndColor(ThemeManager.NEON_BOTTOM_GRADIENT_END);
        
        // Update text colors
        letsPlayLabel.setForeground(new Color(0, 255, 0)); // Neon Green
        statusLabel.setForeground(new Color(255, 0, 128)); // Neon Pink
        scoreLabel.setForeground(ThemeManager.NEON_TEXT_PRIMARY);
        clockLabel.setForeground(ThemeManager.NEON_ACCENT_PRIMARY);
        
        // Update player names color
        userTitle.setForeground(new Color(0, 255, 255)); // Cyan
        computerTitle.setForeground(new Color(255, 50, 255)); // Magenta
    }
    
    private void applySynthwaveTheme() {
        // Apply the synthwave theme using the ThemeManager class
        topPanel.setStartColor(ThemeManager.SYNTHWAVE_TOP_GRADIENT_START);
        topPanel.setEndColor(ThemeManager.SYNTHWAVE_TOP_GRADIENT_END);
        
        centerPanel.setStartColor(ThemeManager.SYNTHWAVE_PANEL_START);
        centerPanel.setEndColor(ThemeManager.SYNTHWAVE_PANEL_END);
        
        bottomPanel.setStartColor(ThemeManager.SYNTHWAVE_BOTTOM_GRADIENT_START);
        bottomPanel.setEndColor(ThemeManager.SYNTHWAVE_BOTTOM_GRADIENT_END);
        
        // Update text colors
        letsPlayLabel.setForeground(new Color(255, 236, 139)); // Light Yellow
        statusLabel.setForeground(ThemeManager.SYNTHWAVE_ACCENT_SECONDARY); // Teal
        scoreLabel.setForeground(ThemeManager.SYNTHWAVE_TEXT_PRIMARY);
        clockLabel.setForeground(ThemeManager.SYNTHWAVE_ACCENT_PRIMARY);
        
        // Game buttons now get colors directly from ThemeManager when created
        
        // Update player names color
        userTitle.setForeground(ThemeManager.SYNTHWAVE_TEXT_PRIMARY); // Pink
        computerTitle.setForeground(ThemeManager.SYNTHWAVE_TEXT_SECONDARY); // Light Blue
    }

    public RockPaperScissorsGame() {
        this("dark", true); // Default to dark theme
    }
    
    public RockPaperScissorsGame(String themeMode, boolean isDarkMode) {
        // Set theme before initializing UI components
        this.themeMode = themeMode;
        this.isDarkMode = isDarkMode;
        
        setTitle("Rock Paper Scissors Game");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(0, 0)); // Remove spacing between panels

        // Top panel modifications with modern gradient
        topPanel = new GradientPaintPanel(
            isDarkMode ? ThemeManager.DARK_TOP_GRADIENT_START : ThemeManager.LIGHT_TOP_GRADIENT_START,
            isDarkMode ? ThemeManager.DARK_TOP_GRADIENT_END : ThemeManager.LIGHT_TOP_GRADIENT_END
        );
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0)); // Reduced padding for more compact layout

        // Add animated "Rock Paper Scissors" title at the top
        createAnimatedGameTitle();

        // Enhanced mode selector with rounded corners and placeholder
        modeSelector = new JComboBox<>(new String[]{"Player vs Computer (PvC)", "Player vs Player (PvP)"});
        modeSelector.setSelectedIndex(-1); // No selection initially to show placeholder
        modeSelector.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        modeSelector.setMaximumSize(new Dimension(350, 35));
        modeSelector.setAlignmentX(Component.CENTER_ALIGNMENT);
        modeSelector.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setHorizontalAlignment(SwingConstants.CENTER);
                
                // If no item is selected (selectedIndex is -1), show placeholder text
                if (index == -1 && value == null) {
                    setText("Choose Mode");
                    setForeground(Color.GRAY);
                    setBackground(new Color(245, 245, 245));
                } else {
                    if (isSelected) {
                        setBackground(new Color(70, 130, 180));
                        setForeground(Color.WHITE);
                    } else {
                        setBackground(new Color(245, 245, 245));
                        setForeground(new Color(50, 50, 50));
                    }
                }
                setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                return c;
            }
        });
        modeSelector.addActionListener(_ -> {
            SoundManager.getInstance().playSound(SoundManager.SOUND_BUTTON_CLICK);
            int selectedIndex = modeSelector.getSelectedIndex();

            if (selectedIndex >= 0) { // A valid game mode is selected (0 for PvC, 1 for PvP)
                isPvCMode = (selectedIndex == 0); // 0 for PvC, 1 for PvP

                if (aiDifficultyPanel != null) {
                    aiDifficultyPanel.setVisible(isPvCMode);
                }

                // Existing logic for player name input:
                if (selectedIndex == 0) { // Player vs Computer
                    String input = JOptionPane.showInputDialog(this,
                        "Enter your name:",
                        "Player Name",
                        JOptionPane.PLAIN_MESSAGE);
                    if (input != null && !input.trim().isEmpty()) {
                        player1Name = input.trim();
                    } else {
                        player1Name = "Player"; // Default if empty or cancelled
                    }
                } else if (selectedIndex == 1) { // Player vs Player
                    String input1 = JOptionPane.showInputDialog(this,
                        "Enter Player 1 name:",
                        "Player 1 Name",
                        JOptionPane.PLAIN_MESSAGE);
                    if (input1 != null && !input1.trim().isEmpty()) {
                        player1Name = input1.trim();
                    } else {
                        player1Name = "Player 1"; // Default
                    }
                    
                    String input2 = JOptionPane.showInputDialog(this,
                        "Enter Player 2 name:",
                        "Player 2 Name",
                        JOptionPane.PLAIN_MESSAGE);
                    if (input2 != null && !input2.trim().isEmpty()) {
                        player2Name = input2.trim();
                    } else {
                        player2Name = "Player 2"; // Default
                    }
                }
                
                // Update animated scoreboard with new player names
                String p1Name = player1Name.trim().isEmpty() ? (isPvCMode ? "Player" : "Player 1") : player1Name;
                String p2Name = isPvCMode ? "Computer" : (player2Name.trim().isEmpty() ? "Player 2" : player2Name);
                animatedScoreboard.updatePlayerNames(p1Name, p2Name, isPvCMode);
                
                resetGame(); // Reset game for the new mode
            }
        });
        topPanel.add(modeSelector);

        // Add round selector with enhanced styling - smaller and more compact
        JPanel roundPanel = new JPanel();
        roundPanel.setOpaque(false);
        roundPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        roundPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel roundLabel = new JLabel("Rounds: ");
        roundLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        roundLabel.setForeground(Color.WHITE);

        roundSelector = new JComboBox<>(new String[]{"Best of 3", "Best of 5", "Best of 7"});
        roundSelector.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        roundSelector.setPreferredSize(new Dimension(140, 35));
        roundSelector.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setHorizontalAlignment(SwingConstants.CENTER);
                if (isSelected) {
                    setBackground(new Color(70, 130, 180));
                    setForeground(Color.WHITE);
                } else {
                    setBackground(new Color(245, 245, 245));
                    setForeground(new Color(50, 50, 50));
                }
                setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                return c;
            }
        });
        roundSelector.addActionListener(_ -> {
            SoundManager.getInstance().playSound(SoundManager.SOUND_BUTTON_CLICK);
            String selected = (String)roundSelector.getSelectedItem();
            totalRounds = Integer.parseInt(selected.split(" ")[2]);
            resetGame();
        });

        roundPanel.add(roundLabel);
        roundPanel.add(roundSelector);
        topPanel.add(roundPanel);
        
        // Add AI difficulty selector in the top panel
        JPanel aiDifficultyPanel = new JPanel();
        aiDifficultyPanel.setOpaque(false);
        aiDifficultyPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        aiDifficultyPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel aiDifficultyLabel = new JLabel("AI Difficulty: ");
        aiDifficultyLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        aiDifficultyLabel.setForeground(Color.WHITE);

        String[] difficulties = {"Easy", "Medium", "Hard"};
        JComboBox<String> aiDifficultySelector = new JComboBox<>(difficulties);
        aiDifficultySelector.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        aiDifficultySelector.setPreferredSize(new Dimension(140, 35));
        aiDifficultySelector.setSelectedIndex(gameManager.getDifficulty().ordinal());
        aiDifficultySelector.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setHorizontalAlignment(SwingConstants.CENTER);
                if (isSelected) {
                    setBackground(new Color(70, 130, 180));
                    setForeground(Color.WHITE);
                } else {
                    setBackground(new Color(245, 245, 245));
                    setForeground(new Color(50, 50, 50));
                }
                setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                return c;
            }
        });
        aiDifficultySelector.addActionListener(_ -> {
            SoundManager.getInstance().playSound(SoundManager.SOUND_BUTTON_CLICK);
            int selectedDifficulty = aiDifficultySelector.getSelectedIndex();
            gameManager.setDifficulty(GameManager.AIDifficulty.values()[selectedDifficulty]);
            log("AI difficulty set to: " + difficulties[selectedDifficulty]);
        });

        aiDifficultyPanel.add(aiDifficultyLabel);
        aiDifficultyPanel.add(aiDifficultySelector);
        
        // Store the panel as a field so we can hide/show it based on game mode
        this.aiDifficultyPanel = aiDifficultyPanel;
        // Initially hidden since no mode is selected by default
        aiDifficultyPanel.setVisible(false);
        topPanel.add(aiDifficultyPanel);
        
        // Enhanced center panel
        centerPanel = new GradientPaintPanel(
            isDarkMode ? ThemeManager.DARK_PANEL_START : ThemeManager.LIGHT_PANEL_START,
            isDarkMode ? ThemeManager.DARK_PANEL_END : ThemeManager.LIGHT_PANEL_END
        );
        centerPanel.setLayout(new BorderLayout());

        // Add "Let's Play!" label at the top of the center panel with better styling
        letsPlayLabel = new JLabel("Let's Play!", SwingConstants.CENTER);
        letsPlayLabel.setFont(new Font("Montserrat", Font.BOLD, 30)); // Decreased font size
        letsPlayLabel.setForeground(isDarkMode ?
            new Color(255, 223, 0) : new Color(220, 20, 60)); // Gold or crimson based on theme
        letsPlayLabel.setBorder(BorderFactory.createEmptyBorder(25, 0, 25, 0));
        // Add subtle glow effect to the text
        Timer glowTimer = new Timer(100, null);
        final float[] glowIntensity = {0.8f};
        final boolean[] increasing = {true};
        glowTimer.addActionListener(_ -> {
            if (increasing[0]) {
                glowIntensity[0] += 0.02f;
                if (glowIntensity[0] >= 1.0f) {
                    glowIntensity[0] = 1.0f;
                    increasing[0] = false;
                }
            } else {
                glowIntensity[0] -= 0.02f;
                if (glowIntensity[0] <= 0.8f) {
                    glowIntensity[0] = 0.8f;
                    increasing[0] = true;
                }
            }
            letsPlayLabel.setForeground(new Color(255, (int)(223 * glowIntensity[0]), (int)(glowIntensity[0] * 0)));
            letsPlayLabel.repaint();
        });
        glowTimer.start();
        centerPanel.add(letsPlayLabel, BorderLayout.NORTH);

        // Update game panel for better visuals
        centerPanel.add(createGamePanel(), BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        // Status and score panel with modern styling
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setOpaque(false);
        statusLabel = new JLabel("Make your move!", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        statusLabel.setForeground(new Color(0, 200, 100));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        
        // Initialize AnimatedScoreboard
        animatedScoreboard = new AnimatedScoreboard();
        
        // Initialize CountdownOverlay
        countdownOverlay = new CountdownOverlay();
        
        scoreLabel = new JLabel("Player: 0 | Computer: 0", SwingConstants.CENTER);
        scoreLabel.setFont(new Font("Segoe UI", Font.PLAIN, 24));
        scoreLabel.setForeground(new Color(220, 220, 250));
        scoreLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        statusPanel.add(statusLabel, BorderLayout.NORTH);
        // Add AnimatedScoreboard to the status panel
        statusPanel.add(animatedScoreboard, BorderLayout.CENTER);
        centerPanel.add(statusPanel, BorderLayout.SOUTH);

        // Enhanced Bottom panel with modern gradient
        bottomPanel = new GradientPaintPanel(
            isDarkMode ? ThemeManager.DARK_BOTTOM_GRADIENT_START : ThemeManager.LIGHT_BOTTOM_GRADIENT_START,
            isDarkMode ? ThemeManager.DARK_BOTTOM_GRADIENT_END : ThemeManager.LIGHT_BOTTOM_GRADIENT_END
        );
        bottomPanel.setLayout(new GridLayout(2, 1, 0, 0));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(0, 50, 20, 50));
        
        // Game buttons panel with enhanced visual effects
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 50, 20)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Create premium circular glow effect behind buttons
                if (!isPvCMode && roundCount < totalRounds) {
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    
                    // Enhanced glow with premium colors
                    float glowSize = 65 + (float)(Math.sin(animationAngle) * 15); // Larger, more dynamic glow
                    float alpha = 0.4f + (float)(Math.sin(animationAngle) * 0.15); // More prominent glow
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

                    Point rockLoc = rockBtn.getLocation();
                    Point paperLoc = paperBtn.getLocation();
                    Point scissorsLoc = scissorsBtn.getLocation();

                    // Use premium theme colors for glows
                    g2d.setColor(ThemeManager.getRockColor(themeMode));
                    g2d.fillOval((int)(rockLoc.x + 45 - glowSize/2), (int)(rockLoc.y + 45 - glowSize/2),
                            (int)glowSize, (int)glowSize);
                    
                    g2d.setColor(ThemeManager.getPaperColor(themeMode));
                    g2d.fillOval((int)(paperLoc.x + 45 - glowSize/2), (int)(paperLoc.y + 45 - glowSize/2),
                            (int)glowSize, (int)glowSize);
                    
                    g2d.setColor(ThemeManager.getScissorsColor(themeMode));
                    g2d.fillOval((int)(scissorsLoc.x + 45 - glowSize/2), (int)(scissorsLoc.y + 45 - glowSize/2),
                            (int)glowSize, (int)glowSize);
                    
                    // Add secondary premium glow layer
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha * 0.6f));
                    g2d.setColor(ThemeManager.DARK_INNER_GLOW);
                    
                    float innerGlow = glowSize * 0.7f;
                    g2d.fillOval((int)(rockLoc.x + 45 - innerGlow/2), (int)(rockLoc.y + 45 - innerGlow/2),
                            (int)innerGlow, (int)innerGlow);
                    g2d.fillOval((int)(paperLoc.x + 45 - innerGlow/2), (int)(paperLoc.y + 45 - innerGlow/2),
                            (int)innerGlow, (int)innerGlow);
                    g2d.fillOval((int)(scissorsLoc.x + 45 - innerGlow/2), (int)(scissorsLoc.y + 45 - innerGlow/2),
                            (int)innerGlow, (int)innerGlow);
                }
            }
        };
        buttonPanel.setOpaque(false);

        // Create enhanced game buttons with premium colors
        rockBtn = createGameButton("👊", ThemeManager.getRockColor(themeMode), "Rock");
        paperBtn = createGameButton("✋", ThemeManager.getPaperColor(themeMode), "Paper");
        scissorsBtn = createGameButton("✌️", ThemeManager.getScissorsColor(themeMode), "Scissors");

        // Start animation timer
        animationTimer = new Timer(50, _ -> {
            animationAngle += 0.1;
            buttonPanel.repaint();
        });
        animationTimer.start();

        // Control buttons panel with enhanced styling
        JPanel extraButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        extraButtonPanel.setOpaque(false);

        // Enhanced control buttons with modern styling and hover effects
        JButton backBtn = createStyledButton("← Back", new Color(128, 128, 128), Color.WHITE);
        startBtn = createStyledButton("Play", new Color(50, 205, 50), Color.WHITE);
        JButton settingsBtn = createStyledButton("Menu", new Color(70, 130, 180), Color.WHITE);

        backBtn.addActionListener(_ -> {
            SoundManager.getInstance().playSound(SoundManager.SOUND_BUTTON_CLICK);
            // Close this game window and return to main menu
            dispose();
        });
        settingsBtn.addActionListener(_ -> {
            SoundManager.getInstance().playSound(SoundManager.SOUND_BUTTON_CLICK);
            showSettings();
        });
        startBtn.addActionListener(_ -> {
            SoundManager.getInstance().playSound(SoundManager.SOUND_BUTTON_CLICK);
            // First check if a game mode has been selected
            if (modeSelector.getSelectedIndex() == -1) {
                // No mode selected, show a message
                JOptionPane.showMessageDialog(this,
                    "Please select a game mode first (Player vs Computer or Player vs Player)",
                    "Game Mode Required",
                    JOptionPane.INFORMATION_MESSAGE);
                // Highlight the mode selector to draw attention to it
                modeSelector.requestFocusInWindow();
                return;
            }
            
            if (roundCount < totalRounds) {
                // Reset emoji boxes to default image placeholder
                if (userChoiceLabel != null && computerChoiceLabel != null) {
                    if (!isPvCMode) {
                        setImageToLabel(userChoiceLabel, "image/Player1/player1.png");
                        setImageToLabel(computerChoiceLabel, "image/Player2/player2.png");
                    } else {
                        setImageToLabel(userChoiceLabel, "image/default_user_choice.png"); 
                        setImageToLabel(computerChoiceLabel, "image/default_computer_choice.png");
                    }
                }
                p1Move = null;
                p2Move = null;
                if (isPvCMode) {
                    // Start the match timer for PvC mode
                    animatedScoreboard.startTimer();
                    startPvCCountdown();
                } else {
                    // In PvP mode, start the entire match
                    startPvPMatch();
                    // Start the match timer for PvP mode
                    animatedScoreboard.startTimer();
                }
                requestFocusInWindow();
            }
        });

        extraButtonPanel.add(backBtn);
        extraButtonPanel.add(startBtn);
        extraButtonPanel.add(settingsBtn);
        bottomPanel.add(extraButtonPanel);

        
        // Add main panels
        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent event) {
                // Play key press sound for any key
                SoundManager.getInstance().playSound(SoundManager.SOUND_KEY_PRESS);
                
                if (!isPvCMode && roundCount < totalRounds && statusLabel.getText().contains("Waiting")) {
                    acceptingInput = true; // We're accepting input now
                    char key = Character.toUpperCase(event.getKeyChar());
                    switch (key) {
                        case 'A': case 'S': case 'D':
                            p1KeyPresses++;
                            if (p1KeyPresses > 1) {
                                handleCheating(1, "attempted to change their move after selection");
                                return;
                            }
                            if (p1Move == null) {
                                if (key == 'A') p1Move = "Rock";
                                if (key == 'S') p1Move = "Paper";
                                if (key == 'D') p1Move = "Scissors";
                                statusLabel.setText(player1Name + " selected. Waiting for " + player2Name + "...");
                                // Visual feedback for P1 selection
                                if (userChoiceLabel != null) {
                                    userChoiceLabel.setBorder(BorderFactory.createLineBorder(ThemeManager.DARK_ACCENT_SECONDARY, 5)); // Rose gold
                                }
                            }
                            break;
                        case 'J': case 'K': case 'L':
                            p2KeyPresses++;
                            if (p2KeyPresses > 1) {
                                handleCheating(2, "attempted to change their move after selection");
                                return;
                            }
                            if (p2Move == null) {
                                if (key == 'J') p2Move = "Rock";
                                if (key == 'K') p2Move = "Paper";
                                if (key == 'L') p2Move = "Scissors";
                                if (p1Move != null) {
                                    statusLabel.setText("Both players have chosen. Countdown begins...");
                                } else {
                                    statusLabel.setText(player2Name + " selected. Waiting for " + player1Name + "...");
                                }
                                // Visual feedback for P2 selection
                                if (computerChoiceLabel != null) {
                                    computerChoiceLabel.setBorder(BorderFactory.createLineBorder(ThemeManager.DARK_BORDER_PREMIUM, 5)); // Electric blue
                                }
                            }
                            break;
                        default:
                            // Check for invalid keys during move selection
                            if ((key >= 'A' && key <= 'Z') || (key >= '0' && key <= '9')) {
                                // Determine which player might be trying to cheat
                                if (p1Move == null && p2Move == null) {
                                    // Both players haven't moved, hard to determine who cheated
                                    // Could be either player trying to use wrong keys
                                    String message = "Invalid key '" + key + "' pressed!\n\n" +
                                                   "Player 1 controls: A, S, D\n" +
                                                   "Player 2 controls: J, K, L";
                                    JOptionPane.showMessageDialog(RockPaperScissorsGame.this, message, "Invalid Key", JOptionPane.WARNING_MESSAGE);
                                } else if (p1Move != null && p2Move == null) {
                                    // P1 has moved, P2 hasn't - assume P2 is trying wrong keys
                                    handleCheating(2, "attempted to use invalid keys");
                                } else if (p1Move == null && p2Move != null) {
                                    // P2 has moved, P1 hasn't - assume P1 is trying wrong keys
                                    handleCheating(1, "attempted to use invalid keys");
                                }
                            }
                            break;
                    }
                    
                    if (p1Move != null && p2Move != null) {
                        acceptingInput = false; // Stop accepting input
                        gameInProgress = true; // Game round is now in progress
                        Timer startDelay = new Timer(500, _ -> {
                            countdownThenPlay(p1Move, p2Move);
                        });
                        startDelay.setRepeats(false);
                        startDelay.start();
                    }
                } else if (!acceptingInput && gameInProgress) {
                    // Game is in progress but we're not accepting input - this is cheating
                    char key = Character.toUpperCase(event.getKeyChar());
                    if ((key >= 'A' && key <= 'Z') || (key >= '0' && key <= '9')) {
                        // Determine which player pressed a key when they shouldn't
                        if (key == 'A' || key == 'S' || key == 'D') {
                            handleCheating(1, "pressed keys when input was not allowed");
                        } else if (key == 'J' || key == 'K' || key == 'L') {
                            handleCheating(2, "pressed keys when input was not allowed");
                        } else {
                            // Generic cheat detection for other keys
                            int violatingPlayer = (Math.random() < 0.5) ? 1 : 2; // Random assignment for unknown keys
                            handleCheating(violatingPlayer, "pressed unauthorized keys during game");
                        }
                    }
                }
            }
        });
        setFocusable(true);
        requestFocusInWindow();

        initializeClock(); // Initialize the clock
        
        // Apply theme settings to elements
        updateTheme();
        
        // Add countdown overlay to the layered pane so it appears on top
        setLayeredPane(new JLayeredPane());
        getLayeredPane().add(getContentPane(), JLayeredPane.DEFAULT_LAYER);
        getLayeredPane().add(countdownOverlay, JLayeredPane.POPUP_LAYER);
        
        // Make sure countdown overlay covers the entire window
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                if (countdownOverlay != null) {
                    countdownOverlay.setBounds(0, 0, getWidth(), getHeight());
                }
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        SoundManager.getInstance().playSound(SoundManager.SOUND_BUTTON_CLICK);
        if (roundCount >= totalRounds) {
            statusLabel.setText("Game Over! Final Score - " +
                    (isPvCMode ? "Player" : player1Name) + ": " + player1Score + " | " +
                    (isPvCMode ? "Computer" : player2Name) + ": " + player2Score);
            return;
        }

        JButton clickedButton = (JButton) event.getSource();
        if (isPvCMode) {
            keyPressed = clickedButton.getText();
            startPvCCountdown();
        } else if (statusLabel.getText().contains("Waiting")) {
            // PvP mode button handling
            if (p1Move == null) {
                p1Move = clickedButton.getText();
                statusLabel.setText(player1Name + " selected. Waiting for " + player2Name + "...");
                // When Player 1 makes a move, keep showing their player image
                if (userChoiceLabel != null) {
                    // Keep the player1 image when P1 moves
                    userChoiceLabel.setBorder(BorderFactory.createLineBorder(new Color(255, 105, 180), 5)); // Hot pink border
                }
            } else if (p2Move == null) {
                p2Move = clickedButton.getText();
                statusLabel.setText("Both players have chosen. Countdown begins...");
                // When Player 2 makes a move, keep showing their player image
                if (computerChoiceLabel != null) {
                    // Keep the player2 image when P2 moves
                    computerChoiceLabel.setBorder(BorderFactory.createLineBorder(new Color(30, 144, 255), 5)); // Blue border
                }
                Timer startDelay = new Timer(500, _ -> {
                    countdownThenPlay(p1Move, p2Move);
                });
                startDelay.setRepeats(false);
                startDelay.start();
            }
        }
    }

    private void startPvCCountdown() {
        p1KeyPresses = 0;
        p2KeyPresses = 0;
        statusLabel.setText("Round " + (roundCount + 1) + " - Get ready! Press R, P or S...");
        inputReceived = false;
        keyPressed = null;

        // Reset image displays at the start of countdown
        if (userChoiceLabel != null && computerChoiceLabel != null) {
            setImageToLabel(userChoiceLabel, "image/default_user_choice.png");
            setImageToLabel(computerChoiceLabel, "image/default_computer_choice.png");
            userChoiceLabel.setBorder(null);  // Clear previous highlight borders
            computerChoiceLabel.setBorder(null);  // Clear previous highlight borders
        }

        // Ensure player names are not empty
        String p1Name = player1Name.trim().isEmpty() ? (isPvCMode ? "Player" : "Player 1") : player1Name;

        // Update player names correctly in the titles for PvC mode
        if (userTitle != null && computerTitle != null && isPvCMode) {
            userTitle.setText(p1Name); // Use validated player name
            computerTitle.setText("Computer");
        }

        KeyAdapter keyListener = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent event) {
                if (!inputReceived) {
                    char key = Character.toUpperCase(event.getKeyChar());
                    switch (key) {
                        case 'R': keyPressed = "Rock"; inputReceived = true; updateEmojis("Rock", "❓"); break;
                        case 'P': keyPressed = "Paper"; inputReceived = true; updateEmojis("Paper", "❓"); break;
                        case 'S': keyPressed = "Scissors"; inputReceived = true; updateEmojis("Scissors", "❓"); break;
                    }
                }
            }
        };

        addKeyListener(keyListener);
        setFocusable(true);
        requestFocusInWindow();

        // Use visual countdown overlay instead of simple timer
        countdownOverlay.startCountdown(() -> {
            // This callback is executed when countdown finishes
            removeKeyListener(keyListener);

            if (keyPressed == null) {
                statusLabel.setText("No input received! You missed the round.");
                
                // Automatically start next round after a delay
                if (roundCount < totalRounds - 1) {
                    Timer nextRoundTimer = new Timer(2000, _ -> {
                        startPvCCountdown();
                    });
                    nextRoundTimer.setRepeats(false);
                    nextRoundTimer.start();
                } else {
                    // If this was the last round, show the final score
                    Timer endTimer = new Timer(2000, _ -> {
                        showFinalScore();
                    });
                    endTimer.setRepeats(false);
                    endTimer.start();
                }
            } else {
                // Record the player's move first
                gameManager.recordPlayerMove(keyPressed);
                // Get the computer's move using the AI
                String computerMove = gameManager.getComputerMove();
                // Update both emojis immediately when computer makes its choice
                updateEmojis(keyPressed, computerMove);
                String result = determineWinner(keyPressed, computerMove);
                
                // Ensure player names are not empty for display purposes
                String displayName = p1Name;
                
                statusLabel.setText("Round " + (roundCount + 1) + ": " +
                        displayName + ": " + keyPressed + " | " +
                        "Computer: " + computerMove +
                        " → " + result);
                        
                roundCount++;

                if (roundCount >= totalRounds || player1Score > totalRounds/2 || player2Score > totalRounds/2) {
                    Timer endTimer = new Timer(2000, _ -> {
                        statusLabel.setText("Game Over!");
                        // Stop the match timer when the game ends
                        animatedScoreboard.stopTimer();
                        showFinalScore();
                    });
                    endTimer.setRepeats(false);
                    endTimer.start();
                } else {
                    // Automatically start next round after a delay
                    Timer nextRoundTimer = new Timer(2000, _ -> {
                        startPvCCountdown();
                    });
                    nextRoundTimer.setRepeats(false);
                    nextRoundTimer.start();
                }
            }
        });
    }

    private void countdownThenPlay(String p1, String p2) {
        p1KeyPresses = 0;
        p2KeyPresses = 0;
        log("Starting countdown with moves - P1: " + p1 + ", P2: " + p2);
        
        // Update player names correctly in the titles
        if (userTitle != null && computerTitle != null) {
            userTitle.setText(player1Name);
            computerTitle.setText(player2Name);
        }
        
        // Add key monitoring during countdown for cheating detection
        KeyAdapter countdownKeyListener = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent event) {
                char key = Character.toUpperCase(event.getKeyChar());
                // Check for player 1 keys (A, S, D)
                if (key == 'A' || key == 'S' || key == 'D') {
                    p1KeyPresses++;
                    if (p1KeyPresses > 1) {
                        // Remove this listener to prevent further detection
                        removeKeyListener(this);
                        handleCheating(1, "attempted to press multiple keys during countdown");
                        return;
                    }
                }
                // Check for player 2 keys (J, K, L)
                else if (key == 'J' || key == 'K' || key == 'L') {
                    p2KeyPresses++;
                    if (p2KeyPresses > 1) {
                        // Remove this listener to prevent further detection
                        removeKeyListener(this);
                        handleCheating(2, "attempted to press multiple keys during countdown");
                        return;
                    }
                }
                // Check for any other keys that shouldn't be pressed during countdown
                else if ((key >= 'A' && key <= 'Z') || (key >= '0' && key <= '9')) {
                    // Determine which player pressed invalid key based on timing
                    // For simplicity, we'll just give a general warning
                    removeKeyListener(this);
                    // Award to a random player for invalid key usage - or we could track the timing
                    int violatingPlayer = (p1KeyPresses == 0 && p2KeyPresses == 0) ? 1 : 
                                        (p1KeyPresses > p2KeyPresses ? 1 : 2);
                    handleCheating(violatingPlayer, "pressed invalid keys during countdown");
                    return;
                }
            }
        };
        
        addKeyListener(countdownKeyListener);
        
        // Update emoji boxes with question marks for PvP mode during countdown
        if (userChoiceLabel != null && computerChoiceLabel != null) {
            if (!isPvCMode) {
                setImageToLabel(userChoiceLabel, "image/Player1/player1.png");
                setImageToLabel(computerChoiceLabel, "image/Player2/player2.png");
                userChoiceLabel.setBorder(BorderFactory.createLineBorder(new Color(255, 105, 180), 5)); // Hot pink border
                computerChoiceLabel.setBorder(BorderFactory.createLineBorder(new Color(30, 144, 255), 5)); // Blue border
            } else {
                setImageToLabel(userChoiceLabel, "image/default_user_choice.png");
                setImageToLabel(computerChoiceLabel, "image/default_computer_choice.png");
                userChoiceLabel.setBorder(null);
                computerChoiceLabel.setBorder(null);
            }
        }
        
        // Use visual countdown overlay instead of simple timer
        countdownOverlay.startCountdown(() -> {
            // Remove the key listener after countdown
            removeKeyListener(countdownKeyListener);
            
            // Only determine winner if no cheating was detected
            if (p1KeyPresses <= 1 && p2KeyPresses <= 1) {
                // Update emoji displays for both players in PvP mode
                if (userChoiceLabel != null && computerChoiceLabel != null) {
                    updateEmojis(p1, p2);
                }
                
                String result = determineWinner(p1, p2);
                statusLabel.setText("Round " + (roundCount + 1) + ": " + player1Name + ": " + p1 + " | " + player2Name + ": " + p2 + " → " + result);
                scoreLabel.setText(player1Name + ": " + player1Score + " | " + player2Name + ": " + player2Score);
                roundCount++; // Increment round counter
            }

            if (roundCount >= totalRounds || player1Score > totalRounds/2 || player2Score > totalRounds/2) {
                Timer endTimer = new Timer(2000, _ -> {
                    statusLabel.setText("Game Over!");
                    // Stop the match timer when the game ends
                    animatedScoreboard.stopTimer();
                    showFinalScore();
                });
                endTimer.setRepeats(false);
                endTimer.start();
            } else {
                // In PvP mode, rounds should continue automatically
                Timer nextRoundTimer = new Timer(2000, _ -> {
                    statusLabel.setText("Waiting for players to make their move...");
                    p1Move = null;
                    p2Move = null;
                    p1KeyPresses = 0;
                    p2KeyPresses = 0;
                    gameInProgress = false; // Reset game state
                    acceptingInput = false; // Reset input state
                    requestFocusInWindow(); // Make sure we have keyboard focus
                    
                    // Reset borders and images for player avatars
                    if (userChoiceLabel != null && computerChoiceLabel != null) {
                        setImageToLabel(userChoiceLabel, "image/Player1/player1.png");
                        setImageToLabel(computerChoiceLabel, "image/Player2/player2.png");
                        userChoiceLabel.setBorder(null);
                        computerChoiceLabel.setBorder(null);
                    }
                });
                nextRoundTimer.setRepeats(false);
                nextRoundTimer.start();
            }
        });
    }

    private void playSound(String soundFile) {
        try {
            // Load sound from classpath resources instead of direct file
            InputStream soundStream = getResourceAsStream("/sound/" + soundFile);
            if (soundStream == null) {
                log("Sound file not found: " + soundFile);
                return; // Exit silently if file doesn't exist
            }
            
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundStream);
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            clip.start();
        } catch (Exception e) {
            log("Error playing sound: " + e.getMessage());
            // Gracefully handle missing sound file
        }
    }

    /**
     * Helper method to get resource as stream from classpath
     * @param path Path to resource relative to /resources folder
     * @return InputStream for the resource or null if not found
     */
    private InputStream getResourceAsStream(String path) {
        // Try to get resource from classpath
        InputStream stream = getClass().getResourceAsStream(path);
        if (stream == null) {
            // If not found, try with /src/main/resources prefix
            stream = getClass().getResourceAsStream("/src/main/resources" + path);
        }
        if (stream == null) {
            // Try absolute path as fallback
            try {
                File file = new File("src/main/resources" + path);
                if (file.exists()) {
                    return new FileInputStream(file);
                }
            } catch (FileNotFoundException e) {
                log("Resource not found: " + path);
            }
        }
        return stream;
    }

    private String determineWinner(String player, String opponent) {
        String result;
        
        if (player.equals(opponent)) {
            result = "It's a draw!";
            if (isPvCMode) pvcDraws++;
            else pvpDraws++;
            
            // Play draw sound
            SoundManager.getInstance().playSound(SoundManager.SOUND_DRAW);
        } else if ((player.equals("Rock") && opponent.equals("Scissors")) ||
                (player.equals("Paper") && opponent.equals("Rock")) ||
                (player.equals("Scissors") && opponent.equals("Paper"))) {
            result = isPvCMode ? "Player wins!" : player1Name + " wins!";
            player1Score++;
            if (isPvCMode) pvcWins++;
            else pvpP1Wins++;
            
            // Highlight player's choice with a border
            if (userChoiceLabel != null) {
                userChoiceLabel.setBorder(BorderFactory.createLineBorder(new Color(0, 255, 0), 5));
            }
            
            // Play player round win sound
            SoundManager.getInstance().playSound(SoundManager.SOUND_PLAYER_ROUND_WIN);
        } else {
            result = isPvCMode ? "Computer wins!" : player2Name + " wins!";
            player2Score++;
            if (isPvCMode) pvcLosses++;
            else pvpP2Wins++;
            
            // Highlight computer's choice with a border
            if (computerChoiceLabel != null) {
                computerChoiceLabel.setBorder(BorderFactory.createLineBorder(new Color(0, 255, 0), 5));
            }
            
            // Play computer round win sound in PvC, player round win sound in PvP
            SoundManager.getInstance().playSound(isPvCMode ? SoundManager.SOUND_COMPUTER_ROUND_WIN : SoundManager.SOUND_PLAYER_ROUND_WIN);
        }
        
        // Ensure player names are not empty for display purposes
        String p1Name = player1Name.trim().isEmpty() ? (isPvCMode ? "Player" : "Player 1") : player1Name;
        String p2Name = isPvCMode ? "Computer" : (player2Name.trim().isEmpty() ? "Player 2" : player2Name);
        
        // Update both the old score label and the new animated scoreboard
        scoreLabel.setText(p1Name + ": " + player1Score + " | " + 
                           p2Name + ": " + player2Score);
        animatedScoreboard.updateScores(player1Score, player2Score);
        return result;
    }

    private void showFinalScore() {
        // Save game to history using GameHistoryManager
        String mode = isPvCMode ? "PvC" : "PvP";
        String p1 = player1Name.trim().isEmpty() ? (isPvCMode ? "Player" : "Player 1") : player1Name; // Use default name if empty
        String p2 = isPvCMode ? "Computer" : (player2Name.trim().isEmpty() ? "Player 2" : player2Name); // Use default name if empty
        
        // Create and save the game record
        GameHistoryManager.GameRecord record = new GameHistoryManager.GameRecord(
            mode, p1, p2, player1Score, player2Score
        );
        GameHistoryManager.getInstance().addRecord(record);
        
        // Determine result properties
        String winner;
        Color resultColor;
        String resultEmoji;
        if (player1Score == player2Score) {
            winner = "DRAW";
            resultColor = new Color(149, 165, 166);
            resultEmoji = "🤝";
            
            // Play match draw sound when the game ends in a draw
            SoundManager.getInstance().playSound(SoundManager.SOUND_MATCH_DRAW);
        } else if (player1Score > player2Score) {
            winner = isPvCMode ? "VICTORY" : player1Name + " WINS"; // Use player1Name
            resultColor = new Color(46, 204, 113);
            resultEmoji = isPvCMode ? "🏆" : "👑";  // Crown for P1 win
            
            // Play victory sound when player wins the match
            SoundManager.getInstance().playSound(SoundManager.SOUND_WIN);
        } else {
            winner = isPvCMode ? "DEFEAT" : player2Name + " WINS";
            resultColor = new Color(231, 76, 60);
            resultEmoji = isPvCMode ? "💔" : "👑";  // Crown for P2 win instead of broken heart
            
            // Play defeat sound when player loses in PvC mode, or win sound for P2 in PvP mode
            if (isPvCMode) {
                SoundManager.getInstance().playSound(SoundManager.SOUND_LOSE);
            } else {
                SoundManager.getInstance().playSound(SoundManager.SOUND_WIN);
            }
        }

        // Create and show result dialog
        GameResultPanel resultPanel = new GameResultPanel(
                winner, resultColor, resultEmoji,
                player1Score, player2Score, isPvCMode
        );

        JDialog resultDialog = new JDialog(this, "", true);
        resultDialog.setUndecorated(true);
        resultDialog.setContentPane(resultPanel);
        resultDialog.pack();
        resultDialog.setLocationRelativeTo(this);
        
        // Create a timer to dismiss the dialog and reset the game after 3 seconds
        // Make the timer non-modal to avoid blocking
        resultDialog.setModal(false);
        resultDialog.setVisible(true);
        
        // Auto-dismiss after 3 seconds and reset the game
        Timer dismissTimer = new Timer(3000, _ -> {
            resultPanel.stopAnimation();
            resultDialog.dispose();
            resetGame(); // Automatically reset the game
        });
        dismissTimer.setRepeats(false);
        dismissTimer.start();
    }

    private void showStatistics() {
        // Calculate totals
        int totalPvcGames = pvcWins + pvcLosses + pvcDraws;
        int totalPvpGames = pvpP1Wins + pvpP2Wins + pvpDraws;
        int totalGames = totalPvcGames + totalPvpGames;
        int totalWins = pvcWins + pvpP1Wins;
        int totalLosses = pvcLosses + pvpP2Wins;
        int totalDraws = pvcDraws + pvpDraws;
        
        // Use the calculateWinRate method
        double overallWinRate = calculateWinRate(totalWins, totalGames);
        double pvcWinRate = calculateWinRate(pvcWins, totalPvcGames);
        double pvpWinRate = calculateWinRate(pvpP1Wins, totalPvpGames);
        
        // Create simple statistics text
        StringBuilder stats = new StringBuilder();
        stats.append("GAME STATISTICS\n\n");
        stats.append("Player: ").append(player1Name.isEmpty() ? "Player" : player1Name).append("\n\n");
        
        stats.append("OVERALL STATS:\n");
        stats.append("Total Games: ").append(totalGames).append("\n");
        stats.append("Wins: ").append(totalWins).append("\n");
        stats.append("Losses: ").append(totalLosses).append("\n");
        stats.append("Draws: ").append(totalDraws).append("\n");
        stats.append("Win Rate: ").append(String.format("%.1f%%", overallWinRate)).append("\n\n");
        
        stats.append("PLAYER vs COMPUTER:\n");
        stats.append("Games: ").append(totalPvcGames).append("\n");
        stats.append("Wins: ").append(pvcWins).append("\n");
        stats.append("Losses: ").append(pvcLosses).append("\n");
        stats.append("Draws: ").append(pvcDraws).append("\n");
        stats.append("Win Rate: ").append(String.format("%.1f%%", pvcWinRate)).append("\n\n");
        
        stats.append("PLAYER vs PLAYER:\n");
        stats.append("Games: ").append(totalPvpGames).append("\n");
        stats.append("Player 1 Wins: ").append(pvpP1Wins).append("\n");
        stats.append("Player 2 Wins: ").append(pvpP2Wins).append("\n");
        stats.append("Draws: ").append(pvpDraws).append("\n");
        stats.append("P1 Win Rate: ").append(String.format("%.1f%%", pvpWinRate)).append("\n");
        
        // Create and show simple message dialog
        JOptionPane.showMessageDialog(
            this,
            stats.toString(),
            "📊 Game Statistics",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    // Move Game Controls button inside the settings and make settings more stylish
    private void showSettings() {
        JPanel settingsPanel = new JPanel();
        settingsPanel.setLayout(new BoxLayout(settingsPanel, BoxLayout.Y_AXIS));
        settingsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        settingsPanel.setBackground(isDarkMode ? 
            ThemeManager.DARK_BG_SECONDARY : ThemeManager.LIGHT_BG_SECONDARY);

        JLabel settingsTitle = new JLabel("Menu");
        settingsTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        settingsTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        settingsTitle.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        settingsTitle.setForeground(isDarkMode ? 
            ThemeManager.DARK_TEXT_PRIMARY : ThemeManager.LIGHT_TEXT_PRIMARY);
        
        // Settings buttons
        JButton historyButton = createSettingsButton("History", 
            ThemeManager.DARK_ACCENT_PRIMARY, ThemeManager.LIGHT_ACCENT_PRIMARY);
        historyButton.addActionListener(_ -> {
            SoundManager.getInstance().playSound(SoundManager.SOUND_BUTTON_CLICK);
            showGameHistory();
        });
        
        JButton leaderboardButton = createSettingsButton("Leaderboard", 
            ThemeManager.DARK_ACCENT_PRIMARY, ThemeManager.LIGHT_ACCENT_PRIMARY);
        leaderboardButton.addActionListener(_ -> {
            SoundManager.getInstance().playSound(SoundManager.SOUND_BUTTON_CLICK);
            showLeaderboard();
        });
        
        // Add Statistics button to the menu
        int totalGames = pvcWins + pvcLosses + pvcDraws + pvpP1Wins + pvpP2Wins + pvpDraws;
        String statisticsText = "Statistics (" + totalGames + " games)";
        JButton statisticsButton = createSettingsButton(statisticsText, 
            ThemeManager.DARK_ACCENT_PRIMARY, ThemeManager.LIGHT_ACCENT_PRIMARY);
        statisticsButton.addActionListener(_ -> {
            SoundManager.getInstance().playSound(SoundManager.SOUND_BUTTON_CLICK);
            showStatistics();
        });
        

                
        JButton resetButton = createSettingsButton("Reset Scores", 
            ThemeManager.DARK_ACCENT_SECONDARY, ThemeManager.LIGHT_ACCENT_SECONDARY);
        resetButton.addActionListener(_ -> {
            SoundManager.getInstance().playSound(SoundManager.SOUND_BUTTON_CLICK);
            resetAllStats();
        });

        settingsPanel.add(settingsTitle);
        settingsPanel.add(Box.createVerticalStrut(40));
        settingsPanel.add(historyButton);
        settingsPanel.add(Box.createVerticalStrut(15));
        settingsPanel.add(leaderboardButton);
        settingsPanel.add(Box.createVerticalStrut(15));
        settingsPanel.add(statisticsButton);
        settingsPanel.add(Box.createVerticalStrut(15));
        settingsPanel.add(resetButton);
        
        JButton exitButton = createSettingsButton("Exit Game", 
            ThemeManager.DARK_ACCENT_SECONDARY, ThemeManager.LIGHT_ACCENT_SECONDARY);
        exitButton.addActionListener(_ -> {
            SoundManager.getInstance().playSound(SoundManager.SOUND_BUTTON_CLICK);
            if (confirmExit()) {
                dispose();
            }
        });
        settingsPanel.add(Box.createVerticalStrut(15));
        settingsPanel.add(exitButton);

        JDialog settingsDialog = new JDialog(this, "Menu", true);
        settingsDialog.setContentPane(settingsPanel);
        settingsDialog.pack();
        settingsDialog.setSize(400, 450); // Increased size to accommodate new Statistics button
        settingsDialog.setLocationRelativeTo(this);
        settingsDialog.setVisible(true);
    }
    

    
    // Helper method to create consistent settings buttons
    private JButton createSettingsButton(String text, Color darkColor, Color lightColor) {
        Color buttonColor = isDarkMode ? darkColor : lightColor;
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Create gradient background
                GradientPaint gradient;
                if (getModel().isPressed()) {
                    gradient = new GradientPaint(0, 0, buttonColor.darker(), 0, getHeight(), buttonColor);
                } else if (getModel().isRollover()) {
                    gradient = new GradientPaint(0, 0, buttonColor.brighter(), 0, getHeight(), buttonColor);
                } else {
                    gradient = new GradientPaint(0, 0, buttonColor, 0, getHeight(), buttonColor.darker());
                }
                
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                
                // Draw slight shadow/highlight based on pressed state
                if (getModel().isPressed()) {
                    g2d.setColor(new Color(0, 0, 0, 50));
                    g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                } else {
                    g2d.setColor(new Color(255, 255, 255, 50));
                    g2d.drawRoundRect(2, 2, getWidth()-4, getHeight()-4, 15, 15);
                }
                
                // Draw text with shadow
                FontMetrics fm = g2d.getFontMetrics();
                Rectangle textRect = fm.getStringBounds(text, g2d).getBounds();
                
                int x = (getWidth() - textRect.width) / 2;
                int y = (getHeight() - textRect.height) / 2 + fm.getAscent();
                
                // Draw subtle text shadow
                g2d.setColor(new Color(0, 0, 0, 50));
                g2d.drawString(text, x+1, y+1);
                
                // Draw text
                g2d.setColor(Color.WHITE);
                g2d.drawString(text, x, y);
            }
        };
        
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setForeground(Color.WHITE);
        button.setBackground(buttonColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        
        // Use different dimensions based on where the button is used
        if (text.equals("Settings") || text.equals("Reset") || text.equals("Start") || text.equals("Exit")) {
            // Main interface buttons
            button.setPreferredSize(new Dimension(150, 60));
        } else {
            // Settings dialog buttons
            button.setAlignmentX(Component.CENTER_ALIGNMENT);
            button.setMaximumSize(new Dimension(300, 45));
        }
        
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        return button;
    }

    private double calculateWinRate(int wins, int total) {
        if (total == 0) return 0;
        return (wins * 100.0) / total;
    }

    private void resetAllStats() {
        // Reset game statistics
        pvcWins = 0; pvcLosses = 0; pvcDraws = 0;
        pvpP1Wins = 0; pvpP2Wins = 0; pvpDraws = 0;
        
        // Clear history and leaderboard data
        GameHistoryManager.getInstance().clearHistory();
        
        // Show confirmation message
        JOptionPane.showMessageDialog(this, 
            "All game statistics, history and leaderboard data have been reset.", 
            "Reset Complete", 
            JOptionPane.INFORMATION_MESSAGE);
        
        resetGame();
    }

    private void resetGame() {
        log("Resetting game - Mode: " + (isPvCMode ? "PvC" : "PvP"));
        player1Score = 0;
        player2Score = 0;
        roundCount = 0;
        p1Move = null;
        p2Move = null;
        gameManager.reset(); // Reset the game manager and AI
        
        // Reset the animated scoreboard
        animatedScoreboard.reset();
        
        // Update status label based on mode
        statusLabel.setText(isPvCMode ? 
            "Make your move!" : 
            "Press Start to begin the match");
            
        // Ensure player names are not empty
        String p1Name = player1Name.trim().isEmpty() ? (isPvCMode ? "Player" : "Player 1") : player1Name;
        String p2Name = player2Name.trim().isEmpty() ? "Player 2" : player2Name;
        
        scoreLabel.setText(isPvCMode ? 
            p1Name + ": 0 | Computer: 0" : 
            p1Name + ": 0 | " + p2Name + ": 0");
            
        // Update animated scoreboard with current player names
        animatedScoreboard.updatePlayerNames(p1Name, isPvCMode ? "Computer" : p2Name, isPvCMode);
            
        if (userTitle != null) {
            userTitle.setText(isPvCMode ? p1Name : p1Name);
        }
        if (computerTitle != null) {
            computerTitle.setText(isPvCMode ? "Computer" : p2Name);
        }

        // Reset emoji boxes to default image placeholder
        if (userChoiceLabel != null && computerChoiceLabel != null) {
            if (!isPvCMode) {
                setImageToLabel(userChoiceLabel, "image/Player1/player1.png");
                setImageToLabel(computerChoiceLabel, "image/Player2/player2.png");
            } else {
                setImageToLabel(userChoiceLabel, "image/default_user_choice.png");
                setImageToLabel(computerChoiceLabel, "image/default_computer_choice.png");
            }
            userChoiceLabel.setBorder(null);
            computerChoiceLabel.setBorder(null);
        }

        // If in PvP mode, auto-start the round with a waiting message
        if (!isPvCMode) {
            statusLabel.setText("Waiting for players to make their move...");
            requestFocusInWindow();
        }
    }

    private void log(String message) {
        if (DEBUG_MODE) {
            System.out.println(LOG_PREFIX + message);
        }
    }

    private boolean confirmExit() {
        int result = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to exit the game?",
                "Exit Confirmation",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        return result == JOptionPane.YES_OPTION;
    }

    private void handleCheating(int cheatingPlayer, String reason) {
        // Play cheating detection sound
        SoundManager.getInstance().playSound(SoundManager.SOUND_CHEATING);
        
        // Get player names for display
        String player1DisplayName = player1Name.trim().isEmpty() ? "Player 1" : player1Name;
        String player2DisplayName = player2Name.trim().isEmpty() ? "Player 2" : player2Name;
        String cheatingPlayerName = (cheatingPlayer == 1) ? player1DisplayName : player2DisplayName;
        String opponentName = (cheatingPlayer == 1) ? player2DisplayName : player1DisplayName;
        
        String message = " CHEATING DETECTED! \n\n" + 
                        cheatingPlayerName + " " + reason + "!\n\n" +
                        "This round is awarded to " + opponentName + ".";
        
        // Award round to the opponent
        if (cheatingPlayer == 1) {
            player2Score++;
            pvpP2Wins++;
        } else {
            player1Score++;
            pvpP1Wins++;
        }

        // Update score display with both old and new scoreboards
        scoreLabel.setText(player1DisplayName + ": " + player1Score + " | " + player2DisplayName + ": " + player2Score);
        animatedScoreboard.updateScores(player1Score, player2Score);

        // Reset moves and key press counters for next round
        p1Move = null;
        p2Move = null;
        p1KeyPresses = 0;
        p2KeyPresses = 0;

        // Create a more prominent popup dialog
        JPanel messagePanel = new JPanel();
        messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.Y_AXIS));
        messagePanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        messagePanel.setBackground(new Color(255, 245, 245)); // Light red background
        
        // Warning icon
        JLabel warningIcon = new JLabel("⚠️", SwingConstants.CENTER);
        warningIcon.setFont(new Font("Dialog", Font.BOLD, 72));
        warningIcon.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Title
        JLabel titleLabel = new JLabel("CHEATING DETECTED!", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.RED);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Message text
        JTextArea messageText = new JTextArea(message);
        messageText.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        messageText.setBackground(new Color(255, 245, 245));
        messageText.setEditable(false);
        messageText.setLineWrap(true);
        messageText.setWrapStyleWord(true);
        messageText.setAlignmentX(Component.CENTER_ALIGNMENT);
        messageText.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        
        // OK Button
        JButton okButton = new JButton("OK");
        okButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        okButton.setBackground(new Color(231, 76, 60));
        okButton.setForeground(Color.WHITE);
        okButton.setFocusPainted(false);
        okButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        okButton.setPreferredSize(new Dimension(100, 40));
        
        messagePanel.add(warningIcon);
        messagePanel.add(Box.createVerticalStrut(10));
        messagePanel.add(titleLabel);
        messagePanel.add(Box.createVerticalStrut(20));
        messagePanel.add(messageText);
        messagePanel.add(Box.createVerticalStrut(20));
        messagePanel.add(okButton);
        
        // Create modal dialog
        JDialog warningDialog = new JDialog(this, "CHEATING DETECTED!", true);
        warningDialog.setContentPane(messagePanel);
        warningDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        warningDialog.pack();
        warningDialog.setSize(450, 350);
        warningDialog.setLocationRelativeTo(this);
        warningDialog.setResizable(false);
        
        // Make dialog always on top and steal focus
        warningDialog.setAlwaysOnTop(true);
        
        // Create dramatic blinking red border effect
        Timer blinkTimer = new Timer(300, null);
        final boolean[] isRed = {true};
        blinkTimer.addActionListener(_ -> {
            if (isRed[0]) {
                messagePanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.RED, 8),
                    BorderFactory.createEmptyBorder(30, 30, 30, 30)
                ));
            } else {
                messagePanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(255, 50, 50), 8),
                    BorderFactory.createEmptyBorder(30, 30, 30, 30)
                ));
            }
            isRed[0] = !isRed[0];
        });
        blinkTimer.start();
        
        // Handle OK button and auto-dismiss
        Runnable dismissAction = () -> {
            blinkTimer.stop();
            warningDialog.dispose();
            roundCount++;
            
            // Status message
            statusLabel.setText("Round " + roundCount + " - " + cheatingPlayerName + " was caught cheating!");
            
            // Check if game should end
            if (roundCount >= totalRounds || player1Score > totalRounds/2 || player2Score > totalRounds/2) {
                Timer endTimer = new Timer(1500, _ -> showFinalScore());
                endTimer.setRepeats(false);
                endTimer.start();
            } else {
                // Continue to next round after a brief pause
                Timer nextRoundTimer = new Timer(2000, _ -> {
                    statusLabel.setText("Waiting for players to make their move...");
                    requestFocusInWindow();
                });
                nextRoundTimer.setRepeats(false);
                nextRoundTimer.start();
            }
        };
        
        okButton.addActionListener(_ -> dismissAction.run());
        
        // Auto-dismiss after 5 seconds if user doesn't click OK
        Timer dismissTimer = new Timer(5000, _ -> dismissAction.run());
        dismissTimer.setRepeats(false);
        dismissTimer.start();
        
        // Play warning sound effect
        playSound("warning.wav");
        
        // Show the dialog
        warningDialog.setVisible(true);
    }

    private JPanel createGamePanel() {
        gamePanel = new JPanel(new BorderLayout());
        gamePanel.setOpaque(false);

        // Main center panel with proper constraints for better centering
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);

        // Title panel for player names with improved spacing
        JPanel titlePanel = new JPanel(new GridLayout(1, 2, 120, 0)); // Increased gap for better spacing
        titlePanel.setOpaque(false);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(0, 50, 15, 50)); // Increased side padding

        userTitle = new JLabel(isPvCMode ? "Player" : player1Name, SwingConstants.CENTER);
        userTitle.setFont(new Font("Arial", Font.BOLD, 28));
        userTitle.setForeground(new Color(255, 105, 180)); // Hot pink
        userTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        userTitle.setOpaque(false);

        computerTitle = new JLabel(isPvCMode ? "Computer" : player2Name, SwingConstants.CENTER);
        computerTitle.setFont(new Font("Arial", Font.BOLD, 28));
        computerTitle.setForeground(new Color(30, 144, 255)); // Dodger blue
        computerTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        computerTitle.setOpaque(false);

        titlePanel.add(userTitle);
        titlePanel.add(computerTitle);

        // Enhanced player choice boxes with VS label in the center
        JPanel gameDisplayPanel = new JPanel(new GridBagLayout());
        gameDisplayPanel.setOpaque(false);
        gameDisplayPanel.setBorder(BorderFactory.createEmptyBorder(20, 60, 40, 60)); // Added top spacing and side padding
        
        GridBagConstraints boxConstraints = new GridBagConstraints();
        boxConstraints.insets = new Insets(0, 20, 0, 20); // Spacing around elements
        boxConstraints.anchor = GridBagConstraints.CENTER;
        boxConstraints.weighty = 1.0;

        // Enhanced VS label with better styling - positioned in center
        JLabel vsLabel = new JLabel("VS", SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Add glow effect
                for (int i = 0; i < 5; i++) {
                                       g2d.setColor(new Color(255, 215, 0, 30 - i * 5)); // Gold glow
                    g2d.setFont(getFont());
                    FontMetrics fm = g2d.getFontMetrics();
                    int x = (getWidth() - fm.stringWidth(getText())) / 2;
                    int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                    g2d.drawString(getText(), x + i, y + i);
                }
                
                super.paintComponent(g);
            }
        };
        vsLabel.setFont(new Font("Arial", Font.BOLD, 42)); // Larger font for center positioning
        vsLabel.setForeground(ThemeManager.DARK_BORDER_GOLD); // Premium gold color
        vsLabel.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        vsLabel.setOpaque(false);

        
        // Create player choice boxes
        JPanel[] boxes = new JPanel[2];
        JLabel[] labels = new JLabel[2];
        for (int i = 0; i < 2; i++) {
            JPanel box = new JPanel(new BorderLayout());
            box.setOpaque(false);
            box.setPreferredSize(new Dimension(200, 240)); // Larger boxes
            box.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15)); // Padding

            JLabel imgLabel = new JLabel();
            imgLabel.setHorizontalAlignment(SwingConstants.CENTER);
            imgLabel.setVerticalAlignment(SwingConstants.CENTER);
            imgLabel.setPreferredSize(new Dimension(120, 120)); // Larger image area
            
            box.add(imgLabel, BorderLayout.CENTER);
            boxes[i] = box;
            labels[i] = imgLabel;
        }
        userChoiceLabel = labels[0];
        computerChoiceLabel = labels[1];

        // Position elements in the game display panel
        // Left player box
        boxConstraints.gridx = 0;
        boxConstraints.gridy = 0;
        gameDisplayPanel.add(boxes[0], boxConstraints);
        
        // VS label in center
        boxConstraints.gridx = 1;
        boxConstraints.gridy = 0;
        gameDisplayPanel.add(vsLabel, boxConstraints);
        
        // Right player box  
        boxConstraints.gridx = 2;
        boxConstraints.gridy = 0;
        gameDisplayPanel.add(boxes[1], boxConstraints);

        // Create main content panel with proper alignment
        JPanel verticalPanel = new JPanel();
        verticalPanel.setLayout(new BoxLayout(verticalPanel, BoxLayout.Y_AXIS));
        verticalPanel.setOpaque(false);
        verticalPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Ensure all components are centered
        titlePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        gameDisplayPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        verticalPanel.add(titlePanel);
        verticalPanel.add(gameDisplayPanel);

        // Use GridBagConstraints for perfect centering
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE; // Don't expand, just center
        
        centerPanel.add(verticalPanel, gbc);
        gamePanel.add(centerPanel, BorderLayout.CENTER);
        return gamePanel;
    }

    private void updateEmojis(String playerChoice, String computerChoice) {
        if (userChoiceLabel != null && computerChoiceLabel != null) {
            SwingUtilities.invokeLater(() -> {
                // Load and set images for player and computer choices
                setImageToLabel(userChoiceLabel, getEmoji(playerChoice));
                setImageToLabel(computerChoiceLabel, getEmoji(computerChoice));

                // Add premium border effects with enhanced colors
                userChoiceLabel.setBorder(BorderFactory.createLineBorder(ThemeManager.DARK_ACCENT_SECONDARY, 5)); // Rose gold
                computerChoiceLabel.setBorder(BorderFactory.createLineBorder(ThemeManager.DARK_BORDER_PREMIUM, 5)); // Electric blue
            });
        }
    }

    private void setImageToLabel(JLabel label, String imagePath) {
        if (imagePath != null) {
            ImageIcon icon = loadImageIcon(imagePath);
            if (icon != null) {
                Image scaledImage = icon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
                label.setIcon(new ImageIcon(scaledImage));
                label.setText(null); // Remove any text
                label.setBorder(null); // Remove any border
            } else {
                // If image can't be loaded, display text instead
                label.setIcon(null);
                String displayText = imagePath.substring(imagePath.lastIndexOf("/") + 1);
                label.setText(displayText);
                label.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
            }
        } else {
            label.setIcon(null);
            label.setText("");
            label.setBorder(null); // Remove any border
        }
    }

    private String getEmoji(String choice) {
        return switch (choice) {
            case "Rock" -> "image/rock.jpg";
            case "Paper" -> "image/paper.jpg";
            case "Scissors" -> "image/scissors.jpg";
            default -> null; // No image for default
        };
    }

    private void initializeClock() {
        clockLabel = new JLabel();
        clockLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        clockLabel.setForeground(isDarkMode ? 
            ThemeManager.DARK_ACCENT_PRIMARY : ThemeManager.LIGHT_ACCENT_PRIMARY);
        clockLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        clockTimer = new Timer(1000, _ -> {
            if (clockLabel != null) {
                String currentTime = java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("hh:mm:ss a"));
                clockLabel.setText(currentTime);
            }
        });
        clockTimer.start();

        // Add the clock label to the existing bottom panel structure
        // Since bottomPanel uses GridLayout(2,1), we need to change its layout to accommodate the clock
        bottomPanel.setLayout(new BorderLayout());
        
        // Create a panel for the existing content
        JPanel existingContent = new JPanel(new GridLayout(2, 1, 0, 10));
        existingContent.setOpaque(false);
        
        // Move existing components to the new panel
        Component[] components = bottomPanel.getComponents();
        for (Component comp : components) {
            bottomPanel.remove(comp);
            existingContent.add(comp);
        }
        
        // Add the existing content to center and clock to south
        bottomPanel.add(existingContent, BorderLayout.CENTER);
        bottomPanel.add(clockLabel, BorderLayout.SOUTH);
    }

    private void showGameHistory() {
        // Get history from GameHistoryManager
        List<GameHistoryManager.GameRecord> history = GameHistoryManager.getInstance().getGameHistory();
        
        // Create a gradient panel for a more modern look
        JPanel historyPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                int w = getWidth();
                int h = getHeight();
                
                // Create a soft gradient background
                GradientPaint gp = new GradientPaint(
                    0, 0, ThemeManager.DARK_TOP_GRADIENT_START,
                    0, h, ThemeManager.DARK_BOTTOM_GRADIENT_END
                );
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);
                g2d.dispose();
            }
        };
        historyPanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        
        // Create header panel with icon and title
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        
        // Title with icon
        JLabel titleLabel = new JLabel("Game History", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(ThemeManager.DARK_TEXT_PRIMARY);
        
        // Add glow effect to title
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        headerPanel.add(titleLabel, BorderLayout.NORTH);
        
        // Add file location in a stylized info panel
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        infoPanel.setOpaque(false);
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, ThemeManager.DARK_BORDER_GOLD),
            BorderFactory.createEmptyBorder(0, 0, 15, 0)
        ));
        
        // File location with icon
        JLabel locationIcon = new JLabel("📁");
        locationIcon.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        locationIcon.setForeground(ThemeManager.DARK_ACCENT_PRIMARY);
        
        JLabel locationLabel = new JLabel("History file: " + GameHistoryManager.getInstance().getHistoryFilePath());
        locationLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        locationLabel.setForeground(ThemeManager.DARK_TEXT_SECONDARY);
        
        infoPanel.add(locationIcon);
        infoPanel.add(locationLabel);
        headerPanel.add(infoPanel, BorderLayout.CENTER);
        
        // Mode filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        filterPanel.setOpaque(false);
        filterPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 10, 0));
        
        JLabel filterLabel = new JLabel("Filter by mode: ");
        filterLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        filterLabel.setForeground(ThemeManager.DARK_TEXT_SECONDARY);
        
        String[] modes = {"All Modes", "PvC", "PvP"};
        JComboBox<String> modeFilter = new JComboBox<>(modes);
        modeFilter.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        modeFilter.setBackground(new Color(40, 50, 70));
        modeFilter.setForeground(ThemeManager.DARK_TEXT_PRIMARY);
        modeFilter.setPreferredSize(new Dimension(120, 30));
        
        filterPanel.add(filterLabel);
        filterPanel.add(modeFilter);
        headerPanel.add(filterPanel, BorderLayout.SOUTH);
        
        historyPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Content panel for table or no history message
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);
        
        if (history.isEmpty()) {
            // Create a stylized no history panel
            JPanel noHistoryPanel = new JPanel(new BorderLayout());
            noHistoryPanel.setOpaque(false);
            noHistoryPanel.setBorder(BorderFactory.createEmptyBorder(50, 20, 50, 20));
            
            JLabel noHistoryIcon = new JLabel("📜", SwingConstants.CENTER);
            noHistoryIcon.setFont(new Font("Segoe UI", Font.PLAIN, 72));
            noHistoryIcon.setForeground(new Color(100, 100, 120, 150));
            
            JLabel noHistoryLabel = new JLabel("No games have been played yet", SwingConstants.CENTER);
            noHistoryLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
            noHistoryLabel.setForeground(ThemeManager.DARK_TEXT_SECONDARY);
            
            JLabel startPlayingLabel = new JLabel("Play a game to start building your history!", SwingConstants.CENTER);
            startPlayingLabel.setFont(new Font("Segoe UI", Font.ITALIC, 16));
            startPlayingLabel.setForeground(ThemeManager.DARK_TEXT_SECONDARY.brighter());
            
            JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 10));
            textPanel.setOpaque(false);
            textPanel.add(noHistoryLabel);
            textPanel.add(startPlayingLabel);
            
            noHistoryPanel.add(noHistoryIcon, BorderLayout.CENTER);
            noHistoryPanel.add(textPanel, BorderLayout.SOUTH);
            
            contentPanel.add(noHistoryPanel, BorderLayout.CENTER);
        } else {
            // Create table with stylized renderer for colorful rows and icons
            String[] columnNames = {"Date/Time", "Mode", "Player 1", "Player 2", "Score", "Result", "Details"};
            Object[][] data = new Object[history.size()][7];
            
            for (int i = 0; i < history.size(); i++) {
                GameHistoryManager.GameRecord record = history.get(i);
                data[i][0] = record.date;
                data[i][1] = record.mode;
                data[i][2] = record.player1;
                data[i][3] = record.player2;
                data[i][4] = record.player1Score + " - " + record.player2Score;
                data[i][5] = record.result;
                data[i][6] = String.format("%s vs %s", record.player1, record.player2);
            }
            
            // Custom table with alternating row colors and cell styling
            JTable historyTable = new JTable(data, columnNames) {
                @Override
                public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                    Component comp = super.prepareRenderer(renderer, row, column);
                    
                    // Add alternating row colors
                    if (!isRowSelected(row)) {
                        Color bgColor;
                        if (row % 2 == 0) {
                            // Even rows - darker
                            bgColor = new Color(20, 30, 50);
                        } else {
                            // Odd rows - lighter
                            bgColor = new Color(30, 40, 60);
                        }
                        comp.setBackground(bgColor);
                        comp.setForeground(ThemeManager.DARK_TEXT_PRIMARY);
                    }
                    
                    // Highlight the mode column with colors
                    if (column == 1) { // Mode column
                        String mode = (String)getValueAt(row, column);
                        if ("PvC".equals(mode)) {
                            comp.setForeground(new Color(100, 210, 255)); // AI color
                        } else if ("PvP".equals(mode)) {
                            comp.setForeground(new Color(255, 120, 140)); // Player color
                        }
                    }
                    
                    // Highlight the result column
                    if (column == 5) { // Result column
                        String result = (String)getValueAt(row, column);
                        if (result.contains("won")) {
                            if (result.contains("Computer")) {
                                comp.setForeground(new Color(255, 100, 100)); // Red for computer win
                            } else {
                                comp.setForeground(new Color(100, 255, 100)); // Green for player win
                            }
                        } else if (result.contains("Draw")) {
                            comp.setForeground(new Color(255, 255, 100)); // Yellow for draw
                        }
                    }
                    
                    // Style the score column
                    if (column == 4) { // Score column
                        comp.setFont(new Font("Segoe UI", Font.BOLD, 14));
                    }
                    
                    return comp;
                }
            };
            
            // Set up table appearance
            historyTable.setRowHeight(35);
            historyTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            historyTable.setGridColor(new Color(60, 70, 90));
            historyTable.setShowGrid(true);
            historyTable.setIntercellSpacing(new Dimension(5, 5));
            historyTable.setDefaultEditor(Object.class, null); // Read-only
            
            // Style the header
            JTableHeader header = historyTable.getTableHeader();
            header.setFont(new Font("Segoe UI", Font.BOLD, 14));
            header.setBackground(new Color(40, 50, 70));
            header.setForeground(ThemeManager.DARK_TEXT_PRIMARY);
            header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, ThemeManager.DARK_BORDER_GOLD));
            
            // Set column widths
            historyTable.getColumnModel().getColumn(0).setPreferredWidth(150); // Date
            historyTable.getColumnModel().getColumn(1).setPreferredWidth(80);  // Mode
            historyTable.getColumnModel().getColumn(2).setPreferredWidth(120); // Player 1
            historyTable.getColumnModel().getColumn(3).setPreferredWidth(120); // Player 2
            historyTable.getColumnModel().getColumn(4).setPreferredWidth(100); // Score
            historyTable.getColumnModel().getColumn(5).setPreferredWidth(120); // Result
            historyTable.getColumnModel().getColumn(6).setPreferredWidth(200); // Details
            
            // Create a custom scroll pane with styled scrollbars
            JScrollPane scrollPane = new JScrollPane(historyTable);
            scrollPane.setPreferredSize(new Dimension(900, 400));
            scrollPane.setBorder(BorderFactory.createLineBorder(ThemeManager.DARK_BORDER_PREMIUM, 1));
            scrollPane.getViewport().setBackground(new Color(15, 20, 30));
            
            // Configure mode filter to filter the table
            modeFilter.addActionListener(_ -> {
                String selectedMode = (String)modeFilter.getSelectedItem();
                filterHistoryTable(historyTable, selectedMode);
            });
            
            contentPanel.add(scrollPane, BorderLayout.CENTER);
            
            // Add statistics summary panel
            JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
            statsPanel.setOpaque(false);
            statsPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
            
            // Calculate simple stats
            int totalGames = history.size();
            long pvcGames = history.stream().filter(r -> "PvC".equals(r.mode)).count();
            long pvpGames = history.stream().filter(r -> "PvP".equals(r.mode)).count();
            
            JLabel totalLabel = new JLabel(String.format("Total Games: %d", totalGames));
            totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
            totalLabel.setForeground(ThemeManager.DARK_TEXT_PRIMARY);
            
            JLabel pvcLabel = new JLabel(String.format("PvC Games: %d", pvcGames));
            pvcLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            pvcLabel.setForeground(new Color(100, 210, 255));
            
            JLabel pvpLabel = new JLabel(String.format("PvP Games: %d", pvpGames));
            pvpLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            pvpLabel.setForeground(new Color(255, 120, 140));
            
            statsPanel.add(totalLabel);
            statsPanel.add(pvcLabel);
            statsPanel.add(pvpLabel);
            
            contentPanel.add(statsPanel, BorderLayout.SOUTH);
        }
        
        historyPanel.add(contentPanel, BorderLayout.CENTER);
        
        // Create footer with buttons
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setOpaque(false);
        footerPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        
        // Timestamp
        JLabel timestampLabel = new JLabel("Last updated: " + 
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
            SwingConstants.LEFT);
        timestampLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        timestampLabel.setForeground(ThemeManager.DARK_TEXT_SECONDARY);
        
        footerPanel.add(timestampLabel, BorderLayout.WEST);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        buttonPanel.setOpaque(false);
        
        // Enhanced styled buttons with icons
        JButton clearButton = createStyledButton("🗑️ Clear History", 
            new Color(231, 76, 60), Color.WHITE);
        clearButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        clearButton.setPreferredSize(new Dimension(150, 40));
        clearButton.addActionListener(_ -> {
            int result = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to clear all game history?\nThis action cannot be undone.",
                "Confirm Clear History",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
            
            if (result == JOptionPane.YES_OPTION) {
                GameHistoryManager.getInstance().clearHistory();
                JOptionPane.showMessageDialog(this, 
                    "Game history cleared successfully.",
                    "History Cleared",
                    JOptionPane.INFORMATION_MESSAGE);
                showGameHistory(); // Refresh the view
            }
        });
        
        JButton exportButton = createStyledButton("📊 Export CSV", 
            new Color(52, 152, 219), Color.WHITE);
        exportButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        exportButton.setPreferredSize(new Dimension(150, 40));
        exportButton.addActionListener(_ -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Export Game History");
            fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "CSV Files", "csv"));
            
            if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                try {
                    String targetPath = fileChooser.getSelectedFile().getPath();
                    if (!targetPath.toLowerCase().endsWith(".csv")) {
                        targetPath += ".csv";
                    }
                    GameHistoryManager.getInstance().exportHistory(targetPath);
                    JOptionPane.showMessageDialog(this,
                        "Game history exported successfully to:\n" + targetPath,
                        "Export Successful",
                        JOptionPane.INFORMATION_MESSAGE);
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(this,
                        "Error exporting history: " + e.getMessage(),
                        "Export Failed",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        buttonPanel.add(exportButton);
        buttonPanel.add(clearButton);
        footerPanel.add(buttonPanel, BorderLayout.EAST);
        
        historyPanel.add(footerPanel, BorderLayout.SOUTH);
        
        // Create and show dialog
        JDialog historyDialog = new JDialog(this, "Game History", true);
        historyDialog.setContentPane(historyPanel);
        historyDialog.setPreferredSize(new Dimension(950, 650));
        historyDialog.pack();
        historyDialog.setLocationRelativeTo(this);
        historyDialog.setVisible(true);
    }
    
    /**
     * Filter the history table to show only records of the specified mode
     */
    private void filterHistoryTable(JTable table, String mode) {
        TableRowSorter<TableModel> sorter = new TableRowSorter<>(table.getModel());
        table.setRowSorter(sorter);
        
        if ("All Modes".equals(mode)) {
            sorter.setRowFilter(null); // Show all rows
        } else {
            // Filter by mode column (index 1)
            sorter.setRowFilter(RowFilter.regexFilter("^" + mode + "$", 1));
        }
    }

    private void showLeaderboard() {
        // Get stats separated by game mode
        Map<String, Map<String, GameHistoryManager.PlayerStats>> leaderboardByMode = 
            GameHistoryManager.getInstance().getLeaderboardByMode();
        
        boolean hasPvCStats = !leaderboardByMode.get("PvC").isEmpty();
        boolean hasPvPStats = !leaderboardByMode.get("PvP").isEmpty();
        
        if (!hasPvCStats && !hasPvPStats) {
            JOptionPane.showMessageDialog(this,
                "No games have been played yet.",
                "Leaderboard",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Create panel with tabbed layout for PvC and PvP modes
        JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Create a gradient background
                GradientPaint gradient = new GradientPaint(
                    0, 0, ThemeManager.DARK_TOP_GRADIENT_START,
                    0, getHeight(), ThemeManager.DARK_BOTTOM_GRADIENT_END
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.dispose();
            }
        };
        mainPanel.setOpaque(false);
        mainPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeManager.DARK_BORDER_PREMIUM, 2),
            BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));
        
        // Create stylish header with game logo/title
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel("LEADERBOARD", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Verdana", Font.BOLD, 32));
        titleLabel.setForeground(ThemeManager.DARK_BORDER_GOLD);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        
        // Add subtle glow effect to title
        titleLabel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(0, 0, 15, 0),
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 215, 0, 100), 1),
                BorderFactory.createEmptyBorder(5, 15, 5, 15)
            )
        ));
        
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Create custom styled tabbed pane
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setOpaque(false);
        tabbedPane.setForeground(ThemeManager.DARK_TEXT_PRIMARY);
        tabbedPane.setBackground(new Color(30, 38, 55));
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        // Use custom icons for tabs
        ImageIcon pvcIcon = null;
        ImageIcon pvpIcon = null;
        try {
            // Try to load icons from resources
            pvcIcon = new ImageIcon(getClass().getResource("/image/computer_icon.png"));
            pvpIcon = new ImageIcon(getClass().getResource("/image/players_icon.png"));
            
            // Scale icons if needed
            if (pvcIcon != null && pvcIcon.getIconWidth() > 20) {
                pvcIcon = new ImageIcon(pvcIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH));
            }
            if (pvpIcon != null && pvpIcon.getIconWidth() > 20) {
                pvpIcon = new ImageIcon(pvpIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH));
            }
        } catch (Exception e) {
            // Icon loading failed, continue without icons
            log("Failed to load tab icons: " + e.getMessage());
        }
        
        // Add PvC tab if we have data
        if (hasPvCStats) {
            JPanel pvcPanel = createLeaderboardPanel(leaderboardByMode.get("PvC"), "PvC");
            tabbedPane.addTab("Player vs Computer", pvcIcon, pvcPanel, "PvC Leaderboard");
        }
        
        // Add PvP tab if we have data
        if (hasPvPStats) {
            JPanel pvpPanel = createLeaderboardPanel(leaderboardByMode.get("PvP"), "PvP");
            tabbedPane.addTab("Player vs Player", pvpIcon, pvpPanel, "PvP Leaderboard");
        }
        
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        
        // Add stylish footer with game info
        JLabel footerLabel = new JLabel("Game statistics updated: " + 
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), 
            SwingConstants.RIGHT);
        footerLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        footerLabel.setForeground(ThemeManager.DARK_TEXT_SECONDARY);
        footerLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        mainPanel.add(footerLabel, BorderLayout.SOUTH);

        JDialog dialog = new JDialog(this, "Game Leaderboard", true);
        dialog.setContentPane(mainPanel);
        dialog.setPreferredSize(new Dimension(800, 600));
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    private JPanel createLeaderboardPanel(Map<String, GameHistoryManager.PlayerStats> leaderboard, String mode) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        
        // Add a summary panel at the top with total games stats
        JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        summaryPanel.setOpaque(false);
        summaryPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, ThemeManager.DARK_BORDER_GOLD),
            BorderFactory.createEmptyBorder(0, 0, 10, 0)
        ));
        
        // Calculate total games
        int totalGames = 0;
        for (GameHistoryManager.PlayerStats stats : leaderboard.values()) {
            totalGames += stats.getGamesPlayed();
        }
        
        // Create summary label
        JPanel summaryLabelPanel = new JPanel(new BorderLayout());
        summaryLabelPanel.setOpaque(false);
        
        JLabel modeLabel = new JLabel(mode + " MODE", SwingConstants.CENTER);
        modeLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        modeLabel.setForeground(ThemeManager.DARK_TEXT_PRIMARY);
        
        JLabel totalGamesLabel = new JLabel("Total Games: " + totalGames/2, SwingConstants.CENTER);
        totalGamesLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        totalGamesLabel.setForeground(ThemeManager.DARK_TEXT_PRIMARY);
        
        summaryLabelPanel.add(modeLabel, BorderLayout.NORTH);
        summaryLabelPanel.add(totalGamesLabel, BorderLayout.CENTER);
        
        summaryPanel.add(summaryLabelPanel);
        
        panel.add(summaryPanel, BorderLayout.NORTH);
        
        // Convert to list and sort by win rate
        List<Map.Entry<String, GameHistoryManager.PlayerStats>> sortedPlayers = 
            new ArrayList<>(leaderboard.entrySet());
        sortedPlayers.sort((a, b) -> Double.compare(b.getValue().getWinRate(), a.getValue().getWinRate()));

        // Create table model
        String[] columnNames = {"Rank", "Player", "Games", "Wins", "Losses", "Draws", "Win Rate", "Avg Score"};
        Object[][] data = new Object[sortedPlayers.size()][8];
        
        for (int i = 0; i < sortedPlayers.size(); i++) {
            Map.Entry<String, GameHistoryManager.PlayerStats> entry = sortedPlayers.get(i);
            GameHistoryManager.PlayerStats stats = entry.getValue();
            data[i][0] = i + 1; // Rank
            data[i][1] = entry.getKey();
            data[i][2] = stats.getGamesPlayed();
            data[i][3] = stats.getWins();
            data[i][4] = stats.getLosses();
            data[i][5] = stats.getDraws();
            data[i][6] = String.format("%.1f%%", stats.getWinRate());
            data[i][7] = String.format("%.1f", stats.getAverageScore());
        }

        // Create a custom table with alternating row colors and better formatting
        JTable leaderboardTable = new JTable(data, columnNames) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component comp = super.prepareRenderer(renderer, row, column);
                
                // Add alternating row colors
                if (!isRowSelected(row)) {
                    Color bgColor;
                    if (row % 2 == 0) {
                        // Even rows - darker
                        bgColor = new Color(20, 30, 50);
                    } else {
                        // Odd rows - lighter
                        bgColor = new Color(30, 40, 60);
                    }
                    comp.setBackground(bgColor);
                    comp.setForeground(ThemeManager.DARK_TEXT_PRIMARY);
                }
                
                // Highlight rank columns with colors
                if (column == 0) { // Rank column
                    if (row == 0) {
                        // Gold for first place
                        comp.setBackground(new Color(255, 215, 0, 80));
                        comp.setForeground(Color.WHITE);
                    } else if (row == 1) {
                        // Silver for second place
                        comp.setBackground(new Color(192, 192, 192, 80));
                        comp.setForeground(Color.WHITE);
                    } else if (row == 2 && row < getRowCount()) {
                        // Bronze for third place
                        comp.setBackground(new Color(205, 127, 50, 80));
                        comp.setForeground(Color.WHITE);
                    }
                }
                
                // Highlight win rate column
                if (column == 6) { // Win Rate column
                    String winRateStr = (String)getValueAt(row, column);
                    try {
                        double winRate = Double.parseDouble(winRateStr.replace("%", ""));
                        if (winRate >= 70.0) {
                            comp.setForeground(new Color(50, 205, 50)); // Green for high win rate
                        } else if (winRate <= 30.0) {
                            comp.setForeground(new Color(255, 69, 0));  // Red for low win rate
                        }
                    } catch (NumberFormatException ignored) {}
                }
                
                return comp;
            }
        };
        
        // Style the table
        leaderboardTable.setRowHeight(30);
        leaderboardTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        leaderboardTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        leaderboardTable.getTableHeader().setBackground(new Color(40, 50, 70));
        leaderboardTable.getTableHeader().setForeground(ThemeManager.DARK_TEXT_PRIMARY);
        leaderboardTable.setGridColor(new Color(60, 70, 90));
        leaderboardTable.setDefaultEditor(Object.class, null); // Make table read-only
        leaderboardTable.setFillsViewportHeight(true);
        leaderboardTable.setShowGrid(true);
        
        // Set column widths
        leaderboardTable.getColumnModel().getColumn(0).setPreferredWidth(50);  // Rank
        leaderboardTable.getColumnModel().getColumn(1).setPreferredWidth(150); // Player
        leaderboardTable.getColumnModel().getColumn(2).setPreferredWidth(70);  // Games
        leaderboardTable.getColumnModel().getColumn(3).setPreferredWidth(70);  // Wins
        leaderboardTable.getColumnModel().getColumn(4).setPreferredWidth(70);  // Losses
        leaderboardTable.getColumnModel().getColumn(5).setPreferredWidth(70);  // Draws
        leaderboardTable.getColumnModel().getColumn(6).setPreferredWidth(80);  // Win Rate
        leaderboardTable.getColumnModel().getColumn(7).setPreferredWidth(80);  // Avg Score
        
        // Center-align all columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < leaderboardTable.getColumnCount(); i++) {
            leaderboardTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Create a custom scroll pane with styled scrollbars
        JScrollPane scrollPane = new JScrollPane(leaderboardTable);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createLineBorder(ThemeManager.DARK_BORDER_PREMIUM, 1));
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Add a legend/footer panel
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footerPanel.setOpaque(false);
        footerPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        // Add sorting info
        JLabel sortInfoLabel = new JLabel("Sorted by win rate");
        sortInfoLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        sortInfoLabel.setForeground(ThemeManager.DARK_TEXT_SECONDARY);
        footerPanel.add(sortInfoLabel);
        
        panel.add(footerPanel, BorderLayout.SOUTH);
        
        return panel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new RockPaperScissorsGame().setVisible(true));
    }

    private static class GameButton extends JButton {  // Extract button creation to static inner class
        private final String emoji;
        private final Color color;

        public GameButton(String emoji, Color color, String name, ActionListener listener) {
            this.emoji = emoji;
            this.color = color;
            setPreferredSize(new Dimension(90, 90)); // Slightly larger for premium feel
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setToolTipText(name);
            addActionListener(listener);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Create premium gradient background with depth
            Color lighterColor = new Color(
                Math.min(255, color.getRed() + 30),
                Math.min(255, color.getGreen() + 30),
                Math.min(255, color.getBlue() + 30)
            );
            
            GradientPaint gp = new GradientPaint(0, 0, lighterColor,
                    0, getHeight(), color.darker());
            g2d.setPaint(gp);
            g2d.fillRoundRect(5, 5, getWidth()-10, getHeight()-10, 25, 25);

            // Add premium border with electric blue glow
            g2d.setStroke(new BasicStroke(2.5f));
            g2d.setColor(ThemeManager.DARK_BORDER_PREMIUM); // Electric blue
            g2d.drawRoundRect(5, 5, getWidth()-10, getHeight()-10, 25, 25);
            
            // Add inner glow effect
            g2d.setStroke(new BasicStroke(1.5f));
            g2d.setColor(ThemeManager.DARK_INNER_GLOW); // Soft blue glow with transparency
            g2d.drawRoundRect(7, 7, getWidth()-14, getHeight()-14, 20, 20);

            // Draw subtle outer glow on hover
            if (getModel().isRollover()) {
                g2d.setStroke(new BasicStroke(3f));
                g2d.setColor(new Color(255, 255, 255, 80));
                g2d.drawRoundRect(3, 3, getWidth()-6, getHeight()-6, 28, 28);
            }

            // Draw emoji with enhanced styling
            g2d.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40)); // Larger emoji
            FontMetrics fm = g2d.getFontMetrics();
            int x = (getWidth() - fm.stringWidth(emoji)) / 2;
            int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();

            // Add subtle text shadow for depth
            g2d.setColor(new Color(0, 0, 0, 100));
            g2d.drawString(emoji, x+1, y+1);
            
            // Draw main emoji
            g2d.setColor(Color.WHITE);
            g2d.drawString(emoji, x, y);
        }

        @Override
        protected void paintBorder(Graphics g) {
            // Custom premium hover effect
            if (getModel().isRollover()) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Animated glow effect
                g2d.setColor(ThemeManager.DARK_BORDER_GOLD); // Premium gold
                g2d.setStroke(new BasicStroke(2.5f));
                g2d.drawRoundRect(2, 2, getWidth()-4, getHeight()-4, 30, 30);
            }
        }
    }

    private JButton createGameButton(String emoji, Color color, String name) {
        return new GameButton(emoji, color, name, this);
    }

    // Helper method to create styled buttons with hover effects
    private JButton createStyledButton(String text, Color bgColor, Color fgColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Create gradient background
                GradientPaint gradient;
                if (getModel().isPressed()) {
                    gradient = new GradientPaint(0, 0, bgColor.darker(), 0, getHeight(), bgColor);
                } else if (getModel().isRollover()) {
                    gradient = new GradientPaint(0, 0, bgColor.brighter(), 0, getHeight(), bgColor);
                } else {
                    gradient = new GradientPaint(0, 0, bgColor, 0, getHeight(), bgColor.darker());
                }
                
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                
                // Draw slight shadow/highlight based on pressed state
                if (getModel().isPressed()) {
                    g2d.setColor(new Color(0, 0, 0, 50));
                    g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                } else {
                    g2d.setColor(new Color(255, 255, 255, 50));
                    g2d.drawRoundRect(2, 2, getWidth()-4, getHeight()-4, 15, 15);
                }
                
                // Draw text with shadow
                FontMetrics fm = g2d.getFontMetrics();
                Rectangle textRect = fm.getStringBounds(text, g2d).getBounds();
                
                int x = (getWidth() - textRect.width) / 2;
                int y = (getHeight() - textRect.height) / 2 + fm.getAscent();
                
                // Draw subtle text shadow
                g2d.setColor(new Color(0, 0, 0, 50));
                g2d.drawString(text, x+1, y+1);
                
                // Draw text
                g2d.setColor(fgColor);
                g2d.drawString(text, x, y);
            }
        };
        
        button.setFont(new Font("Segoe UI", Font.BOLD, 20));
        button.setForeground(fgColor);
        button.setBackground(bgColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(300, 45));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        return button;
    }



    /**
     * Helper method to get image from resources
     * @param imagePath Path to image relative to resources
     * @return ImageIcon or null if not found
     */
    private ImageIcon loadImageIcon(String imagePath) {
        try {
            // Remove "resource/" prefix if present
            if (imagePath.startsWith("resource/")) {
                imagePath = imagePath.substring("resource/".length());
            }
            
            // Try to load from classpath
            URL imageUrl = getClass().getResource("/" + imagePath);
            if (imageUrl != null) {
                return new ImageIcon(imageUrl);
            }
            
            // Try to load from src/main/resources
            File file = new File("src/main/resources/" + imagePath);
            if (file.exists()) {
                return new ImageIcon(file.getAbsolutePath());
            }
            
            log("Image not found: " + imagePath);
            return null;
        } catch (Exception e) {
            log("Error loading image: " + imagePath + " - " + e.getMessage());
            return null;
        }
    }

    // New method to start the entire PvP match
    private void startPvPMatch() {
        statusLabel.setText("PvP Match Started - Make your moves!");
        
        // Ensure player names are not empty
        String p1Name = player1Name.trim().isEmpty() ? "Player 1" : player1Name;
        String p2Name = player2Name.trim().isEmpty() ? "Player 2" : player2Name;
        
        scoreLabel.setText(p1Name + ": 0 | " + p2Name + ": 0");
        
        // Make sure names are displayed correctly
        if (userTitle != null && computerTitle != null) {
            userTitle.setText(p1Name);
            computerTitle.setText(p2Name);
        }
        
        // Start the first round immediately
        statusLabel.setText("Waiting for players to make their move...");
        p1Move = null;
        p2Move = null;
        p1KeyPresses = 0;
        p2KeyPresses = 0;
        
        JOptionPane.showMessageDialog(this, 
            "Match started!\n\n" +
            p1Name + " controls: A (Rock), S (Paper), D (Scissors)\n" +
            p2Name + " controls: J (Rock), K (Paper), L (Scissors)\n\n" +
            "Best of " + totalRounds + " rounds. Good luck!",
            "Match Started",
            JOptionPane.INFORMATION_MESSAGE);
            
        requestFocusInWindow();
    }

    /**
     * Creates an animated "Rock Paper Scissors" title with colorful design and visual effects
     */
    private void createAnimatedGameTitle() {
        // Create a custom panel for the animated title with enhanced effects
        JPanel titlePanel = new JPanel() {
            private Timer animationTimer;
            private double animationTime = 0.0;
            private final String[] words = {"Rock", "Paper", "Scissors"};
            private final String[] emojis = {"👊", "✋", "✌️"};

            {
                setOpaque(false);
                setPreferredSize(new Dimension(900, 100));
                setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
                setAlignmentX(Component.CENTER_ALIGNMENT);
                
                // Start animation timer with faster updates for smoother effects
                animationTimer = new Timer(30, _ -> {
                    animationTime += 0.08;
                    repaint();
                });
                animationTimer.start();
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                int width = getWidth();
                int height = getHeight();
                int wordWidth = width / 3;
                
                // Draw background rainbow glow effect
                drawRainbowBackground(g2d, width, height);
                
                // Draw each word with enhanced theme-based colors and animations
                for (int i = 0; i < words.length; i++) {
                    String word = words[i];
                    String emoji = emojis[i];
                    int x = i * wordWidth + wordWidth / 2;
                    int y = height / 2;
                    
                    // Calculate multiple animation effects
                    double floatOffset = Math.sin(animationTime + i * Math.PI / 2) * 15;
                    double scaleEffect = 1.0 + Math.sin(animationTime * 1.5 + i * Math.PI / 3) * 0.15;
                    
                    // Get enhanced theme-based colors
                    Color[] colors = getEnhancedThemeColorsForWord(i);
                    Color primaryColor = colors[0];
                    Color glowColor = colors[1];
                    Color shadowColor = colors[2];
                    
                    // Draw animated emoji above the word with bounce effect
                    double emojiFloat = Math.sin(animationTime * 2 + i * Math.PI / 3) * 8;
                    drawBouncingEmoji(g2d, emoji, x, (int)(y + floatOffset - 35 + emojiFloat), i);
                    
                    // Draw enhanced shadow with multiple layers
                    drawMultiLayerShadow(g2d, word, x, (int)(y + floatOffset), shadowColor);
                    
                    // Draw rainbow glow effect around text
                    drawRainbowGlow(g2d, word, x, (int)(y + floatOffset), i);
                    
                    // Draw main text with scale effect
                    AffineTransform oldTransform = g2d.getTransform();
                    g2d.translate(x, y + floatOffset);
                    g2d.scale(scaleEffect, scaleEffect);
                    
                    // Main text with gradient effect
                    g2d.setFont(new Font("Arial Black", Font.BOLD, 36));
                    FontMetrics fm = g2d.getFontMetrics();
                    int textX = -fm.stringWidth(word) / 2;
                    int textY = fm.getAscent() / 2;
                    
                    // Create gradient paint for text
                    GradientPaint gradientPaint = new GradientPaint(
                        textX, textY - fm.getHeight()/2, primaryColor,
                        textX, textY + fm.getHeight()/2, glowColor
                    );
                    g2d.setPaint(gradientPaint);
                    g2d.drawString(word, textX, textY);
                    
                    g2d.setTransform(oldTransform);
                    
                    // Add enhanced sparkle effects with multiple colors
                    drawColorfulSparkles(g2d, x, (int)(y + floatOffset), i);
                    
                    // Add floating particles around each word
                    drawFloatingParticles(g2d, x, (int)(y + floatOffset), i);
                }
                
                // Draw connecting energy waves between words
                drawEnergyWaves(g2d, width, height);
            }

            private Color[] getEnhancedThemeColorsForWord(int wordIndex) {
                Color[] result = new Color[3]; // [primary, glow, shadow]
                
                switch (themeMode) {
                    case "dark":
                        switch (wordIndex) {
                            case 0: // Rock
                                result[0] = new Color(150, 220, 255); // Bright blue
                                result[1] = new Color(100, 180, 255, 150);
                                result[2] = new Color(50, 100, 150, 100);
                                break;
                            case 1: // Paper
                                result[0] = new Color(255, 180, 200); // Bright pink
                                result[1] = new Color(255, 120, 170, 150);
                                result[2] = new Color(150, 80, 100, 100);
                                break;
                            case 2: // Scissors
                                result[0] = new Color(150, 255, 150); // Bright green
                                result[1] = new Color(100, 255, 100, 150);
                                result[2] = new Color(50, 150, 50, 100);
                                break;
                        }
                        break;
                    case "light":
                        switch (wordIndex) {
                            case 0: // Rock
                                result[0] = new Color(70, 130, 180);
                                result[1] = new Color(100, 160, 210, 150);
                                result[2] = new Color(40, 80, 120, 100);
                                break;
                            case 1: // Paper
                                result[0] = new Color(255, 105, 180);
                                result[1] = new Color(255, 140, 200, 150);
                                result[2] = new Color(150, 70, 120, 100);
                                break;
                            case 2: // Scissors
                                result[0] = new Color(60, 179, 113);
                                result[1] = new Color(90, 209, 143, 150);
                                result[2] = new Color(30, 120, 70, 100);
                                break;
                        }
                        break;
                    case "neon":
                        switch (wordIndex) {
                            case 0: // Rock
                                result[0] = new Color(0, 255, 255); // Cyan
                                result[1] = new Color(0, 191, 255, 200);
                                result[2] = new Color(0, 100, 150, 120);
                                break;
                            case 1: // Paper
                                result[0] = new Color(255, 0, 255); // Magenta
                                result[1] = new Color(255, 100, 255, 200);
                                result[2] = new Color(150, 0, 150, 120);
                                break;
                            case 2: // Scissors
                                result[0] = new Color(0, 255, 127); // Spring green
                                result[1] = new Color(100, 255, 180, 200);
                                result[2] = new Color(0, 150, 80, 120);
                                break;
                        }
                        break;
                    case "synthwave":
                        switch (wordIndex) {
                            case 0: // Rock
                                result[0] = new Color(64, 224, 208); // Turquoise
                                result[1] = new Color(100, 255, 240, 200);
                                result[2] = new Color(30, 120, 110, 120);
                                break;
                            case 1: // Paper
                                result[0] = new Color(238, 130, 238); // Violet
                                result[1] = new Color(255, 180, 255, 200);
                                result[2] = new Color(150, 70, 150, 120);
                                break;
                            case 2: // Scissors
                                result[0] = new Color(255, 215, 0); // Gold
                                result[1] = new Color(255, 240, 100, 200);
                                result[2] = new Color(150, 120, 0, 120);
                                break;
                        }
                        break;
                    default:
                        // Default rainbow colors
                        result[0] = new Color(255, 100 + wordIndex * 50, 150);
                        result[1] = new Color(255, 150 + wordIndex * 30, 200, 150);
                        result[2] = new Color(100, 50, 100, 100);
                }
                
                return result;
            }

            private void drawRainbowBackground(Graphics2D g2d, int width, int height) {
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.1f));
                for (int i = 0; i < width; i += 20) {
                    float hue = (float)((animationTime * 0.1 + i * 0.01) % 1.0);
                    Color rainbowColor = Color.getHSBColor(hue, 0.7f, 0.8f);
                    g2d.setColor(rainbowColor);
                    g2d.fillRect(i, 0, 20, height);
                }
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
            }

            private void drawBouncingEmoji(Graphics2D g2d, String emoji, int x, int y, int wordIndex) {
                g2d.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));
                FontMetrics fm = g2d.getFontMetrics();
                int emojiX = x - fm.stringWidth(emoji) / 2;
                
                // Create rotating hue effect for emoji
                float hue = (float)((animationTime * 0.5 + wordIndex * 0.3) % 1.0);
                Color emojiColor = Color.getHSBColor(hue, 0.8f, 1.0f);
                g2d.setColor(emojiColor);
                g2d.drawString(emoji, emojiX, y);
            }

            private void drawMultiLayerShadow(Graphics2D g2d, String word, int x, int y, Color shadowColor) {
                g2d.setFont(new Font("Arial Black", Font.BOLD, 36));
                FontMetrics fm = g2d.getFontMetrics();
                int textX = x - fm.stringWidth(word) / 2;
                int textY = y + fm.getAscent() / 2;
                
                // Draw multiple shadow layers for depth
                for (int i = 5; i >= 1; i--) {
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.1f * i));
                    g2d.setColor(shadowColor);
                    g2d.drawString(word, textX + i, textY + i);
                }
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
            }

            private void drawRainbowGlow(Graphics2D g2d, String word, int x, int y, int wordIndex) {
                g2d.setFont(new Font("Arial Black", Font.BOLD, 40));
                FontMetrics fm = g2d.getFontMetrics();
                int textX = x - fm.stringWidth(word) / 2;
                int textY = y + fm.getAscent() / 2;
                
                // Draw rainbow glow layers
                for (int glow = 8; glow >= 1; glow--) {
                    float hue = (float)((animationTime * 0.3 + wordIndex * 0.2 + glow * 0.1) % 1.0);
                    Color glowColor = Color.getHSBColor(hue, 0.8f, 0.9f);
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
                    g2d.setColor(glowColor);
                    g2d.setFont(new Font("Arial Black", Font.BOLD, 36 + glow * 2));
                    g2d.drawString(word, textX, textY);
                }
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
            }

            private void drawColorfulSparkles(Graphics2D g2d, int centerX, int centerY, int wordIndex) {
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.8f));
                
                // Create colorful sparkles around each word
                for (int i = 0; i < 8; i++) {
                    double angle = animationTime * 3 + i * Math.PI * 2 / 8 + wordIndex * Math.PI / 3;
                    int sparkleX = centerX + (int)(Math.cos(angle) * (40 + Math.sin(animationTime * 4) * 15));
                    int sparkleY = centerY + (int)(Math.sin(angle) * (25 + Math.cos(animationTime * 4) * 10));
                    
                    // Use rainbow colors for sparkles
                    float hue = (float)((animationTime * 0.5 + i * 0.125 + wordIndex * 0.33) % 1.0);
                    Color sparkleColor = Color.getHSBColor(hue, 1.0f, 1.0f);
                    g2d.setColor(sparkleColor);
                    
                    // Draw sparkle as glowing star
                    int sparkleSize = 4 + (int)(Math.sin(animationTime * 5 + i) * 3);
                    
                    // Draw cross pattern for star effect
                    g2d.fillOval(sparkleX - sparkleSize/2, sparkleY - sparkleSize/2, sparkleSize, sparkleSize);
                    g2d.drawLine(sparkleX - sparkleSize*2, sparkleY, sparkleX + sparkleSize*2, sparkleY);
                    g2d.drawLine(sparkleX, sparkleY - sparkleSize*2, sparkleX, sparkleY + sparkleSize*2);
                    
                    // Add diagonal lines for more star effect
                    g2d.drawLine(sparkleX - sparkleSize, sparkleY - sparkleSize, sparkleX + sparkleSize, sparkleY + sparkleSize);
                    g2d.drawLine(sparkleX - sparkleSize, sparkleY + sparkleSize, sparkleX + sparkleSize, sparkleY - sparkleSize);
                }
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
            }

            private void drawFloatingParticles(Graphics2D g2d, int centerX, int centerY, int wordIndex) {
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f));
                
                // Create floating particles with different speeds
                for (int i = 0; i < 12; i++) {
                    double particleTime = animationTime * (1.0 + i * 0.1);
                    double angle = particleTime + i * Math.PI * 2 / 12;
                    double radius = 60 + Math.sin(particleTime * 2) * 20;
                    
                    int particleX = centerX + (int)(Math.cos(angle) * radius);
                    int particleY = centerY + (int)(Math.sin(angle) * radius * 0.7) + (int)(Math.sin(particleTime * 3) * 10);
                    
                    // Use theme colors for particles
                    float hue = (float)((particleTime * 0.2 + i * 0.083 + wordIndex * 0.25) % 1.0);
                    Color particleColor = Color.getHSBColor(hue, 0.7f, 0.9f);
                    g2d.setColor(particleColor);
                    
                    int particleSize = 2 + (int)(Math.sin(particleTime * 4) * 2);
                    g2d.fillOval(particleX - particleSize/2, particleY - particleSize/2, particleSize, particleSize);
                }
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
            }

            private void drawEnergyWaves(Graphics2D g2d, int width, int height) {
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f));
                g2d.setStroke(new BasicStroke(2.0f));
                
                // Draw flowing energy waves between the words
                for (int wave = 0; wave < 3; wave++) {
                    float hue = (float)((animationTime * 0.1 + wave * 0.33) % 1.0);
                    Color waveColor = Color.getHSBColor(hue, 0.8f, 0.7f);
                    g2d.setColor(waveColor);
                    
                    // Create sine wave pattern
                    for (int x = 0; x < width - 10; x += 10) {
                        double waveHeight = Math.sin((animationTime * 2 + x * 0.02 + wave * Math.PI / 2)) * 15;
                        int y1 = (int)(height / 2 + waveHeight);
                        int y2 = (int)(height / 2 + Math.sin((animationTime * 2 + (x + 10) * 0.02 + wave * Math.PI / 2)) * 15);
                        g2d.drawLine(x, y1, x + 10, y2);
                    }
                }
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
                g2d.setStroke(new BasicStroke(1.0f));
            }
        };

        // Add the animated title panel to the top panel
        topPanel.add(titlePanel);
        topPanel.add(Box.createVerticalStrut(10)); // Add some spacing
    }
}
