import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

/**
 * Theme Manager for Rock Paper Scissors Game
 * Handles different visual themes and colors
 */
public class ThemeManager {
    // Custom divider colors storage
    private static Map<String, Color> customDividerColors = new HashMap<>();
    
    // Default divider color keys
    public static final String DIVIDER_PRIMARY = "divider_primary";
    public static final String DIVIDER_SECONDARY = "divider_secondary";
    public static final String DIVIDER_ACCENT = "divider_accent";
    public static final String DIVIDER_GLOW = "divider_glow";
    
    // Premium Dark theme colors - Luxurious Deep Space
    public static final Color DARK_BG_PRIMARY = new Color(8, 12, 20);          // Deep space black
    public static final Color DARK_BG_SECONDARY = new Color(15, 20, 32);       // Rich midnight blue
    public static final Color DARK_TEXT_PRIMARY = new Color(255, 255, 255);    // Pure white
    public static final Color DARK_TEXT_SECONDARY = new Color(185, 195, 210);  // Platinum silver
    public static final Color DARK_ACCENT_PRIMARY = new Color(100, 210, 255);  // Brilliant sky blue
    public static final Color DARK_ACCENT_SECONDARY = new Color(255, 120, 140); // Rose gold
    public static final Color DARK_PANEL_START = new Color(20, 28, 45);        // Deep charcoal
    public static final Color DARK_PANEL_END = new Color(8, 15, 25);           // Midnight black
    public static final Color DARK_TOP_GRADIENT_START = new Color(20, 28, 45); // Same as center panel - Deep charcoal
    public static final Color DARK_TOP_GRADIENT_END = new Color(8, 15, 25);    // Same as center panel - Midnight black
    public static final Color DARK_BOTTOM_GRADIENT_START = new Color(18, 25, 40); // Steel blue
    public static final Color DARK_BOTTOM_GRADIENT_END = new Color(8, 12, 20);    // Deep space
    
    // Premium borders and highlights - Enhanced inner borders
    public static final Color DARK_BORDER_PREMIUM = new Color(0, 191, 255);    // Deeper electric blue
    public static final Color DARK_BORDER_GOLD = new Color(255, 215, 0);       // Pure gold
    public static final Color DARK_INNER_GLOW = new Color(100, 200, 255, 120); // Stronger blue glow
    public static final Color DARK_INNER_BORDER = new Color(40, 50, 70);       // Subtle inner border
    public static final Color DARK_OUTER_BORDER = new Color(80, 100, 140);     // Enhanced outer border
    
    // Light theme colors
    public static final Color LIGHT_BG_PRIMARY = new Color(245, 245, 250);
    public static final Color LIGHT_BG_SECONDARY = new Color(230, 230, 240);
    public static final Color LIGHT_TEXT_PRIMARY = new Color(40, 40, 50);
    public static final Color LIGHT_TEXT_SECONDARY = new Color(90, 90, 100);
    public static final Color LIGHT_ACCENT_PRIMARY = new Color(65, 105, 225);
    public static final Color LIGHT_ACCENT_SECONDARY = new Color(255, 112, 77);
    public static final Color LIGHT_PANEL_START = new Color(240, 240, 245);
    public static final Color LIGHT_PANEL_END = new Color(225, 225, 235);
    public static final Color LIGHT_TOP_GRADIENT_START = new Color(240, 240, 245); // Same as center panel
    public static final Color LIGHT_TOP_GRADIENT_END = new Color(225, 225, 235);   // Same as center panel
    public static final Color LIGHT_BOTTOM_GRADIENT_START = new Color(220, 225, 235);
    public static final Color LIGHT_BOTTOM_GRADIENT_END = new Color(200, 210, 225);
    
    // Enhanced light theme borders
    public static final Color LIGHT_INNER_BORDER = new Color(200, 200, 210);
    public static final Color LIGHT_OUTER_BORDER = new Color(160, 160, 180);
    
