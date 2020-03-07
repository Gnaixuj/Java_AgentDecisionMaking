package environment;

import utilities.Constants;

public class Maze {

    private State[][] mazeState;
    private Utility[][] mazeUtility;

    public Maze() {
        mazeState = new State[Constants.NUM_OF_COLS][Constants.NUM_OF_ROWS];
        mazeUtility = new Utility[Constants.NUM_OF_COLS][Constants.NUM_OF_ROWS];

        for (int i = 0; i < Constants.NUM_OF_COLS; i++) {
            for (int j = 0; j < Constants.NUM_OF_ROWS; j++) {
                mazeState[i][j] = new State();
                mazeUtility[i][j] = new Utility();
            }
        }

        for (int[] j : Constants.LOCATION_GREEN) {
            mazeState[j[0]][j[1]].setReward(Constants.REWARD_GREEN);
        }

        for (int[] j : Constants.LOCATION_BROWN) {
            mazeState[j[0]][j[1]].setReward(Constants.REWARD_BROWN);
        }

        for (int[] j : Constants.LOCATION_WALL) {
            mazeState[j[0]][j[1]].setReward(0);
            mazeState[j[0]][j[1]].setWall(true);
        }
    }

    public State[][] getMazeState() {
        return mazeState;
    }

    public Utility[][] getMazeUtility() {
        return mazeUtility;
    }

    public void setMazeUtility(Utility[][] u) { mazeUtility = u; }
}
