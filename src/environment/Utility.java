package environment;

import utilities.Constants;

public class Utility {
//    public enum Action {
//        UP, DOWN, LEFT, RIGHT
//    };
    private double utility = 0;
    private String action = "LEFT";

//    public double calculateUtility (State s) {
//        switch (action) {
//            case ("Up"): return s.getReward() + Constants.PROB_INTENDED *
//        }
//    }

    public Utility() {}

    public double getUtility() {
        return utility;
    }

    public void setUtility(double utility) {
        this.utility = utility;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
