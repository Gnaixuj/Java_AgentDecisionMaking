package policies;

import environment.Maze;
import environment.State;
import environment.Utility;
import utilities.Constants;

public class ValueIteration {
    private static final double error = 0.1;
    private static int noOfIteration = 0;

    public static void VIteration(Maze m) {
        boolean converge = false;
        State[][] mazeState = m.getMazeState();
        Utility[][] oldUtility = m.getMazeUtility();
        while (!converge) {
            noOfIteration++;
            Utility[][] newUtility = valueIteration(mazeState, oldUtility);
            converge = convergenceTest(oldUtility, newUtility);
            updateAction(oldUtility, newUtility);
        }

    }

    public static Utility[][] valueIteration(State[][] s, Utility[][] u) {
        Utility[][] newUtility = new Utility[Constants.NUM_OF_COLS][Constants.NUM_OF_ROWS];
        double upEU, downEU, leftEU, rightEU;
        Utility upU, downU, leftU, rightU;
        double newCurStateUtility;

        for (int i = 0; i < Constants.NUM_OF_COLS; i++) {
            for (int j = 0; j < Constants.NUM_OF_ROWS; j++) {
                newUtility[i][j] = new Utility();
                if (s[i][j].isWall()) continue;
                upU = getNextStateUtility(s, u, i, j, i, j-1);
                downU = getNextStateUtility(s, u, i, j, i, j+1);
                leftU = getNextStateUtility(s, u, i, j, i-1, j);
                rightU = getNextStateUtility(s, u, i, j, i+1, j);

                upEU = calculateUtility(upU, leftU, rightU);
                downEU = calculateUtility(downU, leftU, rightU);
                leftEU = calculateUtility(leftU, upU, downU);
                rightEU = calculateUtility(rightU, upU, downU);

                Utility optimalU = getOptimalUtility(upEU, downEU, leftEU, rightEU);
                newCurStateUtility = s[i][j].getReward() + Constants.DISCOUNT_FACTOR * optimalU.getUtility();
                newUtility[i][j].setUtility(newCurStateUtility);
                newUtility[i][j].setAction(optimalU.getAction());
            }
        }
        return newUtility;
    }

    public static Utility getNextStateUtility(State[][] s, Utility[][] u, int curCol, int curRow, int nextCol, int nextRow) {
        if (nextCol < 0 || nextCol > 5 || nextRow < 0 || nextRow > 5) return u[curCol][curRow];
        if (s[nextCol][nextRow].isWall()) return u[curCol][curRow];
        return u[nextCol][nextRow];
    }

    public static double calculateUtility(Utility intended, Utility side1, Utility side2) {
        return Constants.PROB_INTENDED * intended.getUtility() +
                Constants.PROB_RIGHT_ANGLE * side1.getUtility() +
                Constants.PROB_RIGHT_ANGLE * side2.getUtility();
    }

    public static Utility getOptimalUtility(double upEU, double downEU, double leftEU, double rightEU) {
        Utility u = new Utility();
        if (upEU > downEU && upEU > leftEU && upEU > rightEU) {
            u.setUtility(upEU);
            u.setAction("UP");
        }
        else if (downEU > upEU && downEU > leftEU && downEU > rightEU) {
            u.setUtility(downEU);
            u.setAction("DOWN");
        }
        else if (leftEU > downEU && leftEU > upEU && leftEU > rightEU) {
            u.setUtility(leftEU);
            u.setAction("LEFT");
        }
        else {
            u.setUtility(rightEU);
            u.setAction("RIGHT");
        }
        return u;
    }

    public static boolean convergenceTest(Utility[][] oldU, Utility[][] newU) {
        double absDiff;
        for (int i = 0; i < Constants.NUM_OF_COLS; i++) {
            for (int j = 0; j < Constants.NUM_OF_ROWS; j++) {
                absDiff = Math.abs(newU[i][j].getUtility() - oldU[i][j].getUtility());
                if (absDiff > error * (1 - Constants.DISCOUNT_FACTOR) / Constants.DISCOUNT_FACTOR) return false;
            }
        }
        return true;
    }

    public static void updateAction(Utility[][] oldU, Utility[][] newU) {
        for (int i = 0; i < Constants.NUM_OF_COLS; i++) {
            for (int j = 0; j < Constants.NUM_OF_ROWS; j++) {
                oldU[i][j].setUtility(newU[i][j].getUtility());
                if (!(oldU[i][j].getAction().equals(newU[i][j].getAction()))) {
                    oldU[i][j].setAction(newU[i][j].getAction());
                }
            }
        }
    }

    public static void main(String[] args){
        Maze m = new Maze();
        VIteration(m);
        State[][] s = m.getMazeState();
        Utility[][] u = m.getMazeUtility();
        for (int i = 0; i < Constants.NUM_OF_COLS; i++) {
            for (int j = 0; j < Constants.NUM_OF_ROWS; j++) {
                if (s[j][i].isWall()) System.out.print(" WALL ");
                else System.out.print(" " + u[j][i].getAction() + " ");
            }
            System.out.println();
        }

        for (int i = 0; i < Constants.NUM_OF_COLS; i++) {
            for (int j = 0; j < Constants.NUM_OF_ROWS; j++) {
                System.out.print(" " + u[j][i].getUtility() + " ");
            }
            System.out.println();
        }

        System.out.println(noOfIteration);
    }
}
