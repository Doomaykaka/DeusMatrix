package deusmatrix.controllers;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

import deusmatrix.models.GameDifficult;
import deusmatrix.models.User;
import deusmatrix.utils.SupportFunctions;
import org.junit.jupiter.api.Test;

class SudokuGameControllerTest {
    @Test
    void rewardsAreSafeBeforeFirstTimerTick() {
        SudokuGameController controller = new SudokuGameController();
        User user = SupportFunctions.createEmptyUser("test");

        assertDoesNotThrow(() -> controller.giveRewards(GameDifficult.EASY, user));
        assertTrue(user.getExperience() > 0);
    }
}
