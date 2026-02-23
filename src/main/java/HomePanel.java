import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Home screen panel with title and start game button
 */
public class HomePanel extends JPanel {
    private JButton startButton;
    private JButton settingsButton;
    private JPanel mainContentPanel;
    private JPanel loadingPanel;
    private Timer loadingTimer;
    private int loadingProgress = 0;
    private JFrame gameFrame;
    private Image backgroundImage;
    
    // Theme management fields
    private String themeMode = "dark"; // Options: "dark", "light", "neon", "synthwave"
    private boolean isDarkMode = true; // Default to dark mode
    
    // Music state
    private boolean isMusicEnabled = true; // Default music on
    
    public HomePanel(MainFrame mainFrame) {
        // MainFrame parameter kept for API compatibility but not stored as it's not needed
        initializePanel();
        createComponents();
        setupLayout();
        
        // Initialize background music
        initializeBackgroundMusic();
        
        // Play homepage start sound
        playHomepageStartSound();
    }
    
    private void initializePanel() {
        // Initialize with default theme
        updateBackgroundColors();
        setFocusable(true);
        
        // Load background image
        ImageIcon rpsIcon = loadImageIcon("image/RPS.png");
        if (rpsIcon != null) {
            backgroundImage = rpsIcon.getImage();
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        if (backgroundImage != null) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            
            // Create a semi-transparent overlay for better text readability
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
            
            // Scale and center the background image
            int imgWidth = backgroundImage.getWidth(null);
            int imgHeight = backgroundImage.getHeight(null);
            int panelWidth = getWidth();
            int panelHeight = getHeight();
            
            // Calculate scaling to cover the entire panel while maintaining aspect ratio
            double scaleX = (double) panelWidth / imgWidth;
            double scaleY = (double) panelHeight / imgHeight;
            double scale = Math.max(scaleX, scaleY); // Use max to cover entire panel
            
            int scaledWidth = (int) (imgWidth * scale);
            int scaledHeight = (int) (imgHeight * scale);
            
            // Center the image
            int x = (panelWidth - scaledWidth) / 2;
            int y = (panelHeight - scaledHeight) / 2;
            
            // Draw the background image
            g2d.drawImage(backgroundImage, x, y, scaledWidth, scaledHeight, null);
            
            // Add a dark overlay for better text contrast
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f));
            Color overlayColor;
            switch (themeMode) {
                case "dark" -> overlayColor = ThemeManager.DARK_BG_PRIMARY;
                case "light" -> overlayColor = ThemeManager.LIGHT_BG_PRIMARY;
                case "neon" -> overlayColor = ThemeManager.NEON_BG_PRIMARY;
                case "synthwave" -> overlayColor = ThemeManager.SYNTHWAVE_BG_PRIMARY;
                default -> overlayColor = ThemeManager.DARK_BG_PRIMARY;
            }
            g2d.setColor(overlayColor);
            g2d.fillRect(0, 0, panelWidth, panelHeight);
            
