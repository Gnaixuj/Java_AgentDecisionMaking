package utilities;

public class Constants {
    public static final int NUM_OF_ROWS = 6;
    public static final int NUM_OF_COLS = 6;

    public static final double PROB_INTENDED = 0.8;
    public static final double PROB_RIGHT_ANGLE = 0.1;

    public static final double REWARD_WHITE = -0.04;
    public static final double REWARD_GREEN = 1;
    public static final double REWARD_BROWN = -1;

    public static final double E = 0.1;
    public static final int K = 20;

    public static final double DISCOUNT_FACTOR = 0.99;

    public static final int[] LOCATION_START = {2, 3};
    public static final int[][] LOCATION_GREEN = { {0, 0}, {2, 0}, {3, 1}, {4, 2}, {5, 0}, {5, 3} };
    public static final int[][] LOCATION_BROWN = { {1, 1}, {2, 2}, {3, 3}, {4, 4}, {5, 1} };
    public static final int[][] LOCATION_WALL = { {1, 0}, {1, 4}, {2, 4}, {3, 4}, {4, 1} };
}
