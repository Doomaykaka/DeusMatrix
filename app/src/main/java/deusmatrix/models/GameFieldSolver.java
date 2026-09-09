package deusmatrix.models;

public class GameFieldSolver {
    private GameField gameField;
    private int solutionsFound;

    public GameFieldSolver(GameField gameField) {
        this.gameField = gameField;
    }

    public boolean haveOneSolution() {
        solutionsFound = 0;
        countSolutions();
        return solutionsFound == 1;
    }

    public boolean solveAndApply() {
        return fillOne();
    }

    private void countSolutions() {
        int[] pos = findEmpty();

        if (pos == null) {
            solutionsFound++;
            return;
        }

        if (solutionsFound > 1) return;

        int row = pos[0], col = pos[1];

        for (int num = 1; num <= GameField.FIELD_SIZE; num++) {
            if (isValidMove(row, col, num)) {
                gameField.setCellValue(row, col, num);
                countSolutions();
                gameField.setCellValue(row, col, GameField.FIELD_EMPTY_VALUE);
                if (solutionsFound > 1) return;
            }
        }
    }

    private boolean fillOne() {
        int[] pos = findEmpty();

        if (pos == null) return true;

        int row = pos[0], col = pos[1];

        for (int num = 1; num <= GameField.FIELD_SIZE; num++) {
            if (isValidMove(row, col, num)) {
                gameField.setCellValue(row, col, num);
                if (fillOne()) return true; // решение найдено — выходим
                gameField.setCellValue(row, col, GameField.FIELD_EMPTY_VALUE);
            }
        }

        return false;
    }

    private int[] findEmpty() {
        for (int i = 0; i < GameField.FIELD_SIZE; i++)
            for (int j = 0; j < GameField.FIELD_SIZE; j++)
                if (gameField.getCellValue(i, j) == GameField.FIELD_EMPTY_VALUE) return new int[] {i, j};
        return null;
    }

    private boolean isValidMove(int rowIndex, int columnIndex, int num) {
        for (int column = 0; column < GameField.FIELD_SIZE; column++)
            if (gameField.getCellValue(rowIndex, column) == num) return false;

        for (int row = 0; row < GameField.FIELD_SIZE; row++)
            if (gameField.getCellValue(row, columnIndex) == num) return false;

        int blockRow = (rowIndex / GameField.BLOCKS_IN_LINE_COUNT) * GameField.NUMS_IN_BLOCK_COUNT;
        int blockColumn = (columnIndex / GameField.BLOCKS_IN_LINE_COUNT) * GameField.NUMS_IN_BLOCK_COUNT;
        for (int i = 0; i < GameField.NUMS_IN_BLOCK_COUNT; i++)
            for (int j = 0; j < GameField.NUMS_IN_BLOCK_COUNT; j++)
                if (gameField.getCellValue(blockRow + i, blockColumn + j) == num) return false;

        return true;
    }
}
