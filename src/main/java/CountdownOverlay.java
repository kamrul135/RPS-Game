import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.util.Random;

/**
 * A stylish circular countdown overlay that displays 3, 2, 1 countdown
 * with advanced visual effects and animations.
 */
public class CountdownOverlay extends JPanel {
    private int currentCount = 3;
    private float scale = 0.1f;
    private float alpha = 0.0f;
    private float rotation = 0.0f;
    private Timer animationTimer;
    private Runnable onComplete;
    private boolean isRunning = false;
    private int particleCount = 40;
    private Particle[] particles;
    private Random random = new Random();
    
    // Colors for the gradient background
    private Color gradientStartColor = new Color(255, 105, 180); // Hot pink
    private Color gradientEndColor = new Color(0, 255, 255);    // Cyan
    
    // Premium color themes
    private static final Color[][] PREMIUM_THEMES = {
        // Gold & Royal Blue theme
        {new Color(255, 215, 0), new Color(0, 35, 102)},
        // Diamond & Sapphire theme
        {new Color(185, 242, 255), new Color(15, 82, 186)},
        // Ruby & Emerald theme
        {new Color(220, 20, 60), new Color(0, 158, 96)},
        // Neon Pink & Electric Blue
        {new Color(255, 0, 255), new Color(0, 191, 255)},
        // Platinum & Amethyst
        {new Color(229, 228, 226), new Color(153, 102, 204)},
        // Amber & Teal
        {new Color(255, 191, 0), new Color(0, 128, 128)},
        // Crimson & Royal Purple
        {new Color(220, 20, 60), new Color(102, 51, 153)}
    };
    
    // Particle class for additional visual effects
    private class Particle {
        float x, y;
        float speedX, speedY;
        float size;
        float alpha;
        Color color;
        
        Particle() {
            reset();
        }
        
        void reset() {
            // Start particles from the center
            x = getWidth() / 2.0f;
            y = getHeight() / 2.0f;
            
            // Random direction and speed
            float angle = random.nextFloat() * (float) (Math.PI * 2);
            float speed = 1.0f + random.nextFloat() * 5.0f;
            speedX = (float) Math.cos(angle) * speed;
            speedY = (float) Math.sin(angle) * speed;
            
            // Random size and initial alpha
            size = 2.0f + random.nextFloat() * 10.0f;
            alpha = 0.7f + random.nextFloat() * 0.3f;
            
            // Choose a color that's a blend between our gradient colors
            float blend = random.nextFloat();
            int r = (int) (gradientStartColor.getRed() * blend + gradientEndColor.getRed() * (1-blend));
            int g = (int) (gradientStartColor.getGreen() * blend + gradientEndColor.getGreen() * (1-blend));
            int b = (int) (gradientStartColor.getBlue() * blend + gradientEndColor.getBlue() * (1-blend));
            color = new Color(r, g, b);
        }
        
        void update() {
            x += speedX;
            y += speedY;
            
            // Fade out
            alpha -= 0.01f;
            
            // Shrink
            size -= 0.1f;
            
            // Reset if faded out or too small
            if (alpha <= 0 || size <= 0) {
                reset();
            }
        }
        
        void draw(Graphics2D g2d) {
            if (alpha <= 0) return;
            
            g2d.setColor(new Color(
                color.getRed(), 
                color.getGreen(), 
                color.getBlue(), 
                (int)(255 * alpha)
            ));
            
            g2d.fill(new Ellipse2D.Float(x - size/2, y - size/2, size, size));
        }
    }
    
    /**
     * Create a new countdown overlay
     */
    public CountdownOverlay() {
        setOpaque(false);
        setLayout(new BorderLayout());
        
        // Initialize particles
        particles = new Particle[particleCount];
        for (int i = 0; i < particleCount; i++) {
            particles[i] = new Particle();
        }
        
        // Set a random premium theme
        setRandomPremiumTheme();
    }
    
    /**
     * Sets a random premium color theme
     */
    public void setRandomPremiumTheme() {
        int themeIndex = random.nextInt(PREMIUM_THEMES.length);
        setGradientColors(PREMIUM_THEMES[themeIndex][0], PREMIUM_THEMES[themeIndex][1]);
    }
    
