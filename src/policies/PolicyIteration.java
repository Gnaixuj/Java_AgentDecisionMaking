package policies;

import environment.Maze;
import environment.State;
import environment.Utility;
import utilities.Constants;

public class PolicyIteration {

    public static void PIteration(Maze m) {
        boolean converge = false;
        State[][] mazeState = m.getMazeState();
        Utility[][] mazeUtility = m.getMazeUtility();
//        while (!converge) {
        for (int i = 0; i < 250; i++) { // tbc
            Utility[][] newUtility = policyEvaluation(mazeState, mazeUtility);
            newUtility = policyImprovement(mazeState, newUtility);
            updateAction(mazeUtility, newUtility); // tbc
//            converge = convergenceTest(mazeUtility, newUtility); // u want the new action but keep the old utility; make a copy of mazeState?
//            if (!converge) {
//                updateAction(mazeUtility, newUtility);
//            }
        }
    }

    // newCurStateUtility - updated utility of state s after going thru the Bellman Equation
    public static Utility[][] policyEvaluation(State[][] s, Utility[][] u) {
        Utility[][] newUtility = new Utility[Constants.NUM_OF_COLS][Constants.NUM_OF_ROWS];
        Utility upU, downU, leftU, rightU;
        double newCurStateUtility, optimalUtility;

        for (int i = 0; i < Constants.NUM_OF_COLS; i++) {
            for (int j = 0; j < Constants.NUM_OF_ROWS; j++) {
                newUtility[i][j] = new Utility();
                if (s[i][j].isWall()) continue;
                upU = getNextStateUtility(s, u, i, j, i, j-1);
                downU = getNextStateUtility(s, u, i, j, i, j+1);
                leftU = getNextStateUtility(s, u, i, j, i-1, j);
                rightU = getNextStateUtility(s, u, i, j, i+1, j);

                switch(u[i][j].getAction()) {
                    case ("UP"): {
//                        if (j == 0 || s[i][j-1].isWall()) optimalUtility = upU.getUtility(); // stay put
//                        else optimalUtility = calculateUtility(upU, leftU, rightU);
                        optimalUtility = calculateUtility(upU, leftU, rightU);
                        break;
                    }
                    case ("DOWN"): {
//                        if (j == 5 || s[i][j+1].isWall()) optimalUtility = downU.getUtility(); // stay put
//                        else optimalUtility = calculateUtility(downU, leftU, rightU);
                        optimalUtility = calculateUtility(downU, leftU, rightU);
                        break;
                    }
                    case ("LEFT"): {
//                        if (i == 0 || s[i-1][j].isWall()) optimalUtility = leftU.getUtility(); // stay put
//                        else optimalUtility = calculateUtility(leftU, upU, downU);
                        optimalUtility = calculateUtility(leftU, upU, downU);
                        break;
                    }
                    case ("RIGHT"): {
//                        if (i == 5 || s[i+1][j].isWall()) optimalUtility = rightU.getUtility(); // stay put
//                        else optimalUtility = calculateUtility(rightU, upU, downU);
                        optimalUtility = calculateUtility(rightU, upU, downU);
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

    public static Utility[][] policyImprovement(State[][] s, Utility[][] u) {
        double upEU, downEU, leftEU, rightEU;
        Utility upU, downU, leftU, rightU;
        for (int i = 0; i < Constants.NUM_OF_COLS; i++) {
            for (int j = 0; j < Constants.NUM_OF_ROWS; j++) {
                if (s[i][j].isWall()) continue;
                upU = getNextStateUtility(s, u, i, j, i, j-1);
                downU = getNextStateUtility(s, u, i, j, i, j+1);
                leftU = getNextStateUtility(s, u, i, j, i-1, j);
                rightU = getNextStateUtility(s, u, i, j, i+1, j);

//                if (j == 0 || s[i][j-1].isWall()) upEU = upU.getUtility();
//                else upEU = calculateUtility(upU, leftU, rightU);
                upEU = calculateUtility(upU, leftU, rightU);

//                if (j == 5 || s[i][j+1].isWall()) downEU = u[i][j].getUtility();
//                else downEU = calculateUtility(downU, leftU, rightU);
                downEU = calculateUtility(downU, leftU, rightU);

//                if (i == 0 || s[i-1][j].isWall()) leftEU = u[i][j].getUtility();
//                else leftEU = calculateUtility(leftU, upU, downU);
                leftEU = calculateUtility(leftU, upU, downU);

//                if (i == 5 || s[i+1][j].isWall()) rightEU = u[i][j].getUtility();
//                else rightEU = calculateUtility(rightU, upU, downU);
                rightEU = calculateUtility(rightU, upU, downU);

                String optimalAction = getOptimalAction(upEU, downEU, leftEU, rightEU);
                u[i][j].setAction(optimalAction); // do i still need to update utility?
            }
        }
        return u;
    }

    public static String getOptimalAction(double upEU, double downEU, double leftEU, double rightEU) {
        if (upEU > downEU && upEU > leftEU && upEU > rightEU) return "UP";
        if (downEU > upEU && downEU > leftEU && downEU > rightEU) return "DOWN";
        if (leftEU > downEU && leftEU > upEU && leftEU > rightEU) return "LEFT";
        return "RIGHT";
    }

    public static boolean convergenceTest(Utility[][] oldU, Utility[][] newU) {
        for (int i = 0; i < Constants.NUM_OF_COLS; i++) {
            for (int j = 0; j < Constants.NUM_OF_ROWS; j++) {
                if (!(oldU[i][j].getAction().equals(newU[i][j].getAction()))) return false;
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
        PIteration(m);
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
    }
}
