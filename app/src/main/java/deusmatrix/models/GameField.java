package deusmatrix.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class GameField implements Cloneable {
    private int[][] field;
    private GameDifficult difficult;
    private final Random random;

    public static final int FIELD_SIZE = 9;
    public static final int BLOCKS_IN_LINE_COUNT = 3;
    public static final int NUMS_IN_BLOCK_COUNT = 3;
    public static final int FIELD_EMPTY_VALUE = 0;

    private static final int[][] BASE_SOLUTION = {
            {5, 3, 4, 6, 7, 8, 9, 1, 2},
            {6, 7, 2, 1, 9, 5, 3, 4, 8},
            {1, 9, 8, 3, 4, 2, 5, 6, 7},
            {8, 5, 9, 7, 6, 1, 4, 2, 3},
            {4, 2, 6, 8, 5, 3, 7, 9, 1},
            {7, 1, 3, 9, 2, 4, 8, 5, 6},
            {9, 6, 1, 5, 3, 7, 2, 8, 4},
            {2, 8, 7, 4, 1, 9, 6, 3, 5},
            {3, 4, 5, 2, 8, 6, 1, 7, 9}
    };

    public GameField(GameDifficult difficult) {
        this.field = new int[FIELD_SIZE][FIELD_SIZE];
        this.difficult = difficult;
        this.random = new Random();

        generateGame(difficult);
    }

    private GameField(GameDifficult difficult, int[][] field) {
        this.field = field;
        this.difficult = difficult;
        this.random = new Random();
    }

    private void generateGame(GameDifficult difficult) {
        int[][] solved = generateSolvedField();
        removeNums(solved, difficult.getNumsCountToRemove());
    }

    private int[][] generateSolvedField() {
        int[][] grid = new int[FIELD_SIZE][FIELD_SIZE];
        for (int row = 0; row < FIELD_SIZE; row++) {
            System.arraycopy(BASE_SOLUTION[row], 0, grid[row], 0, FIELD_SIZE);
        }

        swapBlocksRows(grid);
        swapBlocksColumns(grid);
        swapRowsBlocks(grid);
        swapColumnsBlocks(grid);

        int[] mapping = generateMapping();

        int[][] result = new int[FIELD_SIZE][FIELD_SIZE];
        for (int row = 0; row < FIELD_SIZE; row++) {
            for (int column = 0; column < FIELD_SIZE; column++) {
                int currentValue = grid[row][column];
                result[row][column] = mapping[currentValue];
            }
        }
        return result;
    }

    private void swapBlocksRows(int[][] grid) {
        for (int blockRow = 0; blockRow < BLOCKS_IN_LINE_COUNT; blockRow++) {
            int r1 = blockRow * NUMS_IN_BLOCK_COUNT + random.nextInt(NUMS_IN_BLOCK_COUNT);
            int r2 = blockRow * NUMS_IN_BLOCK_COUNT + random.nextInt(NUMS_IN_BLOCK_COUNT);
            swapRows(grid, r1, r2);
        }
    }

    private void swapBlocksColumns(int[][] grid) {
        for (int blockColumn = 0; blockColumn < BLOCKS_IN_LINE_COUNT; blockColumn++) {
            int firstColumn = blockColumn * NUMS_IN_BLOCK_COUNT + random.nextInt(NUMS_IN_BLOCK_COUNT);
            int secondColumn = blockColumn * NUMS_IN_BLOCK_COUNT + random.nextInt(NUMS_IN_BLOCK_COUNT);
            swapColumns(grid, firstColumn, secondColumn);
        }
    }

    private void swapRowsBlocks(int[][] grid) {
        for (int i = 0; i < BLOCKS_IN_LINE_COUNT; i++) {
            int firstBlock = random.nextInt(BLOCKS_IN_LINE_COUNT);
            int secondBlock = random.nextInt(BLOCKS_IN_LINE_COUNT);
            swapRowBlocks(grid, firstBlock, secondBlock);
        }
    }

    private void swapColumnsBlocks(int[][] grid) {
        for (int i = 0; i < BLOCKS_IN_LINE_COUNT; i++) {
            int firstBlock = random.nextInt(BLOCKS_IN_LINE_COUNT);
            int secondBlock = random.nextInt(BLOCKS_IN_LINE_COUNT);
            swapColumnBlocks(grid, firstBlock, secondBlock);
        }
    }

    private int[] generateMapping() {
        int[] mapping = null;

        List<Integer> nums = new ArrayList<>();

        for (int n = 1; n <= FIELD_SIZE; n++) {
            nums.add(n);
        }

        Collections.shuffle(nums, random);

        mapping = new int[FIELD_SIZE + 1];

        for (int n = 1; n <= FIELD_SIZE; n++) {
            mapping[n] = nums.get(n - 1);
        }

        return mapping;
    }

    private void removeNums(int[][] solved, int numsCountToRemove) {
        setSolvedValues(solved);

        List<int[]> cellsAddresses = getAllCellsAddresses();

        int removed = 0;
        for (int[] cellAddress : cellsAddresses) {
            if (removed >= numsCountToRemove) {
                break;
            }

            int row = cellAddress[0];
            int column = cellAddress[1];
            int saved = this.field[row][column];
            this.field[row][column] = FIELD_EMPTY_VALUE;

            GameField clone = this.cloneField();
            GameFieldSolver solver = new GameFieldSolver(clone);
            if (!solver.solve()) {
                this.field[row][column] = saved;
                continue;
            }

            removed++;
        }
    }

    private void setSolvedValues(int[][] solved) {
        for (int row = 0; row < FIELD_SIZE; row++) {
            System.arraycopy(solved[row], 0, this.field[row], 0, FIELD_SIZE);
        }
    }

    private List<int[]> getAllCellsAddresses() {
        List<int[]> cells = new ArrayList<>();

        for (int row = 0; row < FIELD_SIZE; row++) {
            for (int column = 0; column < FIELD_SIZE; column++) {
                cells.add(new int[]{row, column});
            }
        }

        Collections.shuffle(cells, random);

        return cells;
    }

    private void swapRows(int[][] grid, int firstRow, int secondRow) {
        if (firstRow == secondRow) {
            return;
        }

        int[] tmp = grid[firstRow];
        grid[firstRow] = grid[secondRow];
        grid[secondRow] = tmp;
    }

    private void swapColumns(int[][] grid, int firstcolumn, int secondColumn) {
        if (firstcolumn == secondColumn){
            return;
        }

        for (int row = 0; row < FIELD_SIZE; row++) {
            int tmp = grid[row][firstcolumn];
            grid[row][firstcolumn] = grid[row][secondColumn];
            grid[row][secondColumn] = tmp;
        }
    }

    private void swapRowBlocks(int[][] grid, int firstBlock, int secondBlock) {
        if (firstBlock == secondBlock) {
            return;
        }

        for (int offset = 0; offset < BLOCKS_IN_LINE_COUNT; offset++) {
            swapRows(grid, firstBlock * NUMS_IN_BLOCK_COUNT + offset, secondBlock * NUMS_IN_BLOCK_COUNT + offset);
        }
    }

    private void swapColumnBlocks(int[][] grid, int firstBlock, int secondBlock) {
        if (firstBlock == secondBlock) {
            return;
        }

        for (int offset = 0; offset < BLOCKS_IN_LINE_COUNT; offset++) {
            swapColumns(grid, firstBlock * NUMS_IN_BLOCK_COUNT + offset, secondBlock * NUMS_IN_BLOCK_COUNT + offset);
        }
    }

    private GameField cloneField() {
        int[][] cloneField = new int[FIELD_SIZE][FIELD_SIZE];

        for (int row = 0; row < FIELD_SIZE; row++) {
            System.arraycopy(this.field[row], 0, cloneField[row], 0, FIELD_SIZE);
        }

        return new GameField(this.difficult, cloneField);
    }

    public boolean fieldIsSolved() {
        for (int row = 0; row < FIELD_SIZE; row++) {
            for (int column = 0; column < FIELD_SIZE; column++) {
                if (field[row][column] == FIELD_EMPTY_VALUE) return false;
            }
        }
        return true;
    }

    public void setCellValue(int row, int col, int value) {
        field[row][col] = value;
    }

    public int getCellValue(int row, int col) {
        return field[row][col];
    }

    @Override
    protected GameField clone() throws CloneNotSupportedException {
        return cloneField();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        String sep = "-------------------------------------\n";
        sb.append(sep);

        for (int row = 0; row < FIELD_SIZE; row++) {
            sb.append("|");

            for (int column = 0; column < FIELD_SIZE; column++) {
                int v = field[row][column];
                String cell = (v == FIELD_EMPTY_VALUE) ? "   " : String.format(" %d ", v);
                sb.append(cell).append("|");
            }

            sb.append("\n");

            if ((row + 1) % NUMS_IN_BLOCK_COUNT == 0) {
                sb.append(sep);
            }
        }
        return sb.toString();
    }
}
