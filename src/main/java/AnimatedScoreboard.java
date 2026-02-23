import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * An animated scoreboard component for the Rock Paper Scissors game
 * Features live score updates with smooth animations and visual effects
 */
public class AnimatedScoreboard extends JPanel {
    private int player1Score = 0;
    private int player2Score = 0;
    private String player1Name = "Player 1";
    private String player2Name = "Player 2";
    private boolean isPvCMode = true;
    
    // Animation variables
    private Timer animationTimer;
    private float glowIntensity = 0.0f;
    private float pulseScale = 1.0f;
    private float sparklePhase = 0.0f;
    private int lastPlayer1Score = 0;
    private int lastPlayer2Score = 0;
    private boolean player1ScoreChanged = false;
    private boolean player2ScoreChanged = false;
    private int scoreChangeAnimation = 0;
    
    // Visual constants
    private static final int ANIMATION_DURATION = 60; // frames
    private static final Color PLAYER1_COLOR = new Color(52, 152, 219); // Blue
    private static final Color PLAYER2_COLOR = new Color(40, 30, 93);  // Indigo #281E5D
    private static final Color NEUTRAL_COLOR = new Color(149, 165, 166); // Gray
    private static final Color GLOW_COLOR = new Color(241, 196, 15);     // Gold
    
    // Match timer properties
    private Timer matchTimer;
    private long elapsedTimeSeconds = 0;
    private boolean timerRunning = false;
    
    public AnimatedScoreboard() {
        initializeComponent();
        startAnimation();
    }
    
    private void initializeComponent() {
        setOpaque(false);
        setPreferredSize(new Dimension(140, 80)); // Adjusted height since we removed the timer
        setMinimumSize(new Dimension(120, 80));
        setMaximumSize(new Dimension(160, 80));
        setAlignmentX(Component.CENTER_ALIGNMENT);
    }
    