    /**
     * Start the countdown animation
     * @param onComplete Runnable to execute when animation completes
     */
    public void startCountdown(Runnable onComplete) {
        this.onComplete = onComplete;
        this.currentCount = 3;
        this.scale = 0.1f;
        this.alpha = 0.0f;
        this.rotation = 0.0f;
        this.isRunning = true;
        
        // Reset all particles
        for (Particle p : particles) {
            p.reset();
        }
        
        if (animationTimer != null && animationTimer.isRunning()) {
            animationTimer.stop();
        }
        
        // Play sound for countdown start
        SoundManager.getInstance().playSound(SoundManager.SOUND_COUNTDOWN);
        
        animationTimer = new Timer(16, new ActionListener() { // ~60fps for smoother animation
            private int ticks = 0;
            private final int TICKS_PER_NUMBER = 60; // ~1 second per number at 60fps
            
            @Override
            public void actionPerformed(ActionEvent e) {
                ticks++;
                
                // Update all particles
                for (Particle p : particles) {
                    p.update();
                }
                
                // First half of animation: scale up and fade in
                if (ticks < TICKS_PER_NUMBER / 2) {
                    scale = Math.min(1.0f, scale + 0.05f);
                    alpha = Math.min(1.0f, alpha + 0.05f);
                    rotation += 1.0f; // Add rotation animation
                } 
                // Second half of animation: keep scale, prepare for next number
                else if (ticks < TICKS_PER_NUMBER) {
                    // Keep scale at 1.0, but continue rotation slightly
                    rotation += 0.5f;
                } 
                // Time to switch to next number
                else {
                    currentCount--;
                    ticks = 0;
                    scale = 0.1f;
                    alpha = 0.0f;
                    rotation = 0.0f;
                    
                    // For each new number, set a new random theme for variety
                    setRandomPremiumTheme();
                    
                    // Reset particles with new energetic burst
                    for (Particle p : particles) {
                        p.reset();
                    }
                    
                    // Play sound for next number
                    if (currentCount > 0) {
                        SoundManager.getInstance().playSound(SoundManager.SOUND_COUNTDOWN);
                    }
                    
                    // End of countdown - show "GO" when currentCount reaches 0
                    if (currentCount < 0) {
                        isRunning = false;
                        animationTimer.stop();
                        setVisible(false);
                        
                        // Execute callback when complete
                        if (onComplete != null) {
                            onComplete.run();
                        }
                        return;
                    }
                }
                
                // Trigger repaint for animation
                repaint();
            }
        });
        
        setVisible(true);
        animationTimer.start();
    }
    
