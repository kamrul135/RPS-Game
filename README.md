# Rock Paper Scissors Game

A modern Java implementation of the classic Rock Paper Scissors game with an advanced AI opponent and multiple game modes.

![Game Screenshot](resource/image/game_screenshot.png)

## Features

- **Multiple Game Modes**
  - Player vs Computer (PvC)
  - Player vs Player (PvP)

- **Advanced AI**
  - Three difficulty levels: Easy, Medium, Hard
  - AI learns from player's previous moves
  - Pattern recognition to predict player behavior

- **Customization Options**
  - Adjustable number of rounds (Best of 3/5/7)
  - Player name customization
  - Dark and Light theme options

- **Rich Game Interface**
  - Modern UI with animated elements
  - Visual move indicators
  - Game statistics tracking
  - Match history

## Requirements

- Java Development Kit (JDK) 17 or higher
- Minimum resolution: 1024x768

## Repository Cleanup

Several auxiliary files and reports that were used during
development have been removed to keep the repository focused on the
core game. Build output (`build/`), IDE settings (`.vscode/`), and
runtime data (`data/`) are ignored by the accompanying
`.gitignore` file.  Developers should compile from source using the
instructions below; temporary files will not be committed.

## How to Play

### Starting the Game

1. Compile and run `RockPaperScissorsGame.java`
2. Choose a game mode: PvC or PvP
3. Enter player name(s)
4. Select number of rounds
5. If in PvC mode, adjust AI difficulty as needed
6. Click "Start" to begin

### Controls

#### Player vs Computer Mode
- Press `R` for Rock
- Press `P` for Paper
- Press `S` for Scissors
- You can also click the corresponding buttons

#### Player vs Player Mode
- **Player 1**:
  - `A` for Rock
  - `S` for Paper
  - `D` for Scissors
  
- **Player 2**:
  - `J` for Rock
  - `K` for Paper
  - `L` for Scissors

## Game Rules

1. Rock beats Scissors
2. Scissors beats Paper
3. Paper beats Rock
4. First player to win the majority of rounds wins the match

## Project Structure

Source code is organized under `src/main/java` and resources (images,
sounds, configuration) are located in `src/main/resources`.
Only source files and essential documentation are tracked; compiled
artifacts, IDE settings and runtime data are excluded with a
`.gitignore` file.

Key modules include:

- `RockPaperScissorsGame.java` - Main game UI and logic
- `GameAI.java` - AI logic for predicting player moves
- `GameManager.java` - Manages game state and AI integration
- `GameResultPanel.java` - Custom panel for displaying game results

## AI Difficulty Levels

- **Easy**: Mostly random moves with minimal pattern recognition
- **Medium**: Balanced between random moves and strategic decisions
- **Hard**: Advanced pattern recognition with adaptive strategies

## Future Improvements

- Network multiplayer support
- Additional gesture options (Lizard, Spock)
- Sound effects and music
- Achievement system
- Customizable themes and skins

## Credits

Developed as a Java programming project with focus on:
- Object-oriented design
- UI/UX principles
- AI implementation
- Event-driven programming

## License

This project is licensed under the MIT License - see the LICENSE file for details. 