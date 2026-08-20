package deusmatrix.models;

public class GameFieldSolver {
    private final GameField gameField;
    private int solutionsFound;

    public GameFieldSolver(GameField gameField) {
        if (gameField == null) {
            throw new IllegalArgumentException("Game field must not be null");
        }
        this.gameField = gameField;
    }

    /**
     * Finds the first solution and verifies that no second solution exists.
     * The field is left with the first solution when at least one exists.
     */
    public boolean solve() {
        GameField original = gameField.clone();
        if (!findFirstSolution(gameField)) {
            solutionsFound = 0;
            return false;
        }

        solutionsFound = countSolutions(original, 2);
        return solutionsFound == 1;
    }

    public int getSolutionsFound() {
        return solutionsFound;
    }

    private boolean findFirstSolution(GameField field) {
        int[] emptyCell = findEmptyCell(field);
        if (emptyCell == null) {
            return true;
        }

        int row = emptyCell[0];
        int column = emptyCell[1];
        for (int value = 1; value <= GameField.FIELD_SIZE; value++) {
            if (isValidMove(field, row, column, value)) {
                field.setCellValue(row, column, value);
                if (findFirstSolution(field)) {
                    return true;
                }
                field.setCellValue(row, column, GameField.FIELD_EMPTY_VALUE);
            }
        }

        return false;
    }

    private int countSolutions(GameField field, int limit) {
        int[] emptyCell = findEmptyCell(field);
        if (emptyCell == null) {
            return 1;
        }

        int row = emptyCell[0];
        int column = emptyCell[1];
        int count = 0;
        for (int value = 1; value <= GameField.FIELD_SIZE && count < limit; value++) {
            if (isValidMove(field, row, column, value)) {
                field.setCellValue(row, column, value);
                count += countSolutions(field, limit - count);
                field.setCellValue(row, column, GameField.FIELD_EMPTY_VALUE);
            }
        }

        return count;
    }

    private int[] findEmptyCell(GameField field) {
        for (int row = 0; row < GameField.FIELD_SIZE; row++) {
            for (int column = 0; column < GameField.FIELD_SIZE; column++) {
                if (field.getCellValue(row, column) == GameField.FIELD_EMPTY_VALUE) {
                    return new int[] {row, column};
                }
            }
        }
        return null;
    }

    private boolean isValidMove(GameField field, int rowIndex, int columnIndex, int value) {
        for (int column = 0; column < GameField.FIELD_SIZE; column++) {
            if (field.getCellValue(rowIndex, column) == value) {
                return false;
            }
        }

        for (int row = 0; row < GameField.FIELD_SIZE; row++) {
            if (field.getCellValue(row, columnIndex) == value) {
                return false;
            }
        }

        int blockRow = (rowIndex / GameField.BLOCKS_IN_LINE_COUNT) * GameField.NUMS_IN_BLOCK_COUNT;
        int blockColumn = (columnIndex / GameField.BLOCKS_IN_LINE_COUNT) * GameField.NUMS_IN_BLOCK_COUNT;
        for (int row = blockRow; row < blockRow + GameField.NUMS_IN_BLOCK_COUNT; row++) {
            for (int column = blockColumn; column < blockColumn + GameField.NUMS_IN_BLOCK_COUNT; column++) {
                if (field.getCellValue(row, column) == value) {
                    return false;
                }
            }
        }

        return true;
    }
}
