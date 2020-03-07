package policies;

import environment.Maze;
import environment.State;
import environment.Utility;
import utilities.Constants;

public class PolicyIteration {

    public static int noOfIterations = 0;

    public static void PIteration(Maze m) {
        boolean converge = false;
        State[][] mazeState = m.getMazeState();

        while (!converge) {
            noOfIterations++;
            Utility[][] mazeUtility = m.getMazeUtility();
            Utility[][] newUtility = policyEvaluation(mazeState, mazeUtility);
            Utility[][] bestUtility = policyImprovement(mazeState, newUtility);
            converge = convergenceTest(mazeState, mazeUtility, newUtility, bestUtility);
            if (!converge) {
                m.setMazeUtility(newUtility);
            }
            System.out.println("NOI: " + noOfIterations); // d
        }
    }

    // newCurStateUtility - updated utility of state s after going thru Value Iteration
    public static Utility[][] policyEvaluation(State[][] s, Utility[][] u) {
        Utility[][] newUtility = new Utility[Constants.NUM_OF_COLS][Constants.NUM_OF_ROWS];
        double newCurStateUtility;
        int k = 0;

        while (k < Constants.K) {
            for (int i = 0; i < Constants.NUM_OF_COLS; i++) {
                for (int j = 0; j < Constants.NUM_OF_ROWS; j++) {
                    newUtility[i][j] = new Utility();
                    if (s[i][j].isWall()) continue;
                    newCurStateUtility = simplfiedBellmanEqn(s, u, i, j);
//                    u[i][j].setUtility(newCurStateUtility);
                    newUtility[i][j].setAction(u[i][j].getAction()); // tbc
                    newUtility[i][j].setUtility(newCurStateUtility);
//                    System.out.println("Debug"); // d
                }
            }
            k++;
//            System.out.println(k); // d
        }
        return newUtility;
    }

    public static double simplfiedBellmanEqn(State[][] s, Utility[][] u, int curCol, int curRow) {
        Utility upU, downU, leftU, rightU;
        double curStateUtility, newCurStateUtility;

        upU = getNextStateUtility(s, u, curCol, curRow, curCol, curRow - 1);
        downU = getNextStateUtility(s, u, curCol, curRow, curCol, curRow + 1);
        leftU = getNextStateUtility(s, u, curCol, curRow, curCol - 1, curRow);
        rightU = getNextStateUtility(s, u, curCol, curRow, curCol + 1, curRow);

        switch (u[curCol][curRow].getAction()) {
            case ("UP"): {
                curStateUtility = calculateUtility(upU, leftU, rightU);
                break;
            }
            case ("DOWN"): {
                curStateUtility = calculateUtility(downU, leftU, rightU);
                break;
            }
            case ("LEFT"): {
                curStateUtility = calculateUtility(leftU, upU, downU);
                break;
            }
            case ("RIGHT"): {
                curStateUtility = calculateUtility(rightU, upU, downU);
                break;
            }
            default:
                curStateUtility = 0;
        }
        newCurStateUtility = s[curCol][curRow].getReward() + Constants.DISCOUNT_FACTOR * curStateUtility;
        return newCurStateUtility;
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
        Utility[][] bestUtility = new Utility[Constants.NUM_OF_COLS][Constants.NUM_OF_ROWS];
        double upEU, downEU, leftEU, rightEU;
        Utility upU, downU, leftU, rightU;

        for (int i = 0; i < Constants.NUM_OF_COLS; i++) {
            for (int j = 0; j < Constants.NUM_OF_ROWS; j++) {
                bestUtility[i][j] = new Utility();
                if (s[i][j].isWall()) continue;
                upU = getNextStateUtility(s, u, i, j, i, j-1);
                downU = getNextStateUtility(s, u, i, j, i, j+1);
                leftU = getNextStateUtility(s, u, i, j, i-1, j);
                rightU = getNextStateUtility(s, u, i, j, i+1, j);

                upEU = calculateUtility(upU, leftU, rightU); // initially will be equal to the cur policy
                downEU = calculateUtility(downU, leftU, rightU);
                leftEU = calculateUtility(leftU, upU, downU);
                rightEU = calculateUtility(rightU, upU, downU);

                Utility optimalUtility = getOptimalUtility(upEU, downEU, leftEU, rightEU);
                bestUtility[i][j].setAction(optimalUtility.getAction());
                bestUtility[i][j].setUtility(optimalUtility.getUtility());
            }
        }
        return bestUtility;
    }

    public static Utility getOptimalUtility(double upEU, double downEU, double leftEU, double rightEU) {
        if (upEU > downEU && upEU > leftEU && upEU > rightEU) return new Utility("UP", upEU);
        if (downEU > upEU && downEU > leftEU && downEU > rightEU) return new Utility("DOWN", downEU);
        if (leftEU > downEU && leftEU > upEU && leftEU > rightEU) return new Utility("LEFT", leftEU);
        return new Utility("RIGHT", rightEU);
    }

    // modified policy iteration
    public static boolean convergenceTest(State[][] s, Utility[][] oldU, Utility[][] newU, Utility[][] bestU) {
        boolean converge = true;
        for (int i = 0; i < Constants.NUM_OF_COLS; i++) {
            for (int j = 0; j < Constants.NUM_OF_ROWS; j++) {
//                if (newU[i][j].getUtility() > oldU[i][j].getUtility()) {
//                    oldU[i][j].setAction(newU[i][j].getAction());
//                    converge = false;
//                }
                if (bestU[i][j].getUtility() > simplfiedBellmanEqn(s, newU, i, j)) {
                    newU[i][j].setAction(bestU[i][j].getAction());
                    converge = false;
                }
//                oldU[i][j].setUtility(newU[i][j].getUtility());
            }
        }
        return converge;
    }

    public static void main(String[] args){
        Maze m = new Maze();
        PIteration(m);
        State[][] s = m.getMazeState();
        Utility[][] u = m.getMazeUtility();

        System.out.println("Optimal Policy: ");
        for (int i = 0; i < Constants.NUM_OF_COLS; i++) {
            for (int j = 0; j < Constants.NUM_OF_ROWS; j++) {
                if (s[j][i].isWall()) System.out.print(" WALL ");
                else System.out.print(" " + u[j][i].getAction() + " ");
            }
            System.out.println();
        }

        System.out.println("State Utilities: ");
        for (int i = 0; i < Constants.NUM_OF_COLS; i++) {
            for (int j = 0; j < Constants.NUM_OF_ROWS; j++) {
                System.out.print(" " + u[j][i].getUtility() + " ");
            }
            System.out.println();
        }
        System.out.println("No. Of Iterations: " + noOfIterations);
    }
}
