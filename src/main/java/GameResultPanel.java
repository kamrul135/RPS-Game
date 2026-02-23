import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Panel component for displaying game results.
 * This is not the main class - run RockPaperScissorsGame instead.
 */
public class GameResultPanel extends JPanel {
    private static final String GAME_OVER_TEXT = "GAME OVER";
    private final String resultText;
    private final Color resultColor;
    private final String resultEmoji;
    private final Color[] particleColors;
    private final java.util.List<Particle> particles;
    private float angle = 0;
    private final javax.swing.Timer animationTimer;  // Explicitly specify javax.swing.Timer
    private final int player1Score;
    private final int player2Score;
    private final boolean isPvCMode;
    private final Random random = new Random();

    private static class Particle {
        float x, y;
        float speed;
        float angle;
        Color color;
        float alpha = 1.0f;
        float size;

        Particle(float x, float y, Color color) {
            this.x = x;
            this.y = y;
            this.color = color;
            this.speed = 1 + (float)Math.random() * 3;
            this.angle = (float)(Math.random() * Math.PI * 2);
            this.size = 2 + (float)Math.random() * 4;
        }

        void update() {
            x += Math.cos(angle) * speed;
            y += Math.sin(angle) * speed;
            alpha *= 0.98f;
            size *= 0.98f;
        }
    }

    public GameResultPanel(String result, Color color, String emoji, int p1Score, int p2Score, boolean isPvC) {
        this.resultText = result;
        this.resultColor = color;
        this.resultEmoji = emoji;
        this.player1Score = p1Score;
        this.player2Score = p2Score;
        this.isPvCMode = isPvC;
        
        // Initialize particle colors based on result
        this.particleColors = new Color[]{
            color,
            color.brighter(),
            Color.WHITE,
            new Color(255, 215, 0) // Gold color for particles
        };
        
        particles = new ArrayList<>();
        setPreferredSize(new Dimension(400, 300));
        setOpaque(false);

        // Initialize animation timer with explicit Timer reference
        animationTimer = new Timer(16, e -> {
            angle += 0.05f;
            updateParticles();
            repaint();
        });
        animationTimer.start();
    }

