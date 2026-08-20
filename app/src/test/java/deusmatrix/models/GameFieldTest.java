package deusmatrix.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Random;
import org.junit.jupiter.api.Test;

class GameFieldTest {
    @Test
    void generatedFieldHasOneSolution() {
        GameField field = new GameField(GameDifficult.EASY, new Random(1));
        GameField solved = field.clone();
        GameFieldSolver solver = new GameFieldSolver(solved);

        assertTrue(solver.solve());
        assertEquals(1, solver.getSolutionsFound());
        assertTrue(solved.fieldIsSolved());
    }

    @Test
    void solverRejectsFieldWithMultipleSolutions() {
        GameField field = new GameField(GameDifficult.EASY, new Random(1));
        for (int row = 0; row < GameField.FIELD_SIZE; row++) {
            for (int column = 0; column < GameField.FIELD_SIZE; column++) {
                field.setCellValue(row, column, GameField.FIELD_EMPTY_VALUE);
            }
        }

        GameFieldSolver solver = new GameFieldSolver(field);

        assertFalse(solver.solve());
        assertEquals(2, solver.getSolutionsFound());
    }

    @Test
    void fieldRejectsInvalidCellValues() {
        GameField field = new GameField(GameDifficult.EASY, new Random(1));

        assertThrows(IllegalArgumentException.class, () -> field.setCellValue(0, 0, 10));
        assertThrows(IndexOutOfBoundsException.class, () -> field.getCellValue(-1, 0));
    }
}
