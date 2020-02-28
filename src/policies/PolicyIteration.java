package policies;

import environment.Maze;
import environment.State;
import environment.Utility;
import utilities.Constants;

public class PolicyIteration {

    public void PIteration(Maze m) {
        boolean converge = false;
        State curState, upState, downState, leftState, rightState;
        State[][] mazeState = m.getMazeState();
        Utility[][] mazeUtility = m.getMazeUtility();
        while (!converge) {
            Utility[][] newUtility = policyEvaluation(mazeState, mazeUtility);
            newUtility = policyImprovement(mazeState, newUtility);
            converge = convergenceTest(mazeUtility, newUtility); // u want the new action but keep the old utility; make a copy of mazeState?
        }
    }

    // newCurStateUtility - updated utility of state s after going thru the Bellman Equation
    public Utility[][] policyEvaluation(State[][] s, Utility[][] u) {
        Utility[][] newUtility = new Utility[Constants.NUM_OF_COLS][Constants.NUM_OF_ROWS];
        Utility upU, downU, leftU, rightU;
        double newCurStateUtility, optimalUtility;

        for (int i = 0; i < Constants.NUM_OF_COLS; i++) {
            for (int j = 0; j < Constants.NUM_OF_ROWS; j++) {
                upU = getNextStateUtility(s, u, i, j, i, j-1);
                downU = getNextStateUtility(s, u, i, j, i, j+1);
                leftU = getNextStateUtility(s, u, i, j, i-1, j);
                rightU = getNextStateUtility(s, u, i, j, i+1, j);

                switch(u[i][j].getAction()) {
                    case ("UP"): {
                        if (s[i][j-1].isWall()) optimalUtility = upU.getUtility(); // stay put
                        else optimalUtility = Constants.PROB_INTENDED * upU.getUtility() +
                                Constants.PROB_RIGHT_ANGLE * leftU.getUtility() +
                                Constants.PROB_RIGHT_ANGLE * rightU.getUtility();
                        break;
                    }
                    case ("DOWN"): {
                        if (s[i][j+1].isWall()) optimalUtility = downU.getUtility(); // stay put
                        else optimalUtility = Constants.PROB_INTENDED * downU.getUtility() +
                                Constants.PROB_RIGHT_ANGLE * leftU.getUtility() +
                                Constants.PROB_RIGHT_ANGLE * rightU.getUtility();
                        break;
                    }
                    case ("LEFT"): {
                        if (s[i-1][j].isWall()) optimalUtility = leftU.getUtility(); // stay put
                        else optimalUtility = Constants.PROB_INTENDED * leftU.getUtility() +
                                Constants.PROB_RIGHT_ANGLE * upU.getUtility() +
                                Constants.PROB_RIGHT_ANGLE * downU.getUtility();
                        break;
                    }
                    case ("RIGHT"): {
                        if (s[i+1][j].isWall()) optimalUtility = rightU.getUtility(); // stay put
                        else optimalUtility = Constants.PROB_INTENDED * rightU.getUtility() +
                                Constants.PROB_RIGHT_ANGLE * upU.getUtility() +
                                Constants.PROB_RIGHT_ANGLE * downU.getUtility();
                        break;
                    }
                    default: optimalUtility = 0;
                }

                newCurStateUtility = s[i][j].getReward() + Constants.DISCOUNT_FACTOR * optimalUtility;
                newUtility[i][j].setUtility(newCurStateUtility);
            }
        }
        return newUtility;
    }

    public Utility getNextStateUtility(State[][] s, Utility[][] u, int curCol, int curRow, int nextCol, int nextRow) {
        if (nextCol < 0 || nextCol > 5 || nextRow < 0 || nextRow > 5) return u[curCol][curRow];
        if (s[nextCol][nextRow].isWall()) return u[curCol][curRow];
        return u[nextCol][nextRow];
    }

    public Utility[][] policyImprovement(State[][] s, Utility[][] u) {
        double upEU, downEU, leftEU, rightEU;
        for (int i = 0; i < Constants.NUM_OF_COLS; i++) {
            for (int j = 0; j < Constants.NUM_OF_ROWS; j++) {

                if (j == 0 || s[i][j-1].isWall()) upEU = u[i][j].getUtility();
                else upEU = Constants.PROB_INTENDED * u[i][j-1].getUtility() +
                        Constants.PROB_RIGHT_ANGLE * u[i-1][j].getUtility() +
                        Constants.PROB_RIGHT_ANGLE * u[i+1][j].getUtility();

                if (j == 5 || s[i][j+1].isWall()) downEU = u[i][j].getUtility();
                else downEU = Constants.PROB_INTENDED * u[i][j+1].getUtility() +
                        Constants.PROB_RIGHT_ANGLE * u[i-1][j].getUtility() +
                        Constants.PROB_RIGHT_ANGLE * u[i+1][j].getUtility();

                if (i == 0 || s[i-1][j].isWall()) leftEU = u[i][j].getUtility();
                else leftEU = Constants.PROB_INTENDED * u[i-1][j].getUtility() +
                        Constants.PROB_RIGHT_ANGLE * u[i][j-1].getUtility() +
                        Constants.PROB_RIGHT_ANGLE * u[i][j+1].getUtility();

                if (i == 5 || s[i+1][j].isWall()) rightEU = u[i][j].getUtility();
                else rightEU = Constants.PROB_INTENDED * u[i+1][j].getUtility() +
                        Constants.PROB_RIGHT_ANGLE * u[i][j-1].getUtility() +
                        Constants.PROB_RIGHT_ANGLE * u[i][j+1].getUtility();

                String optimalAction = getOptimalAction(upEU, downEU, leftEU, rightEU);
                u[i][j].setAction(optimalAction);
            }
        }
        return u;
    }

    public String getOptimalAction(double upEU, double downEu, double leftEU, double rightEU) {
        if (upEU > downEu && upEU > leftEU && upEU > rightEU) return "UP";
        if (downEu > upEU && downEu > leftEU && downEu > rightEU) return "DOWN";
        if (leftEU > downEu && leftEU > upEU && leftEU > rightEU) return "LEFT";
        return "RIGHT";
    }
}