    private void updateParticles() {
        // Add new particles
        if (particles.size() < 100) {
            for (int i = 0; i < 5; i++) {
                particles.add(new Particle(
                    getWidth() / 2 + (float)(Math.random() * 40 - 20),
                    getHeight() / 2 + (float)(Math.random() * 40 - 20),
                    particleColors[random.nextInt(particleColors.length)]
                ));
            }
        }

        // Update existing particles
        particles.removeIf(p -> p.alpha < 0.05f);
        particles.forEach(Particle::update);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw background with radial gradient
        Paint originalPaint = g2d.getPaint();
        RadialGradientPaint gradient = new RadialGradientPaint(
            getWidth() / 2f, getHeight() / 2f, getWidth() / 1.5f,
            new float[]{0.0f, 1.0f},
            new Color[]{
                new Color(0, 0, 0, 200),
                new Color(0, 0, 0, 230)
            }
        );
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        // Draw particles
        particles.forEach(p -> {
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, p.alpha));
            g2d.setColor(p.color);
            g2d.fill(new Ellipse2D.Float(p.x, p.y, p.size, p.size));
        });
        g2d.setComposite(AlphaComposite.SrcOver);

        // Draw "GAME OVER" text with 3D effect
        drawGameOverText(g2d);

        // Draw main result text with glow effect
        drawResultText(g2d);

        // Draw score panel
        drawScorePanel(g2d);

        // Draw emoji with pulsing effect
        drawEmoji(g2d);

        g2d.setPaint(originalPaint);
    }

    private void drawGameOverText(Graphics2D g2d) {
        Font gameOverFont = new Font("Arial", Font.BOLD, 36);
        g2d.setFont(gameOverFont);
        FontMetrics fm = g2d.getFontMetrics();
        int textX = (getWidth() - fm.stringWidth(GAME_OVER_TEXT)) / 2;
        int textY = 60;

        // Draw 3D shadow
        for (int i = 5; i > 0; i--) {
            g2d.setColor(new Color(0, 0, 0, 50));
            g2d.drawString(GAME_OVER_TEXT, textX + i, textY + i);
        }

        // Draw main text with gradient
        GradientPaint textGradient = new GradientPaint(
            textX, textY - fm.getAscent(),
            Color.WHITE,
            textX, textY,
            Color.LIGHT_GRAY
        );
        g2d.setPaint(textGradient);
        g2d.drawString(GAME_OVER_TEXT, textX, textY);
    }

    private void drawResultText(Graphics2D g2d) {
        Font resultFont = new Font("Arial", Font.BOLD, 48);
        g2d.setFont(resultFont);
        FontMetrics fm = g2d.getFontMetrics();
        int textX = (getWidth() - fm.stringWidth(resultText)) / 2;
        int textY = getHeight() / 2 + 20;

        // Draw outer glow
        float glowIntensity = 0.5f + 0.5f * (float)Math.sin(angle * 2);
        for (int i = 10; i > 0; i--) {
            float alpha = glowIntensity * (1 - (float)i/10);
            g2d.setColor(new Color(
                resultColor.getRed(),
                resultColor.getGreen(),
                resultColor.getBlue(),
                (int)(50 * alpha)
            ));
            g2d.drawString(resultText, textX - i, textY);
            g2d.drawString(resultText, textX + i, textY);
            g2d.drawString(resultText, textX, textY - i);
            g2d.drawString(resultText, textX, textY + i);
        }

        // Draw main text with metallic effect
        GradientPaint metallic = new GradientPaint(
            textX, textY - fm.getAscent(),
            resultColor.brighter(),
            textX, textY,
            resultColor
        );
        g2d.setPaint(metallic);
        g2d.drawString(resultText, textX, textY);
    }

    private void drawScorePanel(Graphics2D g2d) {
        int panelWidth = 200;
        int panelHeight = 60;
        int x = (getWidth() - panelWidth) / 2;
        int y = getHeight() - panelHeight - 20;

        // Draw score panel background
        g2d.setColor(new Color(0, 0, 0, 150));
        g2d.fillRoundRect(x, y, panelWidth, panelHeight, 15, 15);

        // Draw scores
        g2d.setFont(new Font("Arial", Font.BOLD, 20));
        g2d.setColor(Color.WHITE);
        String p1Text = isPvCMode ? "Player" : "P1";
        String p2Text = isPvCMode ? "CPU" : "P2";
        String scoreText = String.format("%s: %d | %s: %d", p1Text, player1Score, p2Text, player2Score);
        FontMetrics fm = g2d.getFontMetrics();
        g2d.drawString(scoreText, 
            (getWidth() - fm.stringWidth(scoreText)) / 2,
            y + panelHeight/2 + fm.getAscent()/2);
    }

    private void drawEmoji(Graphics2D g2d) {
        float scale = 1.0f + 0.1f * (float)Math.sin(angle * 3);
        
        if (isPvCMode) {
            // In PvC mode, load and display the two thinking emoji images side by side
            ImageIcon playerIcon = loadImageIcon("image/think_kid_question.png");
            ImageIcon cpuIcon = loadImageIcon("image/think cpu.png");
            
            if (playerIcon != null && cpuIcon != null) {
                AffineTransform original = g2d.getTransform();
                
                // Draw player thinking emoji on the left
                int playerWidth = playerIcon.getIconWidth();
                int playerHeight = playerIcon.getIconHeight();
                g2d.translate(getWidth()/2 - playerWidth - 20, 120);
                g2d.scale(scale * 0.5, scale * 0.5);  // Scale down if needed
                g2d.drawImage(playerIcon.getImage(), 0, 0, null);
                
                // Reset transform for CPU emoji
                g2d.setTransform(original);
                
                // Draw CPU thinking emoji on the right
                int cpuWidth = cpuIcon.getIconWidth();
                int cpuHeight = cpuIcon.getIconHeight();
                g2d.translate(getWidth()/2 + 20, 120);
                g2d.scale(scale * 0.5, scale * 0.5);  // Scale down if needed
                g2d.drawImage(cpuIcon.getImage(), 0, 0, null);
                
                g2d.setTransform(original);
            } else {
                // Fallback to original emoji if images couldn't be loaded
                drawOriginalEmoji(g2d, scale);
            }
        } else {
            // In PvP mode, use the original emoji drawing
            drawOriginalEmoji(g2d, scale);
        }
    }
    
    private void drawOriginalEmoji(Graphics2D g2d, float scale) {
        Font emojiFont = new Font("Segoe UI Emoji", Font.PLAIN, 72);
        g2d.setFont(emojiFont);
        FontMetrics fm = g2d.getFontMetrics();
        
        AffineTransform original = g2d.getTransform();
        g2d.translate(getWidth()/2, 120);
        g2d.scale(scale, scale);
        g2d.translate(-fm.stringWidth(resultEmoji)/2, 0);
        
        // Draw emoji shadow
        g2d.setColor(new Color(0, 0, 0, 50));
        g2d.drawString(resultEmoji, 2, 2);
        
        // Draw emoji
        g2d.setColor(Color.WHITE);
        g2d.drawString(resultEmoji, 0, 0);
        
        g2d.setTransform(original);
    }
    
    private ImageIcon loadImageIcon(String path) {
        try {
            java.net.URL imageURL = getClass().getClassLoader().getResource(path);
            if (imageURL != null) {
                return new ImageIcon(imageURL);
            }
        } catch (Exception e) {
            System.err.println("Error loading image: " + path);
        }
        return null;
    }

    public void stopAnimation() {
        animationTimer.stop();
    }

    // Add getters for accessing private fields if needed
    public String getResultText() {
        return resultText;
    }

    public int getPlayer1Score() {
        return player1Score;
    }

    public int getPlayer2Score() {
        return player2Score;
    }

    public boolean isPvCMode() {
        return isPvCMode;
    }
}
