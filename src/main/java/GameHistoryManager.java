import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class GameHistoryManager {
    private static final String DATA_DIR = "data";
    private static final String HISTORY_FILE = "game_history.csv";
    private static final String[] CSV_HEADERS = {"Date", "Mode", "Player1", "Player2", "Player1Score", "Player2Score", "Result"};
    private static GameHistoryManager instance;
    private final List<GameRecord> gameHistory;
    private final Path historyFilePath;

    private GameHistoryManager() {
        gameHistory = new ArrayList<>();
        // Create data directory if it doesn't exist
        try {
            Files.createDirectories(Paths.get(DATA_DIR));
            historyFilePath = Paths.get(DATA_DIR, HISTORY_FILE);
            loadHistory();
        } catch (IOException e) {
            System.err.println("Error creating data directory: " + e.getMessage());
            throw new RuntimeException("Failed to initialize game history storage", e);
        }
    }

    public static synchronized GameHistoryManager getInstance() {
        if (instance == null) {
            instance = new GameHistoryManager();
        }
        return instance;
    }

    public static class GameRecord {
        public final String date;
        public final String mode;
        public final String player1;
        public final String player2;
        public final int player1Score;
        public final int player2Score;
        public final String result;

        public GameRecord(String mode, String player1, String player2, int player1Score, int player2Score) {
            this.date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            this.mode = mode;
            this.player1 = player1;
            this.player2 = player2;
            this.player1Score = player1Score;
            this.player2Score = player2Score;
            this.result = determineResult(player1, player2, player1Score, player2Score);
        }

        // Constructor for loading from CSV with preserved date
        public GameRecord(String date, String mode, String player1, String player2, int player1Score, int player2Score, String result) {
            this.date = date;
            this.mode = mode;
            this.player1 = player1;
            this.player2 = player2;
            this.player1Score = player1Score;
            this.player2Score = player2Score;
            this.result = result;
        }

        private String determineResult(String player1, String player2, int score1, int score2) {
            if (score1 > score2) return player1 + " won";
            if (score2 > score1) return player2 + " won";
            return "Draw";
        }

        public String toCSV() {
            // Escape any commas in the fields
            String escapedPlayer1 = player1.contains(",") ? "\"" + player1 + "\"" : player1;
            String escapedPlayer2 = player2.contains(",") ? "\"" + player2 + "\"" : player2;
            String escapedResult = result.contains(",") ? "\"" + result + "\"" : result;
            
            return String.join(",", 
                date,
                mode,
                escapedPlayer1,
                escapedPlayer2,
                String.valueOf(player1Score),
                String.valueOf(player2Score),
                escapedResult);
        }

        public static GameRecord fromCSV(String csvLine) {
            try {
                String[] parts = csvLine.split(",");
                if (parts.length < 7) {
                    throw new IllegalArgumentException("Invalid CSV line format");
                }
                return new GameRecord(
                    parts[0], // date (preserved from CSV)
                    parts[1], // mode
                    parts[2], // player1
                    parts[3], // player2
                    Integer.parseInt(parts[4].trim()), // player1Score
                    Integer.parseInt(parts[5].trim()), // player2Score
                    parts[6]  // result
                );
            } catch (Exception e) {
                throw new IllegalArgumentException("Error parsing CSV line: " + csvLine, e);
            }
        }
    }

    private void loadHistory() {
        try {
            if (Files.exists(historyFilePath)) {
                List<String> lines = Files.readAllLines(historyFilePath);
                if (!lines.isEmpty() && !lines.get(0).equals(String.join(",", CSV_HEADERS))) {
                    // If file exists but doesn't have headers, backup the old file and create a new one
                    backupExistingFile();
                    saveHistory();
                    return;
                }
                // Skip header line
                for (int i = 1; i < lines.size(); i++) {
                    try {
                        if (!lines.get(i).trim().isEmpty()) {
                            gameHistory.add(parseCSVLine(lines.get(i)));
                        }
                    } catch (Exception e) {
                        System.err.println("Error parsing history line " + i + ": " + e.getMessage());
                    }
                }
            } else {
                // Create new file with headers
                saveHistory();
            }
        } catch (IOException e) {
            System.err.println("Error loading game history: " + e.getMessage());
        }
    }

    // Helper method to parse CSV lines with proper handling of quoted fields
    private GameRecord parseCSVLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean inQuotes = false;
        
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            
            if (c == '"') {
                // Toggle quote state
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                // End of field, add to list
                fields.add(sb.toString());
                sb.setLength(0);
            } else {
                // Add character to field
                sb.append(c);
            }
        }
        
        // Add the last field
        fields.add(sb.toString());
        
        if (fields.size() < 7) {
            throw new IllegalArgumentException("Invalid CSV line format: expected at least 7 fields, got " + fields.size());
        }
        
        // Use the fields to create the GameRecord with preserved date
        return new GameRecord(
            fields.get(0), // date (preserved from CSV)
            fields.get(1), // mode
            fields.get(2), // player1 
            fields.get(3), // player2
            Integer.parseInt(fields.get(4).trim()), // player1Score
            Integer.parseInt(fields.get(5).trim()), // player2Score
            fields.get(6)  // result
        );
    }

    private void backupExistingFile() {
        try {
            if (Files.exists(historyFilePath)) {
                String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
                Path backupPath = historyFilePath.resolveSibling(HISTORY_FILE + "." + timestamp + ".bak");
                Files.move(historyFilePath, backupPath, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("Backed up existing history file to: " + backupPath);
            }
        } catch (IOException e) {
            System.err.println("Error backing up history file: " + e.getMessage());
        }
    }

    public void saveHistory() {
        try {
            List<String> lines = new ArrayList<>();
            lines.add(String.join(",", CSV_HEADERS));
            for (GameRecord record : gameHistory) {
                lines.add(record.toCSV());
            }
            // Create a temporary file first
            Path tempFile = Files.createTempFile(historyFilePath.getParent(), "history_", ".tmp");
            Files.write(tempFile, lines);
            // Atomically move the temporary file to the target location
            Files.move(tempFile, historyFilePath, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException e) {
            System.err.println("Error saving game history: " + e.getMessage());
        }
    }

    public void addRecord(GameRecord record) {
        gameHistory.add(record);
        saveHistory();
    }

    public List<GameRecord> getGameHistory() {
        return new ArrayList<>(gameHistory);
    }

    public void clearHistory() {
        gameHistory.clear();
        saveHistory();
    }

    public Map<String, PlayerStats> getLeaderboard() {
        Map<String, PlayerStats> stats = new HashMap<>();

        for (GameRecord record : gameHistory) {
            // Update Player 1 stats
            stats.computeIfAbsent(record.player1, k -> new PlayerStats())
                 .updateStats(record.player1Score, record.player2Score);

            // Update Player 2 stats
            stats.computeIfAbsent(record.player2, k -> new PlayerStats())
                 .updateStats(record.player2Score, record.player1Score);
        }

        return stats;
    }

    public Map<String, Map<String, PlayerStats>> getLeaderboardByMode() {
        Map<String, Map<String, PlayerStats>> statsByMode = new HashMap<>();
        // Initialize with known modes
        statsByMode.put("PvC", new HashMap<>());
        statsByMode.put("PvP", new HashMap<>());

        for (GameRecord record : gameHistory) {
            // Get the correct mode map
            Map<String, PlayerStats> modeStats = statsByMode.computeIfAbsent(record.mode, _ -> new HashMap<>());
            
            // Update Player 1 stats
            modeStats.computeIfAbsent(record.player1, _ -> new PlayerStats())
                     .updateStats(record.player1Score, record.player2Score);

            // Update Player 2 stats
            modeStats.computeIfAbsent(record.player2, _ -> new PlayerStats())
                     .updateStats(record.player2Score, record.player1Score);
        }

        return statsByMode;
    }

    public static class PlayerStats {
        private int gamesPlayed = 0;
        private int wins = 0;
        private int losses = 0;
        private int draws = 0;
        private int totalScore = 0;
        private int totalOpponentScore = 0;

        public void updateStats(int score, int opponentScore) {
            gamesPlayed++;
            totalScore += score;
            totalOpponentScore += opponentScore;

            if (score > opponentScore) wins++;
            else if (score < opponentScore) losses++;
            else draws++;
        }

        public double getWinRate() {
            return gamesPlayed == 0 ? 0 : (double) wins / gamesPlayed * 100;
        }

        public int getGamesPlayed() { return gamesPlayed; }
        public int getWins() { return wins; }
        public int getLosses() { return losses; }
        public int getDraws() { return draws; }
        public int getTotalScore() { return totalScore; }
        public double getAverageScore() {
            return gamesPlayed == 0 ? 0 : (double) totalScore / gamesPlayed;
        }
    }

    // Add method to get the history file path
    public String getHistoryFilePath() {
        return historyFilePath.toString();
    }

    // Add method to export history to a different location
    public void exportHistory(String targetPath) throws IOException {
        Path target = Paths.get(targetPath);
        Files.copy(historyFilePath, target, StandardCopyOption.REPLACE_EXISTING);
    }

    // Add method to import history from a file
    public void importHistory(String sourcePath) throws IOException {
        Path source = Paths.get(sourcePath);
        if (!Files.exists(source)) {
            throw new FileNotFoundException("Source file not found: " + sourcePath);
        }
        
        // Backup current history
        backupExistingFile();
        
        // Clear current history
        gameHistory.clear();
        
        // Copy the new file
        Files.copy(source, historyFilePath, StandardCopyOption.REPLACE_EXISTING);
        
        // Reload history
        loadHistory();
    }
}