    // Neon theme colors
    public static final Color NEON_BG_PRIMARY = new Color(10, 10, 15);
    public static final Color NEON_BG_SECONDARY = new Color(15, 15, 25);
    public static final Color NEON_TEXT_PRIMARY = new Color(0, 255, 255); // Cyan
    public static final Color NEON_TEXT_SECONDARY = new Color(255, 0, 255); // Magenta
    public static final Color NEON_ACCENT_PRIMARY = new Color(0, 255, 0); // Lime
    public static final Color NEON_ACCENT_SECONDARY = new Color(255, 0, 128); // Hot Pink
    public static final Color NEON_PANEL_START = new Color(15, 15, 30);
    public static final Color NEON_PANEL_END = new Color(5, 5, 15);
    public static final Color NEON_TOP_GRADIENT_START = new Color(15, 15, 30); // Same as center panel
    public static final Color NEON_TOP_GRADIENT_END = new Color(5, 5, 15);     // Same as center panel
    public static final Color NEON_BOTTOM_GRADIENT_START = new Color(0, 0, 30);
    public static final Color NEON_BOTTOM_GRADIENT_END = new Color(15, 0, 50);
    
    // Enhanced neon theme borders
    public static final Color NEON_INNER_BORDER = new Color(50, 0, 100);
    public static final Color NEON_OUTER_BORDER = new Color(0, 255, 255);
    
    // Synthwave theme colors (adding an additional stylish theme)
    public static final Color SYNTHWAVE_BG_PRIMARY = new Color(30, 10, 40);
    public static final Color SYNTHWAVE_BG_SECONDARY = new Color(50, 15, 65);
    public static final Color SYNTHWAVE_TEXT_PRIMARY = new Color(255, 128, 255); // Pink
    public static final Color SYNTHWAVE_TEXT_SECONDARY = new Color(128, 255, 255); // Light Blue
    public static final Color SYNTHWAVE_ACCENT_PRIMARY = new Color(238, 130, 238); // Violet
    public static final Color SYNTHWAVE_ACCENT_SECONDARY = new Color(23, 190, 187); // Teal
    public static final Color SYNTHWAVE_PANEL_START = new Color(40, 15, 60);
    public static final Color SYNTHWAVE_PANEL_END = new Color(20, 5, 30);
    public static final Color SYNTHWAVE_TOP_GRADIENT_START = new Color(40, 15, 60); // Same as center panel
    public static final Color SYNTHWAVE_TOP_GRADIENT_END = new Color(20, 5, 30);    // Same as center panel
    public static final Color SYNTHWAVE_BOTTOM_GRADIENT_START = new Color(20, 0, 60);
    public static final Color SYNTHWAVE_BOTTOM_GRADIENT_END = new Color(50, 0, 80);
    
    // Enhanced synthwave theme borders
    public static final Color SYNTHWAVE_INNER_BORDER = new Color(100, 50, 150);
    public static final Color SYNTHWAVE_OUTER_BORDER = new Color(238, 130, 238);
    
    // Rock, Paper, Scissors colors for different themes
    public static final Color ROCK_COLOR_DARK = new Color(110, 180, 210);
    public static final Color PAPER_COLOR_DARK = new Color(230, 160, 170);
    public static final Color SCISSORS_COLOR_DARK = new Color(130, 220, 130);
    
    public static final Color ROCK_COLOR_LIGHT = new Color(70, 130, 180);    // Steel blue
    public static final Color PAPER_COLOR_LIGHT = new Color(255, 105, 180);  // Hot pink
    public static final Color SCISSORS_COLOR_LIGHT = new Color(60, 179, 113); // Medium sea green
    
    public static final Color ROCK_COLOR_NEON = new Color(0, 191, 255);    // Deep Sky Blue
    public static final Color PAPER_COLOR_NEON = new Color(255, 0, 255);   // Magenta
    public static final Color SCISSORS_COLOR_NEON = new Color(0, 255, 127); // Spring Green
    
    public static final Color ROCK_COLOR_SYNTHWAVE = new Color(64, 224, 208);   // Turquoise
    public static final Color PAPER_COLOR_SYNTHWAVE = new Color(238, 130, 238); // Violet
    public static final Color SCISSORS_COLOR_SYNTHWAVE = new Color(255, 215, 0); // Gold
    
