import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class for managing game sounds
 */
public class SoundManager {
    private static final Logger LOGGER = Logger.getLogger(SoundManager.class.getName());
    private static SoundManager instance;
    private final Map<String, Clip> soundCache;
    private final ConfigManager config;
    private boolean soundEnabled;
    private boolean musicEnabled;
    private Clip backgroundMusicClip;
    private float musicVolume = 0.7f; // Default volume for background music
    
    // Sound names constants
    public static final String SOUND_BUTTON_CLICK = "button.wav";
    public static final String SOUND_ROCK = "rock.wav";
    public static final String SOUND_PAPER = "paper.wav";
    public static final String SOUND_SCISSORS = "scissors.wav";
    public static final String SOUND_WIN = "win.wav";
    public static final String SOUND_LOSE = "lose.wav";
    public static final String SOUND_PLAYER_ROUND_WIN = "p round win.wav";
    public static final String SOUND_COMPUTER_ROUND_WIN = "c round win.wav";
    public static final String SOUND_DRAW = "round draw.wav";
    public static final String SOUND_MATCH_DRAW = "draw.wav";
    public static final String SOUND_CHEATING = "cheating.wav";
    public static final String SOUND_GAME_OVER = "game_over.wav";
    public static final String SOUND_COUNTDOWN = "countdown.wav";
    public static final String SOUND_HOMEPAGE_START = "homepage start.wav";
    public static final String SOUND_LOADING = "loading.wav";
    public static final String SOUND_KEY_PRESS = "key.wav";
    public static final String BACKGROUND_MUSIC = "background.wav";
    
    /**
     * Private constructor to enforce singleton pattern
     */
    private SoundManager() {
        soundCache = new HashMap<>();
        config = ConfigManager.getInstance();
        soundEnabled = config.getBoolean("enable_sound", true);
        musicEnabled = config.getBoolean("enable_music", true);
        
        // Preload common sounds
        preloadSound(SOUND_BUTTON_CLICK);
        preloadSound(SOUND_COUNTDOWN);
        preloadSound(SOUND_WIN);
        preloadSound(SOUND_LOSE);
        preloadSound(SOUND_DRAW);
        preloadSound(SOUND_MATCH_DRAW);
        preloadSound(SOUND_CHEATING);
        
        // Initialize background music
        if (musicEnabled) {
            initializeBackgroundMusic();
        }
    }
    
    /**
     * Get the singleton instance
     * @return SoundManager instance
     */
    public static synchronized SoundManager getInstance() {
        if (instance == null) {
            instance = new SoundManager();
        }
        return instance;
    }
    
    /**
     * Preload a sound into cache for faster playback
     * @param soundName The sound file name
     */
    public void preloadSound(String soundName) {
        if (!soundEnabled) return;
        
        try {
            Clip clip = loadClip(soundName);
            soundCache.put(soundName, clip);
            LOGGER.fine("Preloaded sound: " + soundName);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to preload sound: " + soundName, e);
        }
    }
    
    /**
     * Play a sound
     * @param soundName The sound file name
     */
    public void playSound(String soundName) {
        if (!soundEnabled) return;
        
        try {
            Clip clip;
            if (soundCache.containsKey(soundName)) {
                clip = soundCache.get(soundName);
                if (clip.isRunning()) {
                    clip.stop();
                }
                clip.setFramePosition(0);
            } else {
                clip = loadClip(soundName);
            }
            
            clip.start();
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to play sound: " + soundName, e);
        }
    }
    