    private void startAnimation() {
        animationTimer = new Timer(50, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateAnimation();
                repaint();
            }
        });
        animationTimer.start();
    }
    
    private void updateAnimation() {
        // Update glow intensity
        glowIntensity += 0.1f;
        if (glowIntensity > Math.PI * 2) {
            glowIntensity = 0.0f;
        }
        
        // Update pulse scale
        pulseScale = 1.0f + 0.05f * (float) Math.sin(glowIntensity * 2);
        
        // Update sparkle phase
        sparklePhase += 0.15f;
        if (sparklePhase > Math.PI * 2) {
            sparklePhase = 0.0f;
        }
        
        // Handle score change animations
        if (scoreChangeAnimation > 0) {
            scoreChangeAnimation--;
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        
        // Enable antialiasing for smooth graphics
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        
        int width = getWidth();
        int height = getHeight();
        
        // Draw background with gradient
        drawBackground(g2d, width, height);
        
        // Draw connecting line between scores
        drawConnectingElements(g2d, width, height);
        
        // Draw player scores
        drawPlayerScore(g2d, width, height, true);  // Player 1 (left)
        drawPlayerScore(g2d, width, height, false); // Player 2 (right)
        
        // Draw sparkle effects
        drawSparkleEffects(g2d, width, height);
        
        g2d.dispose();
    }
    
    private void drawBackground(Graphics2D g2d, int width, int height) {
        // Main background - dark blue gradient like a football match scoreboard
        GradientPaint backgroundGradient = new GradientPaint(
            0, 0, new Color(10, 14, 40, 230),
            0, height, new Color(5, 8, 25, 250)
        );
        g2d.setPaint(backgroundGradient);
        g2d.fillRect(0, 0, width, height);
        
        // Create top black header bar with "SCORE" text
        int headerHeight = height / 6;
        int headerY = 2;
        
        // Draw header with slight curve and gradient effect
        GradientPaint headerGradient = new GradientPaint(
            0, headerY, new Color(0, 0, 0, 230),
            0, headerY + headerHeight, new Color(20, 20, 20, 250)
        );
        g2d.setPaint(headerGradient);
        
        // Draw rounded header bar
        g2d.fillRoundRect(width/12, headerY, width - width/6, headerHeight, 10, 10);
        
        // Draw "SCORE" text in the header
        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        g2d.setColor(new Color(255, 255, 255, 220));
        String scoreText = "SCORE";
        FontMetrics fm = g2d.getFontMetrics();
        g2d.drawString(scoreText, width/2 - fm.stringWidth(scoreText)/2, headerY + headerHeight/2 + fm.getAscent()/2 - 1);
        
        // Draw main score area
        int scoreY = headerY + headerHeight + 2;
        int scoreHeight = height / 3;
        
        // Draw left team banner (blue side)
        int[] leftXPoints = {width/12, width/3, width/3 - width/24, width/12 - width/24};
        int[] leftYPoints = {scoreY, scoreY, scoreY + scoreHeight, scoreY + scoreHeight};
        
        GradientPaint leftGradient = new GradientPaint(
            0, scoreY, new Color(30, 90, 170),
            0, scoreY + scoreHeight, new Color(20, 70, 150)
        );
        g2d.setPaint(leftGradient);
        g2d.fillPolygon(leftXPoints, leftYPoints, 4);
        
        // Draw right team banner (indigo side for computer)
        int[] rightXPoints = {width - width/12, width - width/3, width - width/3 + width/24, width - width/12 + width/24};
        int[] rightYPoints = {scoreY, scoreY, scoreY + scoreHeight, scoreY + scoreHeight};
        
        GradientPaint rightGradient = new GradientPaint(
            0, scoreY, new Color(40, 30, 93), // Indigo #281E5D (lighter shade)
            0, scoreY + scoreHeight, new Color(28, 20, 65) // Indigo #1C1441 (darker shade)
        );
        g2d.setPaint(rightGradient);
        g2d.fillPolygon(rightXPoints, rightYPoints, 4);
        
        // Draw center black score area - making it larger for bigger score display
        int[] centerXPoints = {width/3, width - width/3, width - width/3 + width/24, width/3 - width/24};
        int[] centerYPoints = {scoreY, scoreY, scoreY + scoreHeight, scoreY + scoreHeight};
        
        GradientPaint centerGradient = new GradientPaint(
            0, scoreY, new Color(30, 30, 30),
            0, scoreY + scoreHeight, new Color(10, 10, 10)
        );
        g2d.setPaint(centerGradient);
        g2d.fillPolygon(centerXPoints, centerYPoints, 4);
        
        // Add subtle border highlights for depth
        g2d.setColor(new Color(255, 255, 255, 30));
        g2d.setStroke(new BasicStroke(1.0f));
        
        // Header highlight
        g2d.drawRoundRect(width/12, headerY, width - width/6, headerHeight, 10, 10);
        
        // Banner highlights
        g2d.drawPolygon(leftXPoints, leftYPoints, 4);
        g2d.drawPolygon(centerXPoints, centerYPoints, 4);
        g2d.drawPolygon(rightXPoints, rightYPoints, 4);
        
        // Add light beams around the scoreboard for a dramatic effect
        drawLightBeams(g2d, width, height);
    }
    
    private void drawConnectingElements(Graphics2D g2d, int width, int height) {
        int centerX = width / 2;
        int headerHeight = height / 6;
        int scoreY = 2 + headerHeight + 2;
        int scoreHeight = height / 3;
        
        // Draw team names on banners
        drawTeamName(g2d, player1Name, width/6, scoreY, scoreHeight, true);
        drawTeamName(g2d, player2Name, width - width/6, scoreY, scoreHeight, false);
        
        // Draw score numbers in the center black area with separator
        String score1Text = String.valueOf(player1Score);
        String score2Text = String.valueOf(player2Score);
        
        // Use a bigger font for scores (increased from 18 to 30)
        g2d.setFont(new Font("Arial", Font.BOLD, 30));
        FontMetrics fm = g2d.getFontMetrics();
        
        // Score separator (dash or bullet)
        String separator = "-";
        int separatorWidth = fm.stringWidth(separator);
        
        // Calculate total width of scores and separator
        int score1Width = fm.stringWidth(score1Text);
        int score2Width = fm.stringWidth(score2Text);
        int totalWidth = score1Width + separatorWidth + score2Width;
        
        // Draw scores and separator centered in the black area
        int startX = centerX - totalWidth/2;
        int scoresY = scoreY + scoreHeight/2 + fm.getAscent()/2 - 1;
        
        // Apply glow effect for better visibility
        if (player1ScoreChanged || player2ScoreChanged) {
            // Draw glow behind scores if recently changed
            g2d.setColor(new Color(255, 255, 255, 40));
            g2d.fillOval(centerX - totalWidth/2 - 20, scoresY - fm.getAscent() - 5, totalWidth + 40, fm.getHeight() + 10);
        }
        
        // Draw score 1 (left side)
        g2d.setColor(new Color(255, 255, 255, 250));
        g2d.drawString(score1Text, startX, scoresY);
        
        // Draw separator
        g2d.drawString(separator, startX + score1Width, scoresY);
        
        // Draw score 2 (right side)
        g2d.drawString(score2Text, startX + score1Width + separatorWidth, scoresY);
    }
    
    private void drawTeamName(Graphics2D g2d, String name, int x, int scoreY, int scoreHeight, boolean isLeft) {
        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        FontMetrics fm = g2d.getFontMetrics();
        
        // Limit name length if too long
        String displayName = name;
        if (fm.stringWidth(name) > 70) {
            displayName = name.substring(0, 7) + "...";
        }
        
        int textWidth = fm.stringWidth(displayName);
        int textY = scoreY + scoreHeight/2 + fm.getAscent()/2 - 1;
        
        // Position text centered at x (adjusted for left or right side)
        int textX = isLeft ? x - textWidth/2 : x - textWidth/2;
        
        // Draw text with shadow for better visibility
        g2d.setColor(new Color(0, 0, 0, 100));
        g2d.drawString(displayName, textX + 1, textY + 1);
        
        // Draw main text in white
        g2d.setColor(new Color(255, 255, 255, 230));
        g2d.drawString(displayName, textX, textY);
    }
    
    private void drawTimer(Graphics2D g2d, int centerX, int y) {
        // Format elapsed time as MM:SS
        int minutes = (int)(elapsedTimeSeconds / 60);
        int seconds = (int)(elapsedTimeSeconds % 60);
        String timerText = String.format("%02d:%02d", minutes, seconds);
        
        g2d.setFont(new Font("Monospaced", Font.BOLD, 14));
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(timerText);
        
        // Draw with glow effect - make it brighter if the timer is running
        if (timerRunning) {
            g2d.setColor(new Color(220, 220, 220, 210));
            // Draw subtle glow behind the text for running timer
            g2d.setColor(new Color(255, 255, 255, 40));
            g2d.fillOval(centerX - textWidth/2 - 5, y - fm.getAscent() - 3, textWidth + 10, fm.getHeight() + 6);
        } else {
            g2d.setColor(new Color(180, 180, 180, 180));
        }
        
        g2d.drawString(timerText, centerX - textWidth/2, y);
    }
    
    private void drawPlayerScore(Graphics2D g2d, int width, int height, boolean isPlayer1) {
        String playerName = isPlayer1 ? player1Name : player2Name;
        Color playerColor = isPlayer1 ? PLAYER1_COLOR : PLAYER2_COLOR;
        boolean scoreChanged = isPlayer1 ? player1ScoreChanged : player2ScoreChanged;
        
        // Calculate header and score section dimensions for positioning
        int headerHeight = height / 6;
        int scoreY = 2 + headerHeight + 2;
        int scoreHeight = height / 3;
        
        // Calculate position for the team emblems (now directly below the score area)
        int logoY = scoreY + scoreHeight + 15; 
        int logoX = isPlayer1 ? width / 4 : (width * 3) / 4;
        
        // Apply pulse effect if score changed recently
        float currentPulseScale = pulseScale;
        if (scoreChanged && scoreChangeAnimation > 0) {
            float animationProgress = 1.0f - (float)scoreChangeAnimation / ANIMATION_DURATION;
            currentPulseScale += 0.3f * (float)Math.sin(animationProgress * Math.PI * 4) * (1.0f - animationProgress);
        }
        
        // Slightly smaller emblems for the football scoreboard style
        int circleRadius = (int)(14 * currentPulseScale);
        
        // Circle glow effect - more subtle for football scoreboard style
        if (scoreChanged && scoreChangeAnimation > 0) {
            for (int i = 4; i > 0; i--) {
                float alpha = 0.08f * (1.0f - (float)i / 4.0f);
                alpha *= 2.5f; // Intensify glow during score change
                g2d.setColor(new Color(playerColor.getRed(), playerColor.getGreen(), playerColor.getBlue(), (int)(alpha * 255)));
                int glowRadius = circleRadius + i * 2;
                g2d.fillOval(logoX - glowRadius, logoY - glowRadius, glowRadius * 2, glowRadius * 2);
            }
        }
        
        // Main circle - team emblem with metallic gradient for premium look
        Color lighterColor = isPlayer1 ? new Color(70, 140, 210) : new Color(60, 45, 130);
        Color darkerColor = isPlayer1 ? new Color(20, 60, 140) : new Color(28, 20, 65);
        
        // Create metallic gradient effect
        GradientPaint circleGradient = new GradientPaint(
            logoX - circleRadius, logoY - circleRadius, lighterColor,
            logoX + circleRadius, logoY + circleRadius, darkerColor
        );
        g2d.setPaint(circleGradient);
        g2d.fillOval(logoX - circleRadius, logoY - circleRadius, circleRadius * 2, circleRadius * 2);
        
        // Add metallic shine effect
        g2d.setColor(new Color(255, 255, 255, 100));
        g2d.fillArc(logoX - circleRadius, logoY - circleRadius, circleRadius * 2, circleRadius * 2, -30, 60);
        
        // Circle border - metallic rim
        g2d.setColor(new Color(255, 255, 255, 180));
        g2d.setStroke(new BasicStroke(1.2f));
        g2d.drawOval(logoX - circleRadius, logoY - circleRadius, circleRadius * 2, circleRadius * 2);
        
        // Draw team initial in the circle
        String initial = playerName.length() > 0 ? playerName.substring(0, 1).toUpperCase() : "?";
        g2d.setFont(new Font("Arial", Font.BOLD, (int)(13 * currentPulseScale)));
        FontMetrics initialFm = g2d.getFontMetrics();
        int initialWidth = initialFm.stringWidth(initial);
        
        // Draw initial with shadow for better visibility
        g2d.setColor(new Color(0, 0, 0, 80));
        g2d.drawString(initial, logoX - initialWidth/2 + 1, logoY + initialFm.getAscent()/2 - 1);
        
        g2d.setColor(new Color(255, 255, 255, 250));
        g2d.drawString(initial, logoX - initialWidth/2, logoY + initialFm.getAscent()/2 - 2);
    }
    
    private void drawSparkleEffects(Graphics2D g2d, int width, int height) {
        if (scoreChangeAnimation <= 0) return;
        
        g2d.setColor(Color.WHITE);
        
        // Determine which team score changed
        boolean p1Changed = player1ScoreChanged;
        boolean p2Changed = player2ScoreChanged;
        
        // Calculate positions
        int scoreY = 5;
        int scoreHeight = height / 3;
        
        // Create sparkles around the score area that just changed
        for (int i = 0; i < 8; i++) {
            float angle = (float)(i * Math.PI / 4 + sparklePhase);
            
            // Position sparkles around changed score in the red trapezoid
            if (p1Changed) {
                int centerX = width / 2 - 40;
                int sparkleX = centerX + (int)(Math.cos(angle) * 15);
                int sparkleY = scoreY + scoreHeight/2 + (int)(Math.sin(angle) * 10);
                
                float alpha = (float)scoreChangeAnimation / ANIMATION_DURATION;
                g2d.setColor(new Color(255, 255, 255, (int)(alpha * 200)));
                
                int sparkleSize = (i % 3) + 1;
                g2d.fillOval(sparkleX - sparkleSize, sparkleY - sparkleSize, sparkleSize * 2, sparkleSize * 2);
            }
            
            if (p2Changed) {
                int centerX = width / 2 + 40;
                int sparkleX = centerX + (int)(Math.cos(angle) * 15);
                int sparkleY = scoreY + scoreHeight/2 + (int)(Math.sin(angle) * 10);
                
                float alpha = (float)scoreChangeAnimation / ANIMATION_DURATION;
                g2d.setColor(new Color(255, 255, 255, (int)(alpha * 200)));
                
                int sparkleSize = (i % 3) + 1;
                g2d.fillOval(sparkleX - sparkleSize, sparkleY - sparkleSize, sparkleSize * 2, sparkleSize * 2);
            }
        }
    }
    
    /**
     * Updates the scores with animation
     */
    public void updateScores(int newPlayer1Score, int newPlayer2Score) {
        // Check for score changes
        player1ScoreChanged = (newPlayer1Score != lastPlayer1Score);
        player2ScoreChanged = (newPlayer2Score != lastPlayer2Score);
        
        if (player1ScoreChanged || player2ScoreChanged) {
            scoreChangeAnimation = ANIMATION_DURATION;
        }
        
        lastPlayer1Score = player1Score;
        lastPlayer2Score = player2Score;
        player1Score = newPlayer1Score;
        player2Score = newPlayer2Score;
        
        repaint();
    }
    
    /**
     * Updates player names
     */
    public void updatePlayerNames(String p1Name, String p2Name, boolean pvcMode) {
        this.player1Name = p1Name;
        this.player2Name = pvcMode ? "Computer" : p2Name;
        this.isPvCMode = pvcMode;
        repaint();
    }
    
    /**
     * Resets the scoreboard
     */
    public void reset() {
        player1Score = 0;
        player2Score = 0;
        lastPlayer1Score = 0;
        lastPlayer2Score = 0;
        player1ScoreChanged = false;
        player2ScoreChanged = false;
        scoreChangeAnimation = 0;
        stopTimer(); // Stop the timer when resetting
        elapsedTimeSeconds = 0; // Reset the timer
        repaint();
    }
    
    /**
     * Starts the match timer
     */
    public void startTimer() {
        if (matchTimer != null) {
            matchTimer.stop();
        }
        
        elapsedTimeSeconds = 0;
        timerRunning = true;
        
        matchTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                elapsedTimeSeconds++;
                repaint();
            }
        });
        matchTimer.start();
        repaint();
    }
    
    /**
     * Stops the match timer
     */
    public void stopTimer() {
        if (matchTimer != null) {
            matchTimer.stop();
        }
        timerRunning = false;
        repaint();
    }
    
    /**
     * Returns the current elapsed time in seconds
     */
    public long getElapsedTimeSeconds() {
        return elapsedTimeSeconds;
    }
    
    /**
     * Stops the animation timer when component is no longer needed
     */
    public void dispose() {
        if (animationTimer != null) {
            animationTimer.stop();
            animationTimer = null;
        }
        
        if (matchTimer != null) {
            matchTimer.stop();
            matchTimer = null;
        }
    }
    
    @Override
    public void removeNotify() {
        super.removeNotify();
        dispose();
    }
    
    private void drawLightBeams(Graphics2D g2d, int width, int height) {
        // Save original composite
        Composite originalComposite = g2d.getComposite();
        
        // Create light beams radiating from behind the scoreboard
        int numBeams = 8;
        float baseAlpha = 0.15f + (float)(Math.sin(glowIntensity) * 0.05f);
        
        for (int i = 0; i < numBeams; i++) {
            float angle = (float)(i * Math.PI / (numBeams/2) + glowIntensity * 0.5);
            int beamLength = width;
            
            int centerX = width / 2;
            int centerY = height / 3;
            
            int endX = centerX + (int)(Math.cos(angle) * beamLength);
            int endY = centerY + (int)(Math.sin(angle) * beamLength);
            
            // Adjust alpha based on angle for a more dynamic effect
            float alpha = baseAlpha * (0.5f + (float)Math.abs(Math.sin(angle)));
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            
            // Create gradient for the beam
            GradientPaint beamGradient = new GradientPaint(
                centerX, centerY, new Color(255, 255, 255, 100),
                endX, endY, new Color(255, 255, 255, 0)
            );
            g2d.setPaint(beamGradient);
            
            // Draw beam
            g2d.setStroke(new BasicStroke(10.0f + (float)(Math.sin(glowIntensity * 2) * 4.0f)));
            g2d.drawLine(centerX, centerY, endX, endY);
        }
        
        // Restore original composite
        g2d.setComposite(originalComposite);
    }
}
