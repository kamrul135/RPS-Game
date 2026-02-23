import java.util.*;

public class GameAI {
    private final List<String> playerHistory = new ArrayList<>();
    private final Map<String, Integer> patternFrequency = new HashMap<>();
    private final Random random = new Random();
    private static final String[] MOVES = {"Rock", "Paper", "Scissors"};
    
    // Difficulty-based win rates
    private static final double EASY_AI_WIN_RATE = 0.35;   // AI wins 35%, player wins 65%
    private static final double MEDIUM_AI_WIN_RATE = 0.55; // AI wins 55%, player wins 45%
    private static final double HARD_AI_WIN_RATE = 0.75;   // AI wins 75%, player wins 25%
    
    private static final double RANDOM_FACTOR = 0.2; // 20% random moves to avoid being too predictable
    private static final int PATTERN_LENGTH = 3; // Length of patterns to analyze
    private static final double DECAY_FACTOR = 0.9; // Reduce influence of older patterns
    private final Map<String, Double> weightedPatternFrequency = new HashMap<>(); // Use weights for patterns
    
    // Current difficulty level (defaults to MEDIUM)
    private String difficultyLevel = "MEDIUM";

    public String predictNextMove() {
        if (playerHistory.size() < PATTERN_LENGTH) {
            return getStrategyBasedMove();
        }

        // Decide if we should make a random move
        if (random.nextDouble() < RANDOM_FACTOR) {
            return getStrategyBasedMove();
        }

        // Analyze recent pattern
        String recentPattern = getRecentPattern();
        String predictedPlayerMove = predictPlayerMoveWithWeights(recentPattern);

        // Return move based on strategy - this implements our win rate control
        return getStrategyBasedMove(predictedPlayerMove);
    }
    
    /**
     * Sets the difficulty level for the AI
     * @param difficultyLevel The difficulty level (EASY, MEDIUM, or HARD)
     */
    public void setDifficultyLevel(String difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
    }

    public void recordPlayerMove(String move) {
        playerHistory.add(move);
        if (playerHistory.size() >= PATTERN_LENGTH) {
            String pattern = getRecentPattern();
            weightedPatternFrequency.merge(pattern, 1.0, (oldValue, newValue) -> oldValue * DECAY_FACTOR + newValue);
        }
    }

    private String getRecentPattern() {
        StringBuilder pattern = new StringBuilder();
        for (int i = playerHistory.size() - PATTERN_LENGTH; i < playerHistory.size(); i++) {
            pattern.append(playerHistory.get(i).charAt(0)); // Using first letter of each move
        }
        return pattern.toString();
    }

    private String predictPlayerMoveWithWeights(String recentPattern) {
        // Find the most likely move after this pattern using weighted probabilities
        Map<String, Double> moveAfterPatternWeights = new HashMap<>();

        for (int i = 0; i < playerHistory.size() - PATTERN_LENGTH; i++) {
            StringBuilder currentPattern = new StringBuilder();
            for (int j = 0; j < PATTERN_LENGTH; j++) {
                currentPattern.append(playerHistory.get(i + j).charAt(0));
            }

            if (currentPattern.toString().equals(recentPattern) && i + PATTERN_LENGTH < playerHistory.size()) {
                String nextMove = playerHistory.get(i + PATTERN_LENGTH);
                moveAfterPatternWeights.merge(nextMove, weightedPatternFrequency.getOrDefault(currentPattern.toString(), 1.0), Double::sum);
            }
        }

        if (moveAfterPatternWeights.isEmpty()) {
            return MOVES[random.nextInt(MOVES.length)];
        }

        return Collections.max(moveAfterPatternWeights.entrySet(), Map.Entry.comparingByValue()).getKey();
    }
    
    /**
     * Get a move based on the AI difficulty level to enforce win percentages
     * @return A move designed to ensure player win percentages match difficulty levels
     */
    private String getStrategyBasedMove() {
        return getStrategyBasedMove(null);
    }
    
    /**
     * Get a move based on the AI difficulty level to enforce win percentages
     * @param predictedPlayerMove The predicted player move, or null if prediction isn't available
     * @return A move designed to ensure player win percentages match difficulty levels
     */
    private String getStrategyBasedMove(String predictedPlayerMove) {
        // Get winning percentage for current difficulty
        double aiWinRate = switch(difficultyLevel) {
            case "EASY" -> EASY_AI_WIN_RATE;
            case "MEDIUM" -> MEDIUM_AI_WIN_RATE;
            case "HARD" -> HARD_AI_WIN_RATE;
            default -> MEDIUM_AI_WIN_RATE;
        };
        
        // If no prediction is available, make a random choice with win rate in mind
        if (predictedPlayerMove == null) {
            if (random.nextDouble() < aiWinRate) {
                // Make a random move (no advantage)
                return MOVES[random.nextInt(MOVES.length)];
            } else {
                // Make a deliberately losing move
                return getLosingMove();
            }
        }
        
        // With a prediction, we can be more strategic
        if (random.nextDouble() < aiWinRate) {
            // Try to win with the winning move against predicted player move
            return getWinningMove(predictedPlayerMove);
        } else {
            // Deliberately lose by using the losing move against predicted player move
            return getLosingMove(predictedPlayerMove);
        }
    }

    /**
     * Get the move that would win against the predicted player move
     */
    private String getWinningMove(String predictedPlayerMove) {
        return switch (predictedPlayerMove) {
            case "Rock" -> "Paper";
            case "Paper" -> "Scissors";
            case "Scissors" -> "Rock";
            default -> MOVES[random.nextInt(MOVES.length)];
        };
    }
    
    /**
     * Get the move that would lose against the predicted player move
     */
    private String getLosingMove(String predictedPlayerMove) {
        return switch (predictedPlayerMove) {
            case "Rock" -> "Scissors";     // Scissors loses to Rock
            case "Paper" -> "Rock";        // Rock loses to Paper
            case "Scissors" -> "Paper";    // Paper loses to Scissors
            default -> MOVES[random.nextInt(MOVES.length)];
        };
    }
    
    /**
     * Get a random move that's likely to lose
     */
    private String getLosingMove() {
        return MOVES[random.nextInt(MOVES.length)];
    }

    public void reset() {
        playerHistory.clear();
        patternFrequency.clear();
        weightedPatternFrequency.clear(); // Reset weighted patterns
    }
}