    /**
     * Stop the countdown animation
     */
    public void stopCountdown() {
        if (animationTimer != null && animationTimer.isRunning()) {
            animationTimer.stop();
        }
        isRunning = false;
        setVisible(false);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        if (!isRunning) {
            return;
        }
        
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        // Create semi-transparent dark backdrop
        g2d.setColor(new Color(0, 0, 0, 180));
        g2d.fillRect(0, 0, getWidth(), getHeight());
        
        // Calculate center and size of the countdown circle
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        int circleSize = Math.min(getWidth(), getHeight()) / 3;
        
        // Apply scale animation
        int currentSize = (int) (circleSize * scale);
        
        // Draw particles first (behind the main circle)
        for (Particle p : particles) {
            p.draw(g2d);
        }
        
        // Save the current transform to restore later
        AffineTransform originalTransform = g2d.getTransform();
        
        // Apply rotation for the main circle
        g2d.rotate(Math.toRadians(rotation), centerX, centerY);
        
        // Create circular gradient background with glow effect
        // Use special colors for "GO!" phase
        Color currentStartColor = gradientStartColor;
        Color currentEndColor = gradientEndColor;
        
        if (currentCount == 0) {
            // Use bright green and gold for "GO!" - more exciting colors
            currentStartColor = new Color(0, 255, 100);   // Bright green
            currentEndColor = new Color(255, 215, 0);     // Gold
        }
        
        RadialGradientPaint gradient = new RadialGradientPaint(
            centerX, centerY, currentSize * 1.2f,
            new float[] { 0.0f, 0.7f, 1.0f },
            new Color[] { 
                new Color(
                    currentStartColor.getRed(),
                    currentStartColor.getGreen(),
                    currentStartColor.getBlue(),
                    (int)(255 * alpha)
                ),
                new Color(
                    currentEndColor.getRed(),
                    currentEndColor.getGreen(),
                    currentEndColor.getBlue(),
                    (int)(255 * alpha)
                ),
                new Color(
                    currentEndColor.getRed(),
                    currentEndColor.getGreen(),
                    currentEndColor.getBlue(),
                    0
                )
            }
        );
        
        g2d.setPaint(gradient);
        
        // Draw the outer glow
        Ellipse2D.Double outerGlow = new Ellipse2D.Double(
            centerX - currentSize * 0.6, 
            centerY - currentSize * 0.6,
            currentSize * 1.2, currentSize * 1.2
        );
        g2d.fill(outerGlow);
        
        // Draw the main circle
        Ellipse2D.Double circle = new Ellipse2D.Double(
            centerX - currentSize / 2.0, 
            centerY - currentSize / 2.0,
            currentSize, currentSize
        );
        
        // Create a more vibrant gradient for the main circle
        RadialGradientPaint circleGradient = new RadialGradientPaint(
            centerX, centerY, currentSize / 2.0f,
            new float[] { 0.0f, 1.0f },
            new Color[] { 
                new Color(
                    currentStartColor.getRed(),
                    currentStartColor.getGreen(),
                    currentStartColor.getBlue(),
                    (int)(255 * alpha)
                ),
                new Color(
                    currentEndColor.getRed(),
                    currentEndColor.getGreen(),
                    currentEndColor.getBlue(),
                    (int)(255 * alpha)
                )
            }
        );
        
        g2d.setPaint(circleGradient);
        g2d.fill(circle);
        
        // Add a subtle ring around the main circle
        g2d.setStroke(new BasicStroke(3.0f));
        g2d.setColor(new Color(255, 255, 255, (int)(100 * alpha)));
        g2d.draw(circle);
        
        // Restore original transform before drawing text
        g2d.setTransform(originalTransform);
        
        // Draw countdown number or "GO" text with 3D effect
        if (currentCount > 0 || currentCount == 0) {
            // Use a more premium font
            Font font = new Font("Arial", Font.BOLD, (int)(currentSize * 0.7));
            g2d.setFont(font);
            
            // Get the text to display
            String countText = currentCount > 0 ? String.valueOf(currentCount) : "GO!";
            
            // For "GO!" text, make it slightly smaller but more prominent
            if (currentCount == 0) {
                font = new Font("Arial", Font.BOLD, (int)(currentSize * 0.5));
                g2d.setFont(font);
            }
            
            // Get metrics for centering the text
            FontMetrics metrics = g2d.getFontMetrics(font);
            int textWidth = metrics.stringWidth(countText);
            int textHeight = metrics.getHeight();
            
            // Create a subtle 3D effect with multiple shadows
            for (int i = 5; i > 0; i--) {
                int shadowAlpha = (int)(50 * alpha / i);
                g2d.setColor(new Color(0, 0, 0, shadowAlpha));
                g2d.drawString(countText, 
                    centerX - textWidth / 2 + i, 
                    centerY + textHeight / 3 + i);
            }
            
            // Draw the main text with a gradient
            // Use different colors for "GO!" to make it more exciting
            Color startColor = currentCount == 0 ? 
                new Color(255, 255, 100, (int)(255 * alpha)) :  // Bright yellow for GO!
                new Color(255, 255, 255, (int)(255 * alpha));   // White for numbers
            Color endColor = currentCount == 0 ? 
                new Color(255, 200, 0, (int)(255 * alpha)) :    // Gold for GO!
                new Color(220, 220, 220, (int)(255 * alpha));   // Light gray for numbers
            
            GradientPaint textGradient = new GradientPaint(
                centerX - textWidth / 2, centerY - textHeight / 2,
                startColor,
                centerX + textWidth / 2, centerY + textHeight / 2,
                endColor
            );
            
            g2d.setPaint(textGradient);
            g2d.drawString(countText, 
                centerX - textWidth / 2, 
                centerY + textHeight / 3);
                
            // Add a subtle glow around the text
            // Make the glow more prominent for "GO!"
            int glowAlpha = currentCount == 0 ? (int)(80 * alpha) : (int)(40 * alpha);
            g2d.setColor(new Color(255, 255, 255, glowAlpha));
            g2d.setStroke(new BasicStroke(2.0f));
            TextLayout textLayout = new TextLayout(countText, font, g2d.getFontRenderContext());
            Shape outline = textLayout.getOutline(AffineTransform.getTranslateInstance(
                centerX - textWidth / 2, 
                centerY + textHeight / 3
            ));
            g2d.draw(outline);
        }
        
        // Draw orientation lines like in the reference image (if scale is large enough)
        if (scale > 0.7f) {
            int lineExtension = (int)(currentSize * 0.3);
            
            // Draw multiple decorative lines with varying opacity for a more premium look
            for (int i = 0; i < 4; i++) {
                float angle = (float) Math.toRadians(i * 45);
                int opacity = 120 - i * 20;
                
                g2d.setColor(new Color(255, 255, 255, (int)(opacity * alpha)));
                g2d.setStroke(new BasicStroke(1.5f));
                
                // Draw lines at various angles around the circle
                float cos = (float) Math.cos(angle);
                float sin = (float) Math.sin(angle);
                
                g2d.draw(new Line2D.Double(
                    centerX - cos * (currentSize/2 + lineExtension), 
                    centerY - sin * (currentSize/2 + lineExtension),
                    centerX + cos * (currentSize/2 + lineExtension), 
                    centerY + sin * (currentSize/2 + lineExtension)
                ));
            }
            
            // Add concentric rings for extra visual interest
            g2d.setColor(new Color(255, 255, 255, (int)(40 * alpha)));
            g2d.setStroke(new BasicStroke(1.0f));
            g2d.draw(new Ellipse2D.Double(
                centerX - currentSize * 0.7, 
                centerY - currentSize * 0.7,
                currentSize * 1.4, 
                currentSize * 1.4
            ));
            
            g2d.setColor(new Color(255, 255, 255, (int)(30 * alpha)));
            g2d.draw(new Ellipse2D.Double(
                centerX - currentSize * 0.9, 
                centerY - currentSize * 0.9,
                currentSize * 1.8, 
                currentSize * 1.8
            ));
        }
        
        g2d.dispose();
    }
    
    /**
     * Check if the countdown is currently running
     */
    public boolean isRunning() {
        return isRunning;
    }
    
    /**
     * Set the gradient colors for the countdown background
     */
    public void setGradientColors(Color startColor, Color endColor) {
        this.gradientStartColor = startColor;
        this.gradientEndColor = endColor;
    }
}