    /**
     * Get the text color for the current theme
     * @param themeName The current theme name
     * @return The primary text color for that theme
     */
    public static Color getTextColor(String themeName) {
        return switch (themeName) {
            case "dark" -> DARK_TEXT_PRIMARY;
            case "light" -> LIGHT_TEXT_PRIMARY;
            case "neon" -> NEON_TEXT_PRIMARY;
            case "synthwave" -> SYNTHWAVE_TEXT_PRIMARY;
            default -> DARK_TEXT_PRIMARY;
        };
    }
    
    /**
     * Get the accent color for the current theme
     * @param themeName The current theme name
     * @return The primary accent color for that theme
     */
    public static Color getAccentColor(String themeName) {
        return switch (themeName) {
            case "dark" -> DARK_ACCENT_PRIMARY;
            case "light" -> LIGHT_ACCENT_PRIMARY;
            case "neon" -> NEON_ACCENT_PRIMARY;
            case "synthwave" -> SYNTHWAVE_ACCENT_PRIMARY;
            default -> DARK_ACCENT_PRIMARY;
        };
    }
    
    /**
     * Get the rock button color for the current theme
     * @param themeName The current theme name
     * @return The rock color for that theme
     */
    public static Color getRockColor(String themeName) {
        return switch (themeName) {
            case "dark" -> ROCK_COLOR_DARK;
            case "light" -> ROCK_COLOR_LIGHT;
            case "neon" -> ROCK_COLOR_NEON;
            case "synthwave" -> ROCK_COLOR_SYNTHWAVE;
            default -> ROCK_COLOR_DARK;
        };
    }
    
    /**
     * Get the paper button color for the current theme
     * @param themeName The current theme name
     * @return The paper color for that theme
     */
    public static Color getPaperColor(String themeName) {
        return switch (themeName) {
            case "dark" -> PAPER_COLOR_DARK;
            case "light" -> PAPER_COLOR_LIGHT;
            case "neon" -> PAPER_COLOR_NEON;
            case "synthwave" -> PAPER_COLOR_SYNTHWAVE;
            default -> PAPER_COLOR_DARK;
        };
    }
    
    /**
     * Get the scissors button color for the current theme
     * @param themeName The current theme name
     * @return The scissors color for that theme
     */
    public static Color getScissorsColor(String themeName) {
        return switch (themeName) {
            case "dark" -> SCISSORS_COLOR_DARK;
            case "light" -> SCISSORS_COLOR_LIGHT;
            case "neon" -> SCISSORS_COLOR_NEON;
            case "synthwave" -> SCISSORS_COLOR_SYNTHWAVE;
            default -> SCISSORS_COLOR_DARK;
        };
    }
    
    /**
     * Get the inner border color for the current theme
     * @param themeName The current theme name
     * @return The inner border color for that theme
     */
    public static Color getInnerBorderColor(String themeName) {
        return switch (themeName) {
            case "dark" -> DARK_INNER_BORDER;
            case "light" -> LIGHT_INNER_BORDER;
            case "neon" -> NEON_INNER_BORDER;
            case "synthwave" -> SYNTHWAVE_INNER_BORDER;
            default -> DARK_INNER_BORDER;
        };
    }
    
    /**
     * Get the outer border color for the current theme
     * @param themeName The current theme name
     * @return The outer border color for that theme
     */
    public static Color getOuterBorderColor(String themeName) {
        return switch (themeName) {
            case "dark" -> DARK_OUTER_BORDER;
            case "light" -> LIGHT_OUTER_BORDER;
            case "neon" -> NEON_OUTER_BORDER;
            case "synthwave" -> SYNTHWAVE_OUTER_BORDER;
            default -> DARK_OUTER_BORDER;
        };
    }
    
    /**
     * Set a custom divider color for a specific key
     * @param key The key for the divider color
     * @param color The color to set
     */
    public static void setCustomDividerColor(String key, Color color) {
        customDividerColors.put(key, color);
    }
    
    /**
     * Get a custom divider color by key
     * @param key The key for the divider color
     * @return The color associated with the key, or null if not set
     */
    public static Color getCustomDividerColor(String key) {
        return customDividerColors.get(key);
    }
}