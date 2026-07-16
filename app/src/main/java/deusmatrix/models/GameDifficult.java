package deusmatrix.models;

public enum GameDifficult {
    EASY, MIDDLE, HARD;

    private static final int NUMS_COUNT_TO_REMOVE_WITH_EASY_LEVEL = 45;
    private static final int NUMS_COUNT_TO_REMOVE_WITH_MIDDLE_LEVEL = 50;
    private static final int NUMS_COUNT_TO_REMOVE_WITH_HARD_LEVEL = 55;
    
    public int getNumsCountToRemove() {
        if(this.equals(EASY)) {
            return NUMS_COUNT_TO_REMOVE_WITH_EASY_LEVEL;
        } else if(this.equals(MIDDLE)) {
            return NUMS_COUNT_TO_REMOVE_WITH_MIDDLE_LEVEL;
        } 
            
        return NUMS_COUNT_TO_REMOVE_WITH_HARD_LEVEL;
    }
}
