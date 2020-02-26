package environment;

import constants.Constants;

public class Maze {

    private State[][] maze;

    public Maze() {
        maze = new State[Constants.NUM_OF_COLS][Constants.NUM_OF_ROWS];

        for (int i = 0; i < Constants.NUM_OF_COLS; i++) {
            for (int j = 0; j < Constants.NUM_OF_ROWS; j++) {
                maze[i][j] = new State();
            }
        }

        for (int[] j : Constants.LOCATION_GREEN) {
            maze[j[0]][j[1]].setReward(Constants.REWARD_GREEN);
        }

        for (int[] j : Constants.LOCATION_BROWN) {
            maze[j[0]][j[1]].setReward(Constants.REWARD_BROWN);
        }

        for (int[] j : Constants.LOCATION_WALL) {
            maze[j[0]][j[1]].setReward(0);
            maze[j[0]][j[1]].setWall(true);
        }
    }

    public State[][] getMaze() {
        return maze;
    }
}
