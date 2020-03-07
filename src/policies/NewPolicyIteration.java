package policies;

import environment.Maze;
import environment.State;
import environment.Utility;
import utilities.Constants;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class NewPolicyIteration {

    private static int noOfIterations = 0;
    private static List<Utility[][]> utilityList;

    public static void PIteration(Maze m) {
        boolean converge = false;
        State[][] mazeState = m.getMazeState();
        utilityList = new ArrayList<>();

        while (!converge) {
            noOfIterations++;
            Utility[][] mazeUtility = m.getMazeUtility();
            Utility[][] newUtility = policyEvaluation(mazeState, mazeUtility);
            Utility[][] bestUtility = policyImprovement(mazeState, newUtility);
            converge = convergenceTest(mazeState, newUtility, bestUtility);
//            if (!converge) {
//                m.setMazeUtility(newUtility);
//            }
            m.setMazeUtility(newUtility);
            System.out.println("NOI: " + noOfIterations); // d
        }
    }

    public static Utility[][] policyEvaluation(State[][] s, Utility[][] u) {
        Utility[][] newUtility = new Utility[Constants.NUM_OF_COLS][Constants.NUM_OF_ROWS];
        Utility[][] utilityClone = new Utility[Constants.NUM_OF_COLS][Constants.NUM_OF_ROWS];
        double newCurStateUtility;

        int k = 0;
        for (int i = 0; i < Constants.NUM_OF_COLS; i++) {
            for (int j = 0; j < Constants.NUM_OF_ROWS; j++) {
                newUtility[i][j] = new Utility();
                utilityClone[i][j] = new Utility();
            }
        }

        cloneUtilityArray(u, newUtility);
        utilityList.add(newUtility); // To Be Used

        while (k < Constants.K) {
            cloneUtilityArray(newUtility, utilityClone);
            for (int i = 0; i < Constants.NUM_OF_COLS; i++) {
                for (int j = 0; j < Constants.NUM_OF_ROWS; j++) {
                    if (s[i][j].isWall()) continue;
                    newCurStateUtility = simplifiedBellmanEqn(s, utilityClone, i, j);
                    newUtility[i][j].setUtility(newCurStateUtility);
                }
            }
            k++;
        }
        return newUtility;
    }

    public static void cloneUtilityArray(Utility[][] source, Utility[][] destination) {
        for (int i = 0; i < Constants.NUM_OF_COLS; i++) {
            for (int j = 0; j < Constants.NUM_OF_ROWS; j++) {
                destination[i][j].setAction(source[i][j].getAction());
                destination[i][j].setUtility(source[i][j].getUtility());
            }
        }
    }

    public static double simplifiedBellmanEqn(State[][] s, Utility[][] u, int curCol, int curRow) {
        double curStateUtility, newCurStateUtility;

        curStateUtility = getCurStateUtility(s, u, curCol, curRow);
        newCurStateUtility = s[curCol][curRow].getReward() + Constants.DISCOUNT_FACTOR * curStateUtility;
        return newCurStateUtility;
    }

    public static double getCurStateUtility(State[][] s, Utility[][] u, int curCol, int curRow) {
        Utility upU, downU, leftU, rightU;
        double curStateUtility;

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
        return curStateUtility;
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

    public static boolean convergenceTest(State[][] s, Utility[][] newU, Utility[][] bestU) {
        boolean converge = true;

        for (int i = 0; i < Constants.NUM_OF_COLS; i++) {
            for (int j = 0; j < Constants.NUM_OF_ROWS; j++) {
                if (bestU[i][j].getUtility() > getCurStateUtility(s, newU, i, j)) {
                    newU[i][j].setAction(bestU[i][j].getAction());
                    converge = false;
                }
            }
        }
        return converge;
    }

    public static void main(String[] args){
        Maze m = new Maze();
        PIteration(m);
        State[][] s = m.getMazeState();
        Utility[][] u = m.getMazeUtility();
        DecimalFormat df = new DecimalFormat("#.###");
        df.setRoundingMode(RoundingMode.CEILING);

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
                if (u[j][i].getUtility() == 0) System.out.print(" 0.000 ");
                else System.out.print(" " + df.format(u[j][i].getUtility()) + " ");
            }
            System.out.println();
        }
        System.out.println("No. Of Iterations: " + noOfIterations);
    }
}
