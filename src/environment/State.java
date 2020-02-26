package environment;

import constants.Constants;

public class State {

    private double reward = Constants.REWARD_WHITE;
    private boolean isWall = false;

    public State() {};

    public State(String color, boolean wall) {
        if (!wall) {
            if (color == "White") reward = Constants.REWARD_WHITE;
            else if (color == "Green") reward = Constants.REWARD_GREEN;
            else reward = Constants.REWARD_BROWN;
        }
        else isWall = true;
    }

    public double getReward() {
        return reward;
    }

    public void setReward(double reward) {
        this.reward = reward;
    }

    public boolean isWall() {
        return isWall;
    }

    public void setWall(boolean wall) {
        isWall = wall;
    }
}