    /**
     * Load an audio clip from file
     * @param soundName The sound file name
     * @return AudioClip object
     * @throws IOException If file cannot be read
     * @throws UnsupportedAudioFileException If audio format is not supported
     * @throws LineUnavailableException If audio line is unavailable
     */
    private Clip loadClip(String soundName) throws IOException, UnsupportedAudioFileException, LineUnavailableException {
        String soundPath = config.getSoundsPath() + soundName;
        File soundFile = new File(soundPath);
        
        // Check if file exists
        if (!soundFile.exists()) {
            LOGGER.warning("Sound file not found: " + soundPath);
            throw new IOException("Sound file not found: " + soundPath);
        }
        
        // Load and return clip
        AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);
        Clip clip = AudioSystem.getClip();
        clip.open(audioIn);
        return clip;
    }
    
    /**
     * Enable or disable sounds
     * @param enabled true to enable sounds, false to disable
     */
    public void setSoundEnabled(boolean enabled) {
        this.soundEnabled = enabled;
        
        // Stop all playing sounds if disabled
        if (!enabled) {
            for (Clip clip : soundCache.values()) {
                if (clip.isRunning()) {
                    clip.stop();
                }
            }
        }
    }
    
    /**
     * Check if sound is enabled
     * @return true if sound is enabled
     */
    public boolean isSoundEnabled() {
        return soundEnabled;
    }
    
    /**
     * Initialize background music
     */
    private void initializeBackgroundMusic() {
        try {
            backgroundMusicClip = loadClip(BACKGROUND_MUSIC);
            if (backgroundMusicClip != null) {
                // Set volume for background music
                setMusicVolume(musicVolume);
                LOGGER.info("Background music initialized successfully");
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to initialize background music", e);
            backgroundMusicClip = null;
        }
    }
    
    /**
     * Start playing background music
     */
    public void startBackgroundMusic() {
        if (!musicEnabled || backgroundMusicClip == null) return;
        
        try {
            if (!backgroundMusicClip.isRunning()) {
                backgroundMusicClip.setFramePosition(0);
                backgroundMusicClip.loop(Clip.LOOP_CONTINUOUSLY);
                LOGGER.info("Background music started");
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to start background music", e);
        }
    }
    
    /**
     * Stop background music
     */
    public void stopBackgroundMusic() {
        if (backgroundMusicClip != null && backgroundMusicClip.isRunning()) {
            backgroundMusicClip.stop();
            LOGGER.info("Background music stopped");
        }
    }
    
    /**
     * Pause background music
     */
    public void pauseBackgroundMusic() {
        if (backgroundMusicClip != null && backgroundMusicClip.isRunning()) {
            backgroundMusicClip.stop();
            LOGGER.info("Background music paused");
        }
    }
    
    /**
     * Resume background music
     */
    public void resumeBackgroundMusic() {
        if (musicEnabled && backgroundMusicClip != null && !backgroundMusicClip.isRunning()) {
            backgroundMusicClip.loop(Clip.LOOP_CONTINUOUSLY);
            LOGGER.info("Background music resumed");
        }
    }
    
    /**
     * Set background music volume
     * @param volume Volume level (0.0 to 1.0)
     */
    public void setMusicVolume(float volume) {
        this.musicVolume = Math.max(0.0f, Math.min(1.0f, volume));
        
        if (backgroundMusicClip != null) {
            try {
                FloatControl volumeControl = (FloatControl) backgroundMusicClip.getControl(FloatControl.Type.MASTER_GAIN);
                float dB = (float) (Math.log(this.musicVolume) / Math.log(10.0) * 20.0);
                volumeControl.setValue(dB);
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Failed to set music volume", e);
            }
        }
    }
    
    /**
     * Get current music volume
     * @return Volume level (0.0 to 1.0)
     */
    public float getMusicVolume() {
        return musicVolume;
    }
    
    /**
     * Enable or disable background music
     * @param enabled true to enable music, false to disable
     */
    public void setMusicEnabled(boolean enabled) {
        this.musicEnabled = enabled;
        
        if (!enabled) {
            stopBackgroundMusic();
        } else {
            if (backgroundMusicClip == null) {
                initializeBackgroundMusic();
            }
            if (backgroundMusicClip != null) {
                startBackgroundMusic();
            }
        }
    }
    
    /**
     * Check if background music is enabled
     * @return true if music is enabled
     */
    public boolean isMusicEnabled() {
        return musicEnabled;
    }
    
    /**
     * Check if background music is currently playing
     * @return true if music is playing
     */
    public boolean isMusicPlaying() {
        return backgroundMusicClip != null && backgroundMusicClip.isRunning();
    }
    
    /**
     * Clean up resources
     */
    public void cleanup() {
        // Stop and cleanup background music
        if (backgroundMusicClip != null) {
            if (backgroundMusicClip.isRunning()) {
                backgroundMusicClip.stop();
            }
            backgroundMusicClip.close();
            backgroundMusicClip = null;
        }
        
        // Cleanup cached sounds
        for (Clip clip : soundCache.values()) {
            if (clip.isRunning()) {
                clip.stop();
            }
            clip.close();
        }
        soundCache.clear();
    }
} 