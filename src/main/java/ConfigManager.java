import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.File;

/**
 * Utility class for loading and managing game configurations
 */
public class ConfigManager {
    private static final Logger LOGGER = Logger.getLogger(ConfigManager.class.getName());
    private static final String CONFIG_FILE = "config.properties";
    private static ConfigManager instance;
    private final Properties properties;
    
    /**
     * Private constructor to enforce singleton pattern
     */
    private ConfigManager() {
        properties = new Properties();
        loadConfig();
    }
    
    /**
     * Get the singleton instance
     * @return ConfigManager instance
     */
    public static synchronized ConfigManager getInstance() {
        if (instance == null) {
            instance = new ConfigManager();
        }
        return instance;
    }
    
    /**
     * Load configuration from properties file
     */
    private void loadConfig() {
        try {
            // Try to load from classpath
            InputStream input = getClass().getResourceAsStream("/" + CONFIG_FILE);
            if (input == null) {
                // Try to load from direct path as fallback
                File configFile = new File("src/main/resources/" + CONFIG_FILE);
                if (configFile.exists()) {
                    input = new FileInputStream(configFile);
                }
            }
            
            if (input != null) {
                properties.load(input);
                input.close();
                LOGGER.info("Configuration loaded successfully");
            } else {
                LOGGER.warning("Could not find configuration file. Using defaults.");
                loadDefaults();
            }
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Could not load configuration file. Using defaults.", e);
            loadDefaults();
        }
    }
    
    /**
     * Load default configuration values
     */
    private void loadDefaults() {
        // Game settings
        properties.setProperty("default_total_rounds", "3");
        properties.setProperty("default_mode", "PVC");
        properties.setProperty("enable_sound", "true");
        properties.setProperty("show_animations", "true");
        
        // UI settings
        properties.setProperty("default_theme", "DARK");
        properties.setProperty("button_animation_speed", "0.1");
        properties.setProperty("enable_particle_effects", "true");
        
        // AI settings
        properties.setProperty("default_ai_difficulty", "MEDIUM");
        properties.setProperty("pattern_length", "3");
        properties.setProperty("random_factor", "0.2");
        properties.setProperty("decay_factor", "0.9");
        
        // Paths
        properties.setProperty("resources_path", "resource/");
        properties.setProperty("images_path", "resource/image/");
        properties.setProperty("sounds_path", "src/main/resources/sound/");
        
        // Debug
        properties.setProperty("debug_mode", "true");
        properties.setProperty("log_level", "INFO");
    }
    
    /**
     * Get a string property value
     * @param key The property key
     * @param defaultValue Default value if property not found
     * @return Property value or default
     */
    public String getString(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
    
    /**
     * Get an integer property value
     * @param key The property key
     * @param defaultValue Default value if property not found
     * @return Property value or default
     */
    public int getInt(String key, int defaultValue) {
        try {
            return Integer.parseInt(properties.getProperty(key));
        } catch (NumberFormatException | NullPointerException e) {
            return defaultValue;
        }
    }
    
    /**
     * Get a double property value
     * @param key The property key
     * @param defaultValue Default value if property not found
     * @return Property value or default
     */
    public double getDouble(String key, double defaultValue) {
        try {
            return Double.parseDouble(properties.getProperty(key));
        } catch (NumberFormatException | NullPointerException e) {
            return defaultValue;
        }
    }
    
    /**
     * Get a boolean property value
     * @param key The property key
     * @param defaultValue Default value if property not found
     * @return Property value or default
     */
    public boolean getBoolean(String key, boolean defaultValue) {
        String value = properties.getProperty(key);
        if (value == null) return defaultValue;
        return Boolean.parseBoolean(value);
    }
    
    /**
     * Get the default AI difficulty
     * @return Default AI difficulty
     */
    public GameManager.AIDifficulty getDefaultAIDifficulty() {
        String difficulty = getString("default_ai_difficulty", "MEDIUM").toUpperCase();
        try {
            return GameManager.AIDifficulty.valueOf(difficulty);
        } catch (IllegalArgumentException e) {
            LOGGER.warning("Invalid AI difficulty in config. Using MEDIUM.");
            return GameManager.AIDifficulty.MEDIUM;
        }
    }
    
    /**
     * Check if debug mode is enabled
     * @return true if debug mode is enabled
     */
    public boolean isDebugMode() {
        return getBoolean("debug_mode", false);
    }
    
    /**
     * Get the resources path
     * @return Resources path
     */
    public String getResourcePath() {
        return getString("resources_path", "resource/");
    }
    
    /**
     * Get the images path
     * @return Images path
     */
    public String getImagesPath() {
        return getString("images_path", "resource/image/");
    }
    
    /**
     * Get the sounds path
     * @return Sounds path
     */
    public String getSoundsPath() {
        return getString("sounds_path", "resource/sounds/");
    }
} 