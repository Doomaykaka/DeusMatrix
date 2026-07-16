package deusmatrix.models;

public class GameFieldSolver {
    private GameField gameField;
    private int solutionsFound;
    private boolean solutionApplied;

    public GameFieldSolver(GameField gameField) {
        this.gameField = gameField;
    }

    public boolean solve() {
        solutionsFound = 0;
        solutionApplied = false;

        backtrack();

        return solutionsFound == 1 && solutionApplied;
    }

    private void backtrack() {
        int row = -1;
        int column = -1;
        boolean hasEmpty = false;

        outer:
        for (int i = 0; i < GameField.FIELD_SIZE; i++) {
            for (int j = 0; j < GameField.FIELD_SIZE; j++) {
                if (gameField.getCellValue(i, j) == GameField.FIELD_EMPTY_VALUE) {
                    row = i;
                    column = j;
                    hasEmpty = true;
                    break outer;
                }
            }
        }

        if (!hasEmpty) {
            solutionsFound++;
            if (!solutionApplied) {
                solutionApplied = true;
            }
            return;
        }

        if (solutionsFound > 1) {
            return;
        }

        makeStepAndGoToNextStep(row, column);
    }

    private void makeStepAndGoToNextStep(int row, int column) {
        for (int num = 1; num <= GameField.FIELD_SIZE; num++) {
            if (isValidMove(row, column, num)) {
                gameField.setCellValue(row, column, num);
                backtrack();

                if (!solutionApplied) {
                    gameField.setCellValue(row, column, GameField.FIELD_EMPTY_VALUE);
                } else {
                    return;
                }

                if (solutionsFound > 1) {
                    return;
                }
            }
        }
    }

    private boolean isValidMove(int rowIndex, int columnIndex, int num) {
        for (int column = 0; column < GameField.FIELD_SIZE; column++) {
            if (gameField.getCellValue(rowIndex, column) == num) {
                return false;
            }
        }

        for (int row = 0; row < GameField.FIELD_SIZE; row++) {
            if (gameField.getCellValue(row, columnIndex) == num) {
                return false;
            }
        }

        int blockRow = (rowIndex / GameField.BLOCKS_IN_LINE_COUNT) * GameField.NUMS_IN_BLOCK_COUNT;
        int blockColumn = (columnIndex / GameField.BLOCKS_IN_LINE_COUNT) * GameField.NUMS_IN_BLOCK_COUNT;
        for (int i = 0; i < GameField.NUMS_IN_BLOCK_COUNT; i++) {
            for (int j = 0; j < GameField.NUMS_IN_BLOCK_COUNT; j++) {
                if (gameField.getCellValue(blockRow + i, blockColumn + j) == num) {
                    return false;
                }
            }
        }

        return true;
    }
}
