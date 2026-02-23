import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Game Manager for Rock Paper Scissors
 * Handles the game logic and integrates AI for computer moves
 */
public class GameManager {
    // AI difficulty levels
    public enum AIDifficulty {
        EASY,       // Mostly random with minimal pattern recognition
        MEDIUM,     // Balance between random and pattern recognition
        HARD        // Advanced pattern recognition with adaptive strategies
    }
    
    private final GameAI gameAI;
    private final Random random = new Random();
    private AIDifficulty difficulty = AIDifficulty.MEDIUM; // Default difficulty
    
    // Tracking statistics for pattern analysis
    private final List<String> playerMoveHistory = new ArrayList<>();
    private final Map<String, Integer> moveFrequency = new HashMap<>();
    
    // Strategy weights for different difficulty levels
    private final double[] randomStrategyWeight = {0.8, 0.4, 0.15}; // Easy, Medium, Hard
    private final double[] counterStrategyWeight = {0.2, 0.6, 0.85}; // Easy, Medium, Hard
    
    // For tracking player patterns
    private int rockCount = 0;
    private int paperCount = 0;
    private int scissorsCount = 0;
    private String lastPlayerMove = null;
    private String lastComputerMove = null;
    private int repeatMoveCount = 0;
    
    public GameManager() {
        this.gameAI = new GameAI();
        initializeFrequencyMap();
        // Set initial difficulty
        gameAI.setDifficultyLevel(difficulty.name());
    }
    
    private void initializeFrequencyMap() {
        moveFrequency.put("Rock", 0);
        moveFrequency.put("Paper", 0);
        moveFrequency.put("Scissors", 0);
    }
    
    /**
     * Sets the AI difficulty level
     * @param difficulty The AI difficulty level
     */
    public void setDifficulty(AIDifficulty difficulty) {
        this.difficulty = difficulty;
        // Update the GameAI's difficulty level to match
        gameAI.setDifficultyLevel(difficulty.name());
    }
    
    /**
     * Gets the current AI difficulty level
     * @return The current difficulty level
     */
    public AIDifficulty getDifficulty() {
        return difficulty;
    }
    
    /**
     * Records a player's move for analysis
     * @param move The player's move ("Rock", "Paper", or "Scissors")
     */
    public void recordPlayerMove(String move) {
        // Update the AI with the player's move
        gameAI.recordPlayerMove(move);
        
        // Update our own tracking data
        playerMoveHistory.add(move);
        moveFrequency.put(move, moveFrequency.get(move) + 1);
        
        // Update counts for simple frequency analysis
        if (move.equals("Rock")) rockCount++;
        else if (move.equals("Paper")) paperCount++;
        else if (move.equals("Scissors")) scissorsCount++;
        
        // Track repeated moves
        if (move.equals(lastPlayerMove)) {
            repeatMoveCount++;
        } else {
            repeatMoveCount = 0;
        }
        
        lastPlayerMove = move;
    }
    
    /**
     * Generates a computer move based on the AI difficulty and player history
     * @return The computer's move ("Rock", "Paper", or "Scissors")
     */
    public String getComputerMove() {
        // Use the GameAI to get a move based on the difficulty setting
        // This will automatically handle win percentages
        return gameAI.predictNextMove();
    }
    
    /**
     * Predicts the player's next move based on history analysis
     * @return Predicted next move
     */
    private String predictNextPlayerMove() {
        // For the first few moves, when we don't have enough history,
        // use frequency-based prediction
        if (playerMoveHistory.size() < 3) {
            return predictBasedOnFrequency();
        }
        
        // First, check for simple patterns in HARD mode
        if (difficulty == AIDifficulty.HARD) {
            // If player repeats moves, predict they'll continue
            if (repeatMoveCount >= 2) {
                return lastPlayerMove;
            }
            
            // Check if player tends to rotate in a fixed pattern
            if (playerMoveHistory.size() >= 5) {
                String potentialPattern = detectSimplePattern();
                if (potentialPattern != null) {
                    return potentialPattern;
                }
            }
        }
        
        // Use AI's pattern recognition as the primary prediction method
        return gameAI.predictNextMove();
    }
    
    /**
     * Detects if player is following a simple pattern like "Rock, Paper, Scissors, Rock, Paper, ..."
     * @return Next predicted move based on pattern, or null if no pattern detected
     */
    private String detectSimplePattern() {
        int size = playerMoveHistory.size();
        
        // Check for 2-move patterns
        if (size >= 4 && 
            playerMoveHistory.get(size-2).equals(playerMoveHistory.get(size-4)) && 
            playerMoveHistory.get(size-1).equals(playerMoveHistory.get(size-3))) {
            return playerMoveHistory.get(size-2);
        }
        
        // Check for 3-move patterns
        if (size >= 6 && 
            playerMoveHistory.get(size-3).equals(playerMoveHistory.get(size-6)) && 
            playerMoveHistory.get(size-2).equals(playerMoveHistory.get(size-5)) && 
            playerMoveHistory.get(size-1).equals(playerMoveHistory.get(size-4))) {
            return playerMoveHistory.get(size-3);
        }
        
        return null;
    }
    
    /**
     * Predicts next move based on frequency of previous moves
     * @return Most frequently used move
     */
    private String predictBasedOnFrequency() {
        if (rockCount > paperCount && rockCount > scissorsCount) {
            return "Rock";
        } else if (paperCount > rockCount && paperCount > scissorsCount) {
            return "Paper";
        } else if (scissorsCount > rockCount && scissorsCount > paperCount) {
            return "Scissors";
        } else {
            // If tied or no history, return a random prediction
            String[] moves = {"Rock", "Paper", "Scissors"};
            return moves[random.nextInt(moves.length)];
        }
    }
    
    /**
     * Gets the counter move to beat the predicted player move
     * @param predictedMove The predicted player move
     * @return The move that beats the predicted move
     */
    private String getCounterMove(String predictedMove) {
        return switch (predictedMove) {
            case "Rock" -> "Paper";
            case "Paper" -> "Scissors";
            case "Scissors" -> "Rock";
            default -> {
                String[] moves = {"Rock", "Paper", "Scissors"};
                yield moves[random.nextInt(moves.length)];
            }
        };
    }
    
    /**
     * Resets the game state and history
     */
    public void reset() {
        playerMoveHistory.clear();
        initializeFrequencyMap();
        rockCount = 0;
        paperCount = 0;
        scissorsCount = 0;
        lastPlayerMove = null;
        lastComputerMove = null;
        repeatMoveCount = 0;
        gameAI.reset();
    }
}

