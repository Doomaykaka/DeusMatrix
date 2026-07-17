package deusmatrix.controllers;

import deusmatrix.models.GameDifficult;
import deusmatrix.models.GameField;
import deusmatrix.models.GameFieldSolver;

public class SudokuGameController {
    private Long gameStartTimeInMillis;
    private Long attempts;
    private Long maxAttempts;

    private static final int MILLISECONDS_IN_ONE_SECOND = 1000;

    public SudokuGameController() {
        this.attempts = 0L;
        this.maxAttempts = 3L;
    }

    public GameField createGameField(GameDifficult gameDifficult) {
        return new GameField(gameDifficult);
    }

    public GameFieldSolver createGameFieldSolver(GameField gameField) {
        return new GameFieldSolver(gameField);
    }

    public void startGameTimer() {
        this.gameStartTimeInMillis = System.currentTimeMillis();
    }

    public long stopGameTimerAndGetResultInSeconds() throws RuntimeException {
        if (this.gameStartTimeInMillis == null) {
            throw new RuntimeException("Start timer before stop");
        }

        long gameTimeInMillis = System.currentTimeMillis() - this.gameStartTimeInMillis;

        this.gameStartTimeInMillis = null;

        return gameTimeInMillis / MILLISECONDS_IN_ONE_SECOND;
    }

    public void addAttempts() {
        if (attempts != maxAttempts) {
            attempts++;
        }
    }

    public long getAttempts() {
        return attempts;
    }
}