            g2d.dispose();
        }
    }
    
    private void createComponents() {
        setLayout(new CardLayout());
        
        // Create main content panel with BorderLayout
        mainContentPanel = new JPanel(new BorderLayout());
        mainContentPanel.setOpaque(false); // Make transparent to show background
        mainContentPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
        
        // Create panel for buttons on the right side (vertical layout)
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setOpaque(false);
        rightPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Add padding

        // Start game button
        startButton = createStyledButton("Start Game");
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SoundManager.getInstance().playSound(SoundManager.SOUND_BUTTON_CLICK);
                showLoadingAndLaunchGame();
            }
        });

        // Settings button
        settingsButton = createStyledButton("Settings");
        settingsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SoundManager.getInstance().playSound(SoundManager.SOUND_BUTTON_CLICK);
                showSettingsMenu(settingsButton);
            }
        });

        // Exit button
        JButton exitButton = createStyledButton("Exit");
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SoundManager.getInstance().playSound(SoundManager.SOUND_BUTTON_CLICK);
                System.exit(0);
            }
        });

        // Add buttons vertically with spacing - centered in the middle
        rightPanel.add(Box.createVerticalGlue()); // Push buttons to center from top
        rightPanel.add(startButton);
        rightPanel.add(Box.createVerticalStrut(15)); // Space between buttons
        rightPanel.add(settingsButton);
        rightPanel.add(Box.createVerticalStrut(15)); // Space between buttons
        rightPanel.add(exitButton);
        rightPanel.add(Box.createVerticalGlue()); // Push buttons to center from bottom
        
        // Add the button panel to the right side of the BorderLayout
        mainContentPanel.add(rightPanel, BorderLayout.EAST);
        
        // Add stylish animated "Rock Paper Scissors" text positioned at bottom of logo
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0)); // Center align for logo positioning
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 100)); // Push text 100 pixels more to the left
        
        // Create animated colorful label matching wallpaper style
        JLabel bottomLabel = new JLabel("Rock Paper Scissors") {
            private Timer animationTimer;
            private float glowIntensity = 0.0f;
            private float waveOffset = 0.0f;
            private float colorShift = 0.0f;
            private float scaleEffect = 0.0f;
            private long lastUpdate = System.currentTimeMillis();
            
            {
                // Start animation timer
                animationTimer = new Timer(30, actionEvent -> {
                    long currentTime = System.currentTimeMillis();
                    float deltaTime = (currentTime - lastUpdate) / 1000.0f;
                    lastUpdate = currentTime;
                    
                    glowIntensity += deltaTime * 3.0f;
                    waveOffset += deltaTime * 4.0f;
                    colorShift += deltaTime * 2.0f;
                    scaleEffect += deltaTime * 1.5f;
                    repaint();
                });
                animationTimer.start();
            }
            
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                
                String text = getText();
                Font baseFont = new Font("Segoe UI", Font.BOLD, 48);
                float scale = 1.0f + 0.1f * (float) Math.sin(scaleEffect);
                Font font = baseFont.deriveFont(baseFont.getSize() * scale);
                g2d.setFont(font);
                FontMetrics fm = g2d.getFontMetrics();
                
                int textWidth = fm.stringWidth(text);
                int textHeight = fm.getHeight();
                int x = (getWidth() - textWidth) / 2; // Center the text within the label
                int y = (getHeight() + fm.getAscent()) / 2;
                
                // Draw vibrant multi-colored glow layers
                float baseGlow = 0.8f + 0.2f * (float) Math.sin(glowIntensity);
                
                // Rainbow outer glow - largest radius with shifting colors
                for (int i = 20; i > 0; i--) {
                    float alpha = baseGlow * (1.0f - (float)i / 20.0f) * 0.4f;
                    float hue = ((colorShift + i * 0.05f) % 1.0f);
                    Color rainbowColor = Color.getHSBColor(hue, 0.8f, 1.0f);
                    Color glowColor = new Color(
                        rainbowColor.getRed(),
                        rainbowColor.getGreen(),
                        rainbowColor.getBlue(),
                        Math.max(0, Math.min(255, (int)(alpha * 255)))
                    );
                    g2d.setColor(glowColor);
                    g2d.drawString(text, x - i/2, y - i/2);
                    g2d.drawString(text, x + i/2, y + i/2);
                    g2d.drawString(text, x - i/2, y + i/2);
                    g2d.drawString(text, x + i/2, y - i/2);
                }
                
                // Vibrant middle glow with electric colors
                for (int i = 12; i > 0; i--) {
                    float alpha = baseGlow * (1.0f - (float)i / 12.0f) * 0.7f;
                    float colorPhase = colorShift + i * 0.1f;
                    Color electricColor = new Color(
                        (int)(128 + 127 * Math.sin(colorPhase)),
                        (int)(128 + 127 * Math.sin(colorPhase + Math.PI/3)),
                        (int)(128 + 127 * Math.sin(colorPhase + 2*Math.PI/3))
                    );
                    Color glowColor = new Color(
                        electricColor.getRed(),
                        electricColor.getGreen(),
                        electricColor.getBlue(),
                        Math.max(0, Math.min(255, (int)(alpha * 255)))
                    );
                    g2d.setColor(glowColor);
                    g2d.drawString(text, x - i/3, y - i/3);
                    g2d.drawString(text, x + i/3, y + i/3);
                }
                
                // Bright inner glow with pulsing effect
                for (int i = 6; i > 0; i--) {
                    float alpha = baseGlow * (1.0f - (float)i / 6.0f) * 0.6f;
                    float pulse = 0.5f + 0.5f * (float) Math.sin(glowIntensity * 2);
                    g2d.setColor(new Color(255, 255, 255, Math.max(0, Math.min(255, (int)(alpha * 255 * pulse)))));
                    g2d.drawString(text, x - i/4, y - i/4);
                    g2d.drawString(text, x + i/4, y + i/4);
                }
                
                // Draw main text with dynamic rainbow gradient
                String[] words = text.split(" ");
                int wordX = x;
                for (int wordIndex = 0; wordIndex < words.length; wordIndex++) {
                    String word = words[wordIndex];
                    for (int i = 0; i < word.length(); i++) {
                        char c = word.charAt(i);
                        String charStr = String.valueOf(c);
                        int charWidth = fm.charWidth(c);
                        
                        // Dynamic wave effect per character
                        float waveY = 5.0f * (float) Math.sin(waveOffset + (wordX + i) * 0.1f);
                        
                        // Rainbow colors per character
                        float charHue = ((colorShift + (wordX + i) * 0.02f) % 1.0f);
                        Color charColor = Color.getHSBColor(charHue, 0.9f, 1.0f);
                        
                        // Add shimmer effect
                        float shimmer = 0.7f + 0.3f * (float) Math.sin(glowIntensity * 3 + i);
                        Color shimmerColor = new Color(
                            Math.min(255, (int)(charColor.getRed() * shimmer)),
                            Math.min(255, (int)(charColor.getGreen() * shimmer)),
                            Math.min(255, (int)(charColor.getBlue() * shimmer))
                        );
                        
                        g2d.setColor(shimmerColor);
                        g2d.drawString(charStr, wordX, (int)(y + waveY));
                        
                        // Add sparkle particles around characters
                        if ((int)(glowIntensity * 5) % 30 < 15 && i % 3 == 0) {
                            for (int sparkle = 0; sparkle < 3; sparkle++) {
                                float sparkleAngle = (float)(Math.random() * Math.PI * 2);
                                float sparkleDistance = 10 + (float)(Math.random() * 20);
                                int sparkleX = wordX + charWidth/2 + (int)(Math.cos(sparkleAngle) * sparkleDistance);
                                int sparkleY = (int)(y + waveY - textHeight/2 + Math.sin(sparkleAngle) * sparkleDistance);
                                
                                g2d.setColor(Color.WHITE);
                                g2d.fillOval(sparkleX - 1, sparkleY - 1, 2, 2);
                                
                                // Add colored sparkles
                                g2d.setColor(Color.getHSBColor((float)Math.random(), 1.0f, 1.0f));
                                g2d.fillOval(sparkleX, sparkleY, 1, 1);
                            }
                        }
                        
                        wordX += charWidth;
                    }
                    if (wordIndex < words.length - 1) {
                        wordX += fm.stringWidth(" "); // Space between words
                    }
                }
                
                g2d.dispose();
            }
        };
        
        bottomLabel.setHorizontalAlignment(SwingConstants.LEFT);
        bottomLabel.setVerticalAlignment(SwingConstants.CENTER);
        bottomLabel.setPreferredSize(new Dimension(800, 100));
        bottomLabel.setOpaque(false);
        
        bottomPanel.add(bottomLabel);
        mainContentPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        // Create loading panel
        createLoadingPanel();
        
        // Add panels to card layout
        add(mainContentPanel, "MAIN");
        add(loadingPanel, "LOADING");
    }
    
    private void setupLayout() {
        // Add some padding around the content for main panel only
        // Note: Loading panel will handle its own layout without border padding
    }
    
    private void createLoadingPanel() {
        loadingPanel = new JPanel(new GridBagLayout()) {
            private int animationFrame = 0;
            private float pulseScale = 1.0f;
            private int particleCount = 12;
            private float[] particleAngles = new float[particleCount];
            private float[] particleDistances = new float[particleCount];
            private Color[] particleColors = new Color[particleCount];
            private float[] wavePhases = new float[6];
            
            {
                // Initialize particles
                for (int i = 0; i < particleCount; i++) {
                    particleAngles[i] = (float) (i * 2 * Math.PI / particleCount);
                    particleDistances[i] = 60 + (float) (Math.random() * 30);
                    particleColors[i] = new Color(
                        ThemeManager.DARK_ACCENT_PRIMARY.getRed(),
                        ThemeManager.DARK_ACCENT_PRIMARY.getGreen(),
                        ThemeManager.DARK_ACCENT_PRIMARY.getBlue(),
                        150 + (int) (Math.random() * 105)
                    );
                }
                
                // Initialize wave phases
                for (int i = 0; i < wavePhases.length; i++) {
                    wavePhases[i] = (float) (Math.random() * Math.PI * 2);
                }
            }
            
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                
                int centerX = getWidth() / 2;
                int centerY = getHeight() / 2;
                
                // Draw multiple background waves
                for (int wave = 0; wave < 3; wave++) {
                    float waveRadius = 120 + wave * 40;
                    float waveAlpha = 0.3f - wave * 0.1f;
                    float wavePhase = wavePhases[wave] + animationFrame * 0.03f;
                    
                    RadialGradientPaint wavePaint = new RadialGradientPaint(
                        centerX, centerY, waveRadius * pulseScale,
                        new float[]{0.0f, 0.8f, 1.0f},
                        new Color[]{
                            new Color(ThemeManager.DARK_ACCENT_PRIMARY.getRed(), 
                                     ThemeManager.DARK_ACCENT_PRIMARY.getGreen(), 
                                     ThemeManager.DARK_ACCENT_PRIMARY.getBlue(), (int)(50 * waveAlpha)),
                            new Color(ThemeManager.DARK_ACCENT_SECONDARY.getRed(), 
                                     ThemeManager.DARK_ACCENT_SECONDARY.getGreen(), 
                                     ThemeManager.DARK_ACCENT_SECONDARY.getBlue(), (int)(30 * waveAlpha)),
                            new Color(0, 0, 0, 0)
                        }
                    );
                    g2d.setPaint(wavePaint);
                    float waveScale = pulseScale + 0.2f * (float) Math.sin(wavePhase);
                    int waveSize = (int)(waveRadius * 2 * waveScale);
                    g2d.fillOval(centerX - waveSize/2, centerY - waveSize/2, waveSize, waveSize);
                }
                
                // Draw orbiting particles with enhanced effects
                for (int i = 0; i < particleCount; i++) {
                    float angle = particleAngles[i] + animationFrame * 0.025f;
                    float distance = particleDistances[i] * (pulseScale + 0.1f * (float) Math.sin(animationFrame * 0.08f + i));
                    int x = centerX + (int) (Math.cos(angle) * distance);
                    int y = centerY + (int) (Math.sin(angle) * distance);
                    
                    // Enhanced particle with bloom effect
                    float particlePulse = 1.0f + 0.5f * (float) Math.sin(animationFrame * 0.12f + i * 0.5f);
                    int size = (int) (12 * particlePulse);
                    
                    // Draw particle bloom
                    RadialGradientPaint particleBloom = new RadialGradientPaint(
                        x, y, size * 2,
                        new float[]{0.0f, 0.5f, 1.0f},
                        new Color[]{
                            new Color(particleColors[i].getRed(), particleColors[i].getGreen(), particleColors[i].getBlue(), 100),
                            new Color(particleColors[i].getRed(), particleColors[i].getGreen(), particleColors[i].getBlue(), 50),
                            new Color(0, 0, 0, 0)
                        }
                    );
                    g2d.setPaint(particleBloom);
                    g2d.fillOval(x - size, y - size, size * 2, size * 2);
                    
                    // Draw core particle
                    g2d.setColor(particleColors[i]);
                    g2d.fillOval(x - size/3, y - size/3, size*2/3, size*2/3);
                    
                    // Add sparkle effect
                    if (animationFrame % 30 < 15) {
                        g2d.setColor(Color.WHITE);
                        g2d.fillOval(x - 1, y - 1, 2, 2);
                    }
                }
                
                // Draw multiple spinning rings with varying speeds
                g2d.setStroke(new BasicStroke(4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                
                // Outer ring system
                for (int ring = 0; ring < 3; ring++) {
                    int ringRadius = 45 + ring * 15;
                    float ringSpeed = 1.0f + ring * 0.3f;
                    int arcLength = 90 + ring * 30;
                    
                    GradientPaint ringPaint = new GradientPaint(
                        centerX - ringRadius, centerY - ringRadius, 
                        ring % 2 == 0 ? ThemeManager.DARK_ACCENT_PRIMARY : ThemeManager.DARK_ACCENT_SECONDARY,
                        centerX + ringRadius, centerY + ringRadius, 
                        ring % 2 == 0 ? ThemeManager.DARK_ACCENT_SECONDARY : Color.WHITE
                    );
                    g2d.setPaint(ringPaint);
                    
                    int startAngle = (int)(animationFrame * ringSpeed * (ring % 2 == 0 ? 1 : -1));
                    g2d.drawArc(centerX - ringRadius, centerY - ringRadius, 
                               ringRadius * 2, ringRadius * 2, startAngle, arcLength);
                }
                
                // Center hexagonal core with pulsing effect
                int[] hexX = new int[6];
                int[] hexY = new int[6];
                int hexRadius = (int) (20 * pulseScale);
                
                for (int i = 0; i < 6; i++) {
                    double hexAngle = i * Math.PI / 3 + animationFrame * 0.02;
                    hexX[i] = centerX + (int) (Math.cos(hexAngle) * hexRadius);
                    hexY[i] = centerY + (int) (Math.sin(hexAngle) * hexRadius);
                }
                
                RadialGradientPaint hexPaint = new RadialGradientPaint(
                    centerX, centerY, hexRadius,
                    new float[]{0.0f, 0.7f, 1.0f},
                    new Color[]{Color.WHITE, ThemeManager.DARK_ACCENT_PRIMARY, ThemeManager.DARK_ACCENT_SECONDARY}
                );
                g2d.setPaint(hexPaint);
                g2d.fillPolygon(hexX, hexY, 6);
                
                // Add energy lines
                g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                for (int i = 0; i < 8; i++) {
                    float lineAngle = (float) (i * Math.PI / 4 + animationFrame * 0.05);
                    int lineLength = 30 + (int) (10 * Math.sin(animationFrame * 0.1f + i));
                    int x1 = centerX + (int) (Math.cos(lineAngle) * 25);
                    int y1 = centerY + (int) (Math.sin(lineAngle) * 25);
                    int x2 = centerX + (int) (Math.cos(lineAngle) * lineLength);
                    int y2 = centerY + (int) (Math.sin(lineAngle) * lineLength);
                    
                    GradientPaint linePaint = new GradientPaint(
                        x1, y1, Color.WHITE,
                        x2, y2, new Color(255, 255, 255, 0)
                    );
                    g2d.setPaint(linePaint);
                    g2d.drawLine(x1, y1, x2, y2);
                }
                
                // Update animation values
                animationFrame++;
                pulseScale = 1.0f + 0.4f * (float) Math.sin(animationFrame * 0.08f);
                
                g2d.dispose();
            }
        };
        loadingPanel.setBackground(ThemeManager.DARK_BG_PRIMARY);
        
        GridBagConstraints gbc = new GridBagConstraints();
        
        // Loading title with animated glow effect
        JLabel loadingTitle = new JLabel("Loading Game...") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                FontMetrics fm = g2d.getFontMetrics();
                String text = getText();
                int x = (getWidth() - fm.stringWidth(text)) / 2;
                int y = (getHeight() + fm.getAscent()) / 2;
                
                // Draw glow effect
                float glow = 0.5f + 0.5f * (float) Math.sin(System.currentTimeMillis() * 0.005f);
                for (int i = 8; i > 0; i--) {
                    float alpha = glow * (1 - (float)i/8);
                    g2d.setColor(new Color(
                        ThemeManager.DARK_ACCENT_PRIMARY.getRed(),
                        ThemeManager.DARK_ACCENT_PRIMARY.getGreen(),
                        ThemeManager.DARK_ACCENT_PRIMARY.getBlue(),
                        (int)(100 * alpha)
                    ));
                    g2d.drawString(text, x - i/2, y - i/2);
                    g2d.drawString(text, x + i/2, y + i/2);
                }
                
                // Draw main text
                g2d.setColor(getForeground());
                g2d.drawString(text, x, y);
                g2d.dispose();
            }
        };
        loadingTitle.setFont(new Font("Segoe UI", Font.BOLD, 32));
        loadingTitle.setForeground(ThemeManager.DARK_TEXT_PRIMARY);
        loadingTitle.setHorizontalAlignment(SwingConstants.CENTER);
        loadingTitle.setOpaque(false);
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 0, 200, 0); // Minimal top margin, much larger bottom margin to push content very high
        loadingPanel.add(loadingTitle, gbc);
        
        // Animated progress bar
        JPanel progressBarPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int width = getWidth() - 40;
                int height = 8;
                int x = 20;
                int y = (getHeight() - height) / 2;
                
                // Background bar
                g2d.setColor(new Color(ThemeManager.DARK_BG_SECONDARY.getRed(),
                                     ThemeManager.DARK_BG_SECONDARY.getGreen(),
                                     ThemeManager.DARK_BG_SECONDARY.getBlue(), 150));
                g2d.fillRoundRect(x, y, width, height, height, height);
                
                // Progress bar with gradient
                int progressWidth = (int) (width * loadingProgress / 80.0f);
                if (progressWidth > 0) {
                    GradientPaint progressGradient = new GradientPaint(
                        x, y, ThemeManager.DARK_ACCENT_PRIMARY,
                        x + progressWidth, y, ThemeManager.DARK_ACCENT_SECONDARY
                    );
                    g2d.setPaint(progressGradient);
                    g2d.fillRoundRect(x, y, progressWidth, height, height, height);
                    
                    // Add shine effect
                    float shine = 0.7f + 0.3f * (float) Math.sin(System.currentTimeMillis() * 0.01f);
                    g2d.setColor(new Color(255, 255, 255, (int)(100 * shine)));
                    g2d.fillRoundRect(x, y, progressWidth, height/3, height/3, height/3);
                }
                
                g2d.dispose();
            }
        };
        progressBarPanel.setOpaque(false);
        progressBarPanel.setPreferredSize(new Dimension(300, 20));
        
        gbc.gridy = 1;
        gbc.insets = new Insets(50, 0, 20, 0); // Reduced top margin since title is higher
        loadingPanel.add(progressBarPanel, gbc);
        
        // Progress text with typing effect
        JLabel progressLabel = new JLabel("Initializing...");
        progressLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        progressLabel.setForeground(ThemeManager.DARK_TEXT_SECONDARY);
        progressLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 0, 0);
        loadingPanel.add(progressLabel, gbc);
        
        // Animation timer with enhanced effects
        loadingTimer = new Timer(50, e -> {
            loadingPanel.repaint();
            progressBarPanel.repaint();
            loadingTitle.repaint();
            loadingProgress++;
            
            // Update progress text
            if (loadingProgress < 20) {
                progressLabel.setText("Initializing game engine...");
            } else if (loadingProgress < 40) {
                progressLabel.setText("Loading game resources...");
            } else if (loadingProgress < 60) {
                progressLabel.setText("Preparing game environment...");
            } else {
                progressLabel.setText("Almost ready to play!");
            }
            
            // Launch game after loading animation
            if (loadingProgress >= 80) {
                loadingTimer.stop();
                launchGame();
            }
        });
    }
    
    private void showLoadingAndLaunchGame() {
        // Play loading sound
        SoundManager.getInstance().playSound(SoundManager.SOUND_LOADING);
        
        // Switch to loading panel
        CardLayout cl = (CardLayout) getLayout();
        cl.show(this, "LOADING");
        
        // Reset and start loading animation
        loadingProgress = 0;
        loadingTimer.start();
    }
    
    private void launchGame() {
        // Close existing game frame if open
        if (gameFrame != null) {
            gameFrame.dispose();
        }
        
        // Create new game instance with current theme settings
        gameFrame = new RockPaperScissorsGame(themeMode, isDarkMode);
        gameFrame.setVisible(true);
        
        // Add window listener to handle game frame closing
        gameFrame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                gameFrame = null;
                // Return to main content when game is closed
                CardLayout cl = (CardLayout) getLayout();
                cl.show(HomePanel.this, "MAIN");
            }
        });
    }
    
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text) {
            private boolean isHovered = false;
            private boolean isPressed = false;
            private Timer animationTimer;
            private float glowIntensity = 0.0f;
            private float pulseEffect = 0.0f;
            
            {
                // Initialize animation timer for continuous effects
                animationTimer = new Timer(30, actionEvent -> {
                    glowIntensity += 0.05f;
                    pulseEffect += 0.03f;
                    repaint();
                });
                animationTimer.start();
            }
            
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                
                int width = getWidth();
                int height = getHeight();
                
                // Get current state from client properties
                isHovered = Boolean.TRUE.equals(getClientProperty("isHovered"));
                isPressed = Boolean.TRUE.equals(getClientProperty("isPressed"));
                
                // Get theme-appropriate colors
                Color primaryColor = ThemeManager.getAccentColor(themeMode);
                Color secondaryColor;
                Color textColor = ThemeManager.getTextColor(themeMode);
                Color glowColor;
                
                switch (themeMode) {
                    case "dark" -> {
                        secondaryColor = ThemeManager.DARK_ACCENT_SECONDARY;
                        glowColor = ThemeManager.DARK_ACCENT_PRIMARY.brighter();
                    }
                    case "light" -> {
                        secondaryColor = ThemeManager.LIGHT_ACCENT_SECONDARY;
                        glowColor = ThemeManager.LIGHT_ACCENT_PRIMARY.brighter();
                    }
                    case "neon" -> {
                        secondaryColor = ThemeManager.NEON_ACCENT_SECONDARY;
                        glowColor = ThemeManager.NEON_ACCENT_PRIMARY;
                    }
                    case "synthwave" -> {
                        secondaryColor = ThemeManager.SYNTHWAVE_ACCENT_SECONDARY;
                        glowColor = ThemeManager.SYNTHWAVE_ACCENT_PRIMARY.brighter();
                    }
                    default -> {
                        secondaryColor = ThemeManager.DARK_ACCENT_SECONDARY;
                        glowColor = ThemeManager.DARK_ACCENT_PRIMARY.brighter();
                    }
                }
                
                // Create dynamic gradient based on button state
                Color startColor, endColor;
                float scale = 1.0f;
                
                if (isPressed) {
                    startColor = primaryColor.darker().darker();
                    endColor = secondaryColor.darker();
                    scale = 0.95f;
                } else if (isHovered) {
                    startColor = primaryColor.brighter();
                    endColor = secondaryColor.brighter();
                    scale = 1.05f + 0.02f * (float) Math.sin(pulseEffect * 2);
                } else {
                    startColor = primaryColor;
                    endColor = secondaryColor;
                    scale = 1.0f + 0.01f * (float) Math.sin(pulseEffect);
                }
                
                // Apply scale transformation
                int scaledWidth = (int) (width * scale);
                int scaledHeight = (int) (height * scale);
                int offsetX = (width - scaledWidth) / 2;
                int offsetY = (height - scaledHeight) / 2;
                
                // Draw outer glow effect
                if (isHovered || "neon".equals(themeMode)) {
                    float glowAlpha = isHovered ? 0.6f + 0.2f * (float) Math.sin(glowIntensity) : 
                                                 0.3f + 0.1f * (float) Math.sin(glowIntensity * 0.5f);
                    for (int i = 8; i > 0; i--) {
                        float alpha = glowAlpha * (1.0f - (float) i / 8.0f);
                        Color currentGlow = new Color(
                            glowColor.getRed(),
                            glowColor.getGreen(),
                            glowColor.getBlue(),
                            Math.max(0, Math.min(255, (int) (alpha * 255)))
                        );
                        g2d.setColor(currentGlow);
                        int glowOffset = i * 2;
                        g2d.fillRoundRect(
                            offsetX - glowOffset, offsetY - glowOffset,
                            scaledWidth + glowOffset * 2, scaledHeight + glowOffset * 2,
                            25 + glowOffset, 25 + glowOffset
                        );
                    }
                }
                
                // Create main gradient
                GradientPaint mainGradient = new GradientPaint(
                    0, 0, startColor,
                    0, scaledHeight, endColor
                );
                g2d.setPaint(mainGradient);
                g2d.fillRoundRect(offsetX, offsetY, scaledWidth, scaledHeight, 20, 20);
                
                // Add inner highlight for depth
                Color highlightColor = new Color(255, 255, 255, isHovered ? 60 : 30);
                GradientPaint highlightGradient = new GradientPaint(
                    0, 0, highlightColor,
                    0, scaledHeight / 3, new Color(255, 255, 255, 0)
                );
                g2d.setPaint(highlightGradient);
                g2d.fillRoundRect(offsetX + 2, offsetY + 2, scaledWidth - 4, scaledHeight / 3, 18, 18);
                
                // Add subtle border
                g2d.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                Color borderColor = isHovered ? glowColor.brighter() : 
                                   new Color(255, 255, 255, 100);
                g2d.setColor(borderColor);
                g2d.drawRoundRect(offsetX + 1, offsetY + 1, scaledWidth - 2, scaledHeight - 2, 19, 19);
                
                // Draw animated sparkles for special themes
                if ("neon".equals(themeMode) || "synthwave".equals(themeMode)) {
                    drawSparkles(g2d, offsetX, offsetY, scaledWidth, scaledHeight);
                }
                
                // Draw button text with enhanced styling
                drawStyledText(g2d, getText(), textColor, width, height, scale);
                
                g2d.dispose();
            }
            
            private void drawSparkles(Graphics2D g2d, int x, int y, int width, int height) {
                for (int i = 0; i < 5; i++) {
                    float sparklePhase = glowIntensity + i * 1.2f;
                    if ((int) (sparklePhase * 10) % 40 < 20) {
                        int sparkleX = x + 10 + (int) (Math.sin(sparklePhase + i) * (width - 20));
                        int sparkleY = y + 10 + (int) (Math.cos(sparklePhase + i * 1.5) * (height - 20));
                        
                        Color sparkleColor = "neon".equals(themeMode) ? 
                            Color.CYAN : new Color(255, 100, 255);
                        g2d.setColor(new Color(sparkleColor.getRed(), sparkleColor.getGreen(), 
                                             sparkleColor.getBlue(), 200));
                        
                        // Draw star shape
                        int[] xPoints = {sparkleX, sparkleX + 3, sparkleX + 6, sparkleX + 3, sparkleX, sparkleX - 3, sparkleX - 6, sparkleX - 3};
                        int[] yPoints = {sparkleY - 6, sparkleY - 2, sparkleY, sparkleY + 2, sparkleY + 6, sparkleY + 2, sparkleY, sparkleY - 2};
                        g2d.fillPolygon(xPoints, yPoints, 8);
                    }
                }
            }
            
            private void drawStyledText(Graphics2D g2d, String text, Color textColor, int width, int height, float scale) {
                // Set font with scale (reduced from 18 to 14 for smaller buttons)
                Font baseFont = new Font("Segoe UI", Font.BOLD, (int) (14 * scale));
                g2d.setFont(baseFont);
                FontMetrics fm = g2d.getFontMetrics();
                
                int textWidth = fm.stringWidth(text);
                int textHeight = fm.getHeight();
                int x = (width - textWidth) / 2;
                int y = (height + fm.getAscent() - fm.getDescent()) / 2;
                
                // Draw text shadow
                g2d.setColor(new Color(0, 0, 0, 100));
                g2d.drawString(text, x + 2, y + 2);
                
                // Draw main text with possible glow
                if (isHovered && ("neon".equals(themeMode) || "synthwave".equals(themeMode))) {
                    // Draw text glow
                    for (int i = 3; i > 0; i--) {
                        float alpha = 0.3f * (1.0f - (float) i / 3.0f);
                        g2d.setColor(new Color(textColor.getRed(), textColor.getGreen(), 
                                             textColor.getBlue(), (int) (alpha * 255)));
                        g2d.drawString(text, x - i, y - i);
                        g2d.drawString(text, x + i, y + i);
                    }
                }
                
                // Draw main text
                g2d.setColor(textColor);
                g2d.drawString(text, x, y);
                
                // Add shimmer effect for light theme
                if ("light".equals(themeMode) && isHovered) {
                    float shimmerPos = (glowIntensity * 2) % 2.0f - 1.0f;
                    int shimmerX = x + (int) (shimmerPos * textWidth);
                    if (shimmerPos > -1.0f && shimmerPos < 1.0f) {
                        g2d.setColor(new Color(255, 255, 255, 150));
                        g2d.fillRect(shimmerX, y - fm.getAscent(), 3, textHeight);
                    }
                }
            }
            
            @Override
            protected void paintBorder(Graphics g) {
                // Custom border is painted in paintComponent
            }
        };
        
        // Set button properties
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setOpaque(false);
        button.setPreferredSize(new Dimension(160, 40));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Enhanced mouse interactions
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.putClientProperty("isHovered", true);
                button.repaint();
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.putClientProperty("isHovered", false);
                button.repaint();
            }
            
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                button.putClientProperty("isPressed", true);
                button.repaint();
            }
            
            @Override
            public void mouseReleased(java.awt.event.MouseEvent e) {
                button.putClientProperty("isPressed", false);
                button.repaint();
            }
        });
        
        return button;
    }
    
    @Override
    public void requestFocus() {
        super.requestFocus();
        startButton.requestFocus();
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
            java.net.URL imageUrl = getClass().getResource("/" + imagePath);
            if (imageUrl != null) {
                return new ImageIcon(imageUrl);
            }
            
            // Try to load from src/main/resources
            java.io.File file = new java.io.File("src/main/resources/" + imagePath);
            if (file.exists()) {
                return new ImageIcon(file.getAbsolutePath());
            }
            
            System.out.println("Image not found: " + imagePath);
            return null;
        } catch (Exception e) {
            System.out.println("Error loading image: " + imagePath + " - " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Updates the theme of the HomePanel based on the current themeMode
     */
    private void updateTheme() {
        // Update background colors and repaint
        updateBackgroundColors();
        
        // Force repaint of the entire UI
        SwingUtilities.updateComponentTreeUI(this);
        repaint();
    }
    
    /**
     * Updates background colors based on current theme
     */
    private void updateBackgroundColors() {
        Color bgColor;
        switch (themeMode) {
            case "dark" -> bgColor = ThemeManager.DARK_BG_PRIMARY;
            case "light" -> bgColor = ThemeManager.LIGHT_BG_PRIMARY;
            case "neon" -> bgColor = ThemeManager.NEON_BG_PRIMARY;
            case "synthwave" -> bgColor = ThemeManager.SYNTHWAVE_BG_PRIMARY;
            default -> bgColor = ThemeManager.DARK_BG_PRIMARY;
        }
        
        setBackground(bgColor);
        if (loadingPanel != null) {
            loadingPanel.setBackground(bgColor);
        }
    }
    
    /**
     * Shows a settings popup menu with various game options
     */
    private void showSettingsMenu(JButton sourceButton) {
        JFrame settingsFrame = new JFrame("Game Settings");
        settingsFrame.setUndecorated(true);
        settingsFrame.setResizable(false);
        settingsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        JPanel settingsPanel = new JPanel();
        settingsPanel.setLayout(new BoxLayout(settingsPanel, BoxLayout.Y_AXIS));
        settingsPanel.setBackground(isDarkMode ? 
                ThemeManager.DARK_BG_SECONDARY : ThemeManager.LIGHT_BG_SECONDARY);
        settingsPanel.setBorder(BorderFactory.createLineBorder(
                isDarkMode ? ThemeManager.DARK_TEXT_SECONDARY : ThemeManager.LIGHT_TEXT_SECONDARY, 1));
        
        // Custom close button at the top-right
        JMenuItem closeButton = new JMenuItem() {
            private boolean isHovered = false;
            
            {
                setPreferredSize(new Dimension(30, 30));
                addMouseListener(new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseEntered(java.awt.event.MouseEvent e) {
                        isHovered = true;
                        repaint();
                    }
                    
                    @Override
                    public void mouseExited(java.awt.event.MouseEvent e) {
                        isHovered = false;
                        repaint();
                    }
                });
            }
            
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int size = Math.min(getWidth(), getHeight());
                int x = (getWidth() - size) / 2;
                int y = (getHeight() - size) / 2;
                
                // Background circle
                if (isHovered) {
                    g2d.setColor(new Color(239, 68, 68, 100)); // Red highlight when hovered
                    g2d.fillOval(x + 2, y + 2, size - 4, size - 4);
                }
                
                // Draw X icon
                g2d.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2d.setColor(isHovered ? 
                    new Color(239, 68, 68) : 
                    (isDarkMode ? ThemeManager.DARK_TEXT_SECONDARY : ThemeManager.LIGHT_TEXT_SECONDARY));
                
                int iconSize = size / 3;
                int centerX = x + size / 2;
                int centerY = y + size / 2;
                
                // Draw X lines
                g2d.drawLine(centerX - iconSize/2, centerY - iconSize/2, 
                           centerX + iconSize/2, centerY + iconSize/2);
                g2d.drawLine(centerX + iconSize/2, centerY - iconSize/2, 
                           centerX - iconSize/2, centerY + iconSize/2);
            }
        };
        
        closeButton.addActionListener(_ -> {
            SoundManager.getInstance().playSound(SoundManager.SOUND_BUTTON_CLICK);
            settingsFrame.dispose();
        });
        
        // Create a panel to hold the header and close button
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(isDarkMode ? 
                ThemeManager.DARK_BG_SECONDARY : ThemeManager.LIGHT_BG_SECONDARY);
        headerPanel.setPreferredSize(new Dimension(250, 35));
        
        // Settings header
        JMenuItem headerItem = new JMenuItem("Game Settings");
        headerItem.setEnabled(false);
        headerItem.setFont(new Font("Segoe UI", Font.BOLD, 12));
        headerItem.setForeground(isDarkMode ? ThemeManager.DARK_TEXT_SECONDARY : ThemeManager.LIGHT_TEXT_SECONDARY);
        headerItem.setBorder(null);
        
        // Add header and close button to the panel
        headerPanel.add(headerItem, BorderLayout.WEST);
        headerPanel.add(closeButton, BorderLayout.EAST);
        
        // Create a wrapper menu item to hold the header panel
        JMenuItem headerWrapper = new JMenuItem() {
            @Override
            protected void paintComponent(Graphics g) {
                // Don't paint the default menu item background
            }
        };
        headerWrapper.setLayout(new BorderLayout());
        headerWrapper.add(headerPanel);
        headerWrapper.setPreferredSize(new Dimension(250, 35));
        headerWrapper.setEnabled(false);
        
        settingsPanel.add(headerWrapper);
        settingsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        
        // Stylish Music On/Off toggle button with animations
        JMenuItem musicToggle = new JMenuItem() {
            private Timer animationTimer;
            private float animationPhase = 0.0f;
            private float glowIntensity = 0.0f;
            private float toggleAnimation = 0.0f;
            private boolean animatingToggle = false;
            
            {
                // Initialize animation timer
                animationTimer = new Timer(30, _ -> {
                    animationPhase += 0.08f;
                    glowIntensity += 0.06f;
                    
                    if (animatingToggle) {
                        toggleAnimation += 0.12f;
                        if (toggleAnimation >= 1.0f) {
                            toggleAnimation = 1.0f;
                            animatingToggle = false;
                        }
                    }
                    repaint();
                });
                animationTimer.start();
            }
            
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                
                int width = getWidth();
                int height = getHeight();
                
                // Background with subtle gradient
                Color bgColor1 = isDarkMode ? ThemeManager.DARK_BG_SECONDARY : ThemeManager.LIGHT_BG_SECONDARY;
                Color bgColor2 = bgColor1.darker();
                
                if (getModel().isArmed()) {
                    bgColor1 = bgColor1.brighter();
                    bgColor2 = bgColor2.brighter();
                }
                
                GradientPaint bgGradient = new GradientPaint(0, 0, bgColor1, 0, height, bgColor2);
                g2d.setPaint(bgGradient);
                g2d.fillRect(0, 0, width, height);
                
                // "Music" label on the left
                String labelText = "Music";
                Font labelFont = new Font("Segoe UI", Font.BOLD, 14);
                g2d.setFont(labelFont);
                FontMetrics fm = g2d.getFontMetrics();
                
                int labelX = 15;
                int labelY = (height + fm.getAscent()) / 2 - 2;
                
                // Label text shadow
                g2d.setColor(new Color(0, 0, 0, 100));
                g2d.drawString(labelText, labelX + 1, labelY + 1);
                
                // Main label text
                g2d.setColor(isDarkMode ? ThemeManager.DARK_TEXT_PRIMARY : ThemeManager.LIGHT_TEXT_PRIMARY);
                g2d.drawString(labelText, labelX, labelY);
                
                // Music icon (note symbol) next to text
                int iconX = labelX + fm.stringWidth(labelText) + 10;
                int iconY = height / 2 - 8;
                drawMusicIcon(g2d, iconX, iconY);
                
                // Toggle switch area - moved to the right
                int switchWidth = 60;
                int switchHeight = 26;
                int switchX = width - switchWidth - 15; // Position from right side
                int switchY = (height - switchHeight) / 2;
                
                // Switch track
                Color trackColor = isMusicEnabled ? 
                    new Color(34, 197, 94, 180) : new Color(239, 68, 68, 180);
                
                // Add glow effect
                if (isMusicEnabled) {
                    float glowAlpha = 0.4f + 0.2f * (float) Math.sin(glowIntensity);
                    for (int i = 8; i > 0; i--) {
                        Color glowColor = new Color(34, 197, 94, (int)(glowAlpha * 255 / i));
                        g2d.setColor(glowColor);
                        g2d.fillRoundRect(switchX - i, switchY - i, switchWidth + 2*i, switchHeight + 2*i, 
                                        switchHeight + 2*i, switchHeight + 2*i);
                    }
                }
                
                g2d.setColor(trackColor);
                g2d.fillRoundRect(switchX, switchY, switchWidth, switchHeight, switchHeight, switchHeight);
                
                // Switch handle
                int handleSize = 22;
                int handleY = switchY + 2;
                
                // Calculate handle position with animation
                int handleOffX = isMusicEnabled ? switchWidth - handleSize - 2 : 2;
                if (animatingToggle) {
                    float progress = 1.0f - (float) Math.pow(1.0f - toggleAnimation, 3); // Ease out cubic
                    int startX = isMusicEnabled ? 2 : switchWidth - handleSize - 2;
                    int endX = isMusicEnabled ? switchWidth - handleSize - 2 : 2;
                    handleOffX = (int) (startX + (endX - startX) * progress);
                }
                
                int handleX = switchX + handleOffX;
                
                // Handle shadow
                g2d.setColor(new Color(0, 0, 0, 50));
                g2d.fillOval(handleX + 2, handleY + 2, handleSize, handleSize);
                
                // Handle gradient
                Color handleColor1 = Color.WHITE;
                Color handleColor2 = new Color(240, 240, 240);
                GradientPaint handleGradient = new GradientPaint(
                    handleX, handleY, handleColor1,
                    handleX, handleY + handleSize, handleColor2
                );
                g2d.setPaint(handleGradient);
                g2d.fillOval(handleX, handleY, handleSize, handleSize);
                
                // Handle border
                g2d.setColor(new Color(200, 200, 200));
                g2d.setStroke(new BasicStroke(1f));
                g2d.drawOval(handleX, handleY, handleSize, handleSize);
                
                // Handle inner highlight
                g2d.setColor(new Color(255, 255, 255, 150));
                g2d.fillOval(handleX + 3, handleY + 3, 6, 6);
                
                // Status text with style
                String statusText = isMusicEnabled ? "ON" : "OFF";
                Font statusFont = new Font("Segoe UI", Font.BOLD, 14);
                g2d.setFont(statusFont);
                FontMetrics statusFm = g2d.getFontMetrics();
                
                int textX = switchX + switchWidth + 15;
                int textY = (height + statusFm.getAscent()) / 2 - 2;
                
                // Text shadow
                g2d.setColor(new Color(0, 0, 0, 100));
                g2d.drawString(statusText, textX + 1, textY + 1);
                
                // Main text with color based on state
                Color textColor = isMusicEnabled ? 
                    new Color(34, 197, 94) : new Color(239, 68, 68);
                g2d.setColor(textColor);
                g2d.drawString(statusText, textX, textY);
                
                // Add pulsing effect when music is on
                if (isMusicEnabled) {
                    float pulse = 0.7f + 0.3f * (float) Math.sin(animationPhase * 2);
                    g2d.setColor(new Color(34, 197, 94, (int)(pulse * 50)));
                    g2d.fillRoundRect(0, 0, width, height, 8, 8);
                }
            }
            
            private void drawMusicIcon(Graphics2D g2d, int x, int y) {
                g2d.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                
                Color iconColor = isMusicEnabled ? 
                    new Color(34, 197, 94) : new Color(239, 68, 68);
                g2d.setColor(iconColor);
                
                // Note stem
                g2d.drawLine(x + 10, y, x + 10, y + 12);
                
                // Note head
                g2d.fillOval(x + 6, y + 10, 6, 4);
                
                // Musical note flag
                int[] flagX = {x + 10, x + 16, x + 14, x + 10};
                int[] flagY = {y, y + 2, y + 6, y + 4};
                g2d.fillPolygon(flagX, flagY, 4);
                
                // Add sparkle effect when music is on
                if (isMusicEnabled) {
                    float sparkle = (float) Math.sin(animationPhase * 3);
                    if (sparkle > 0.5f) {
                        g2d.setColor(new Color(255, 255, 255, (int)(sparkle * 200)));
                        g2d.fillOval(x + 12, y - 2, 3, 3);
                        g2d.fillOval(x + 4, y + 6, 2, 2);
                    }
                }
            }
        };
        
        musicToggle.setPreferredSize(new Dimension(200, 50));
        musicToggle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        musicToggle.addActionListener(_ -> {
            SoundManager.getInstance().playSound(SoundManager.SOUND_BUTTON_CLICK);
            isMusicEnabled = !isMusicEnabled;
            
            // Actually control the background music through SoundManager
            SoundManager soundManager = SoundManager.getInstance();
            soundManager.setMusicEnabled(isMusicEnabled);
            if (isMusicEnabled) {
                soundManager.startBackgroundMusic();
            } else {
                soundManager.stopBackgroundMusic();
            }
            
            musicToggle.putClientProperty("animatingToggle", true);
            // Start toggle animation by setting the field through reflection or a custom method
            try {
                java.lang.reflect.Field animatingField = musicToggle.getClass().getDeclaredField("animatingToggle");
                animatingField.setAccessible(true);
                animatingField.setBoolean(musicToggle, true);
                
                java.lang.reflect.Field toggleAnimField = musicToggle.getClass().getDeclaredField("toggleAnimation");
                toggleAnimField.setAccessible(true);
                toggleAnimField.setFloat(musicToggle, 0.0f);
            } catch (Exception ex) {
                // Fallback - just repaint
            }
            
            System.out.println("Music " + (isMusicEnabled ? "enabled" : "disabled"));
            musicToggle.repaint();
        });
        settingsPanel.add(musicToggle);
        settingsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        
        // Stylish Sound/Mute toggle button with animations
        JMenuItem soundToggle = new JMenuItem() {
            private Timer animationTimer;
            private float animationPhase = 0.0f;
            private float glowIntensity = 0.0f;
            private float toggleAnimation = 0.0f;
            private boolean animatingToggle = false;
            
            {
                // Initialize animation timer
                animationTimer = new Timer(30, _ -> {
                    animationPhase += 0.08f;
                    glowIntensity += 0.06f;
                    
                    if (animatingToggle) {
                        toggleAnimation += 0.12f;
                        if (toggleAnimation >= 1.0f) {
                            toggleAnimation = 1.0f;
                            animatingToggle = false;
                        }
                    }
                    repaint();
                });
                animationTimer.start();
            }
            
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                
                int width = getWidth();
                int height = getHeight();
                
                // Background with subtle gradient
                Color bgColor1 = isDarkMode ? ThemeManager.DARK_BG_SECONDARY : ThemeManager.LIGHT_BG_SECONDARY;
                Color bgColor2 = bgColor1.darker();
                
                if (getModel().isArmed()) {
                    bgColor1 = bgColor1.brighter();
                    bgColor2 = bgColor2.brighter();
                }
                
                GradientPaint bgGradient = new GradientPaint(0, 0, bgColor1, 0, height, bgColor2);
                g2d.setPaint(bgGradient);
                g2d.fillRect(0, 0, width, height);
                
                // "Sound Effects" label on the left
                String labelText = "Sound Effects";
                Font labelFont = new Font("Segoe UI", Font.BOLD, 14);
                g2d.setFont(labelFont);
                FontMetrics fm = g2d.getFontMetrics();
                
                int labelX = 15;
                int labelY = (height + fm.getAscent()) / 2 - 2;
                
                // Label text shadow
                g2d.setColor(new Color(0, 0, 0, 100));
                g2d.drawString(labelText, labelX + 1, labelY + 1);
                
                // Main label text
                g2d.setColor(isDarkMode ? ThemeManager.DARK_TEXT_PRIMARY : ThemeManager.LIGHT_TEXT_PRIMARY);
                g2d.drawString(labelText, labelX, labelY);
                
                // Sound icon (speaker symbol) next to text
                int iconX = labelX + fm.stringWidth(labelText) + 10;
                int iconY = height / 2 - 8;
                drawSoundIcon(g2d, iconX, iconY);
                
                // Toggle switch area - moved to the right
                int switchWidth = 60;
                int switchHeight = 26;
                int switchX = width - switchWidth - 15; // Position from right side
                int switchY = (height - switchHeight) / 2;
                
                // Switch track
                boolean soundEnabled = SoundManager.getInstance().isSoundEnabled();
                Color trackColor = soundEnabled ? 
                    new Color(34, 197, 94, 180) : new Color(239, 68, 68, 180);
                
                // Add glow effect
                if (soundEnabled) {
                    float glowAlpha = 0.4f + 0.2f * (float) Math.sin(glowIntensity);
                    for (int i = 8; i > 0; i--) {
                        Color glowColor = new Color(34, 197, 94, (int)(glowAlpha * 255 / i));
                        g2d.setColor(glowColor);
                        g2d.fillRoundRect(switchX - i, switchY - i, switchWidth + 2*i, switchHeight + 2*i, 
                                        switchHeight + 2*i, switchHeight + 2*i);
                    }
                }
                
                g2d.setColor(trackColor);
                g2d.fillRoundRect(switchX, switchY, switchWidth, switchHeight, switchHeight, switchHeight);
                
                // Switch handle
                int handleSize = 22;
                int handleY = switchY + 2;
                
                // Calculate handle position with animation
                int handleOffX = soundEnabled ? switchWidth - handleSize - 2 : 2;
                if (animatingToggle) {
                    float progress = 1.0f - (float) Math.pow(1.0f - toggleAnimation, 3); // Ease out cubic
                    int startX = soundEnabled ? 2 : switchWidth - handleSize - 2;
                    int endX = soundEnabled ? switchWidth - handleSize - 2 : 2;
                    handleOffX = (int) (startX + (endX - startX) * progress);
                }
                
                int handleX = switchX + handleOffX;
                
                // Handle shadow
                g2d.setColor(new Color(0, 0, 0, 50));
                g2d.fillOval(handleX + 2, handleY + 2, handleSize, handleSize);
                
                // Handle gradient
                Color handleColor1 = Color.WHITE;
                Color handleColor2 = new Color(240, 240, 240);
                GradientPaint handleGradient = new GradientPaint(
                    handleX, handleY, handleColor1,
                    handleX, handleY + handleSize, handleColor2
                );
                g2d.setPaint(handleGradient);
                g2d.fillOval(handleX, handleY, handleSize, handleSize);
                
                // Handle border
                g2d.setColor(new Color(200, 200, 200));
                g2d.setStroke(new BasicStroke(1f));
                g2d.drawOval(handleX, handleY, handleSize, handleSize);
                
                // Handle inner highlight
                g2d.setColor(new Color(255, 255, 255, 150));
                g2d.fillOval(handleX + 3, handleY + 3, 6, 6);
                
                // Status text with style
                String statusText = soundEnabled ? "ON" : "OFF";
                Font statusFont = new Font("Segoe UI", Font.BOLD, 14);
                g2d.setFont(statusFont);
                FontMetrics statusFm = g2d.getFontMetrics();
                
                int textX = switchX + switchWidth + 15;
                int textY = (height + statusFm.getAscent()) / 2 - 2;
                
                // Text shadow
                g2d.setColor(new Color(0, 0, 0, 100));
                g2d.drawString(statusText, textX + 1, textY + 1);
                
                // Main text with color based on state
                Color textColor = soundEnabled ? 
                    new Color(34, 197, 94) : new Color(239, 68, 68);
                g2d.setColor(textColor);
                g2d.drawString(statusText, textX, textY);
                
                // Add pulsing effect when sound is on
                if (soundEnabled) {
                    float pulse = 0.7f + 0.3f * (float) Math.sin(animationPhase * 2);
                    g2d.setColor(new Color(34, 197, 94, (int)(pulse * 50)));
                    int width2 = getWidth();
                    int height2 = getHeight();
                    g2d.fillRoundRect(0, 0, width2, height2, 8, 8);
                }
            }
            
            private void drawSoundIcon(Graphics2D g2d, int x, int y) {
                g2d.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                
                boolean soundEnabled = SoundManager.getInstance().isSoundEnabled();
                Color iconColor = soundEnabled ? 
                    new Color(34, 197, 94) : new Color(239, 68, 68);
                g2d.setColor(iconColor);
                
                // Speaker cone
                int[] speakerX = {x, x + 6, x + 6, x};
                int[] speakerY = {y + 4, y + 2, y + 10, y + 8};
                g2d.fillPolygon(speakerX, speakerY, 4);
                
                // Speaker base
                g2d.fillRect(x + 6, y + 4, 4, 4);
                
                if (soundEnabled) {
                    // Sound waves
                    g2d.drawArc(x + 12, y + 2, 8, 8, -30, 60);
                    g2d.drawArc(x + 14, y + 4, 6, 4, -20, 40);
                    
                    // Add sparkle effect when sound is on
                    float sparkle = (float) Math.sin(animationPhase * 3);
                    if (sparkle > 0.5f) {
                        g2d.setColor(new Color(255, 255, 255, (int)(sparkle * 200)));
                        g2d.fillOval(x + 16, y + 1, 2, 2);
                        g2d.fillOval(x + 6, y + 9, 2, 2);
                    }
                } else {
                    // Mute X symbol
                    g2d.setColor(new Color(239, 68, 68));
                    g2d.drawLine(x + 12, y + 2, x + 18, y + 8);
                    g2d.drawLine(x + 18, y + 2, x + 12, y + 8);
                }
            }
        };
        
        soundToggle.setPreferredSize(new Dimension(220, 50));
        soundToggle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        soundToggle.addActionListener(_ -> {
            SoundManager.getInstance().playSound(SoundManager.SOUND_BUTTON_CLICK);
            
            // Toggle sound effects through SoundManager
            SoundManager soundManager = SoundManager.getInstance();
            boolean newSoundState = !soundManager.isSoundEnabled();
            soundManager.setSoundEnabled(newSoundState);
            
            soundToggle.putClientProperty("animatingToggle", true);
            // Start toggle animation by setting the field through reflection or a custom method
            try {
                java.lang.reflect.Field animatingField = soundToggle.getClass().getDeclaredField("animatingToggle");
                animatingField.setAccessible(true);
                animatingField.setBoolean(soundToggle, true);
                
                java.lang.reflect.Field toggleAnimField = soundToggle.getClass().getDeclaredField("toggleAnimation");
                toggleAnimField.setAccessible(true);
                toggleAnimField.setFloat(soundToggle, 0.0f);
            } catch (Exception ex) {
                // Fallback - just repaint
            }
            
            System.out.println("Sound effects " + (newSoundState ? "enabled" : "disabled"));
            soundToggle.repaint();
        });        settingsPanel.add(soundToggle);
        settingsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        
        // Theme Selection Button with Popup Menu
        JButton themeButton = new JButton("Themes ▼");
        themeButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        themeButton.setBackground(isDarkMode ? ThemeManager.DARK_BG_SECONDARY : ThemeManager.LIGHT_BG_SECONDARY);
        themeButton.setForeground(isDarkMode ? ThemeManager.DARK_TEXT_PRIMARY : ThemeManager.LIGHT_TEXT_PRIMARY);
        themeButton.setPreferredSize(new Dimension(200, 35));
        themeButton.setBorder(BorderFactory.createRaisedBevelBorder());
        
        // Create popup menu for themes
        JPopupMenu themePopup = new JPopupMenu();
        themePopup.setBackground(isDarkMode ? ThemeManager.DARK_BG_SECONDARY : ThemeManager.LIGHT_BG_SECONDARY);
        
        // Create theme menu items with custom rendering
        String[] themes = {"Dark", "Light", "Neon", "Synthwave"};
        String[] themeKeys = {"dark", "light", "neon", "synthwave"};
        Color[] themeColors = {
            ThemeManager.DARK_ACCENT_PRIMARY,
            ThemeManager.LIGHT_ACCENT_PRIMARY,
            ThemeManager.NEON_ACCENT_PRIMARY,
            ThemeManager.SYNTHWAVE_ACCENT_PRIMARY
        };
        
        for (int i = 0; i < themes.length; i++) {
            final String themeName = themes[i];
            final String themeKey = themeKeys[i];
            final Color themeColor = themeColors[i];
            
            JMenuItem themeItem = new JMenuItem(themeName) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    
                    if (getModel().isArmed()) {
                        g2d.setColor(themeColor.darker());
                    } else {
                        g2d.setColor(isDarkMode ?
                                ThemeManager.DARK_BG_SECONDARY : ThemeManager.LIGHT_BG_SECONDARY);
                    }
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                    
                    // Draw theme color indicator
                    g2d.setColor(themeColor);
                    g2d.fillRoundRect(10, getHeight()/2-5, 10, 10, 5, 5);
                    
                    // Draw check mark if this is the current theme
                    if (themeMode.equals(themeKey)) {
                        g2d.setColor(themeColor);
                        g2d.setStroke(new BasicStroke(2f));
                        g2d.drawLine(getWidth()-20, getHeight()/2-5, getWidth()-15, getHeight()/2+5);
                        g2d.drawLine(getWidth()-15, getHeight()/2+5, getWidth()-5, getHeight()/2-10);
                    }
                    
                    // Draw text
                    FontMetrics fm = g2d.getFontMetrics();
                    g2d.setColor(isDarkMode ? ThemeManager.DARK_TEXT_PRIMARY : ThemeManager.LIGHT_TEXT_PRIMARY);
                    g2d.drawString(themeName, 30, (getHeight() + fm.getAscent())/2 - 2);
                }
            };
            
            themeItem.setPreferredSize(new Dimension(150, 30));
            themeItem.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            
            // Add action listener to change the theme
            themeItem.addActionListener(_ -> {
                SoundManager.getInstance().playSound(SoundManager.SOUND_BUTTON_CLICK);
                themeMode = themeKey;
                isDarkMode = !themeKey.equals("light"); // All themes except light are dark mode variants
                updateTheme();
                settingsFrame.dispose(); // Close settings menu after theme change
            });
            
            themePopup.add(themeItem);
        }
        
        // Add action listener to show the popup menu
        themeButton.addActionListener(_ -> {
            SoundManager.getInstance().playSound(SoundManager.SOUND_BUTTON_CLICK);
            themePopup.show(themeButton, 0, themeButton.getHeight());
        });
        
        settingsPanel.add(themeButton);
        settingsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        
        // Controls button
        JButton controlsButton = new JButton("Controls");
        controlsButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        controlsButton.setBackground(isDarkMode ? ThemeManager.DARK_BG_SECONDARY : ThemeManager.LIGHT_BG_SECONDARY);
        controlsButton.setForeground(isDarkMode ? ThemeManager.DARK_TEXT_PRIMARY : ThemeManager.LIGHT_TEXT_PRIMARY);
        controlsButton.setPreferredSize(new Dimension(200, 35));
        controlsButton.setBorder(BorderFactory.createRaisedBevelBorder());
        controlsButton.addActionListener(_ -> {
            SoundManager.getInstance().playSound(SoundManager.SOUND_BUTTON_CLICK);
            showControls();
        });
        
        settingsPanel.add(controlsButton);
        settingsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        
        // Current theme info
        JMenuItem currentThemeInfo = new JMenuItem("Current Theme: " + themeMode.substring(0, 1).toUpperCase() + themeMode.substring(1));
        currentThemeInfo.setEnabled(false);
        currentThemeInfo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        currentThemeInfo.setForeground(isDarkMode ? ThemeManager.DARK_TEXT_SECONDARY : ThemeManager.LIGHT_TEXT_SECONDARY);
        settingsPanel.add(currentThemeInfo);
        
        // Set up the frame and show it
        settingsFrame.add(settingsPanel);
        settingsFrame.pack();
        
        // Position the frame relative to the source button
        Point buttonLocation = sourceButton.getLocationOnScreen();
        settingsFrame.setLocation(buttonLocation.x, buttonLocation.y + sourceButton.getHeight());
        
        settingsFrame.setVisible(true);
    }
    
    /**
     * Shows the game controls information dialog
     */
    private void showControls() {
        JOptionPane.showMessageDialog(this,
                "Game Controls:\n\n" +
                        "PvC Mode:\n" +
                        "R = Rock\n" +
                        "P = Paper\n" +
                        "S = Scissors\n\n" +
                        "PvP Mode:\n" +
                        "Player 1:\n" +
                        "A = Rock\n" +
                        "S = Paper\n" +
                        "D = Scissors\n\n" +
                        "Player 2:\n" +
                        "J = Rock\n" +
                        "K = Paper\n" +
                        "L = Scissors",
                "Game Controls",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    /**
     * Initialize background music through SoundManager
     */
    private void initializeBackgroundMusic() {
        SoundManager soundManager = SoundManager.getInstance();
        soundManager.setMusicEnabled(isMusicEnabled);
        if (isMusicEnabled) {
            soundManager.startBackgroundMusic();
        }
    }
    
    /**
     * Play homepage start sound when entering the homepage
     */
    private void playHomepageStartSound() {
        SoundManager soundManager = SoundManager.getInstance();
        soundManager.playSound(SoundManager.SOUND_HOMEPAGE_START);
    }
}