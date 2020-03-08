package policies;

import environment.Maze;
import environment.State;
import environment.Utility;
import utilities.Constants;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ValueIteration {

    private static int noOfIterations = 0;
    private static List<Utility[][]> utilityList;

    public static void VIteration(Maze m) {
        double maxChange;
        boolean converge = false;
        State[][] mazeState = m.getMazeState();
        utilityList = new ArrayList<>();

        while (!converge) {
            noOfIterations++;
            Utility[][] mazeUtility = m.getMazeUtility();
            maxChange = 0;
            Utility[][] newUtility = valueIteration(mazeState, mazeUtility);
            maxChange = calcMaxChange(mazeUtility, newUtility, maxChange);
            converge = convergenceTest(maxChange);
            m.setMazeUtility(newUtility);
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
            }
        }

        cloneUtilityArray(u, newUtility);
        utilityList.add(newUtility);

        for (int i = 0; i < Constants.NUM_OF_COLS; i++) {
            for (int j = 0; j < Constants.NUM_OF_ROWS; j++) {
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

    public static void cloneUtilityArray(Utility[][] source, Utility[][] destination) {
        for (int i = 0; i < Constants.NUM_OF_COLS; i++) {
            for (int j = 0; j < Constants.NUM_OF_ROWS; j++) {
                destination[i][j].setAction(source[i][j].getAction());
                destination[i][j].setUtility(source[i][j].getUtility());
            }
        }
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
        if (upEU > downEU && upEU > leftEU && upEU > rightEU) return new Utility("UP", upEU);
        if (downEU > upEU && downEU > leftEU && downEU > rightEU) return new Utility("DOWN", downEU);
        if (leftEU > downEU && leftEU > upEU && leftEU > rightEU) return new Utility("LEFT", leftEU);
        return new Utility("RIGHT", rightEU);
    }

    public static double calcMaxChange(Utility[][] oldU, Utility[][] newU, double maxChange) {
        double absDiff;
        for (int i = 0; i < Constants.NUM_OF_COLS; i++) {
            for (int j = 0; j < Constants.NUM_OF_ROWS; j++) {
                absDiff = Math.abs(newU[i][j].getUtility() - oldU[i][j].getUtility());
                if (absDiff > maxChange) return absDiff;
            }
        }
        return maxChange;
    }

    public static boolean convergenceTest(double maxChange) {
        if (maxChange < Constants.E * (1 - Constants.DISCOUNT_FACTOR) / Constants.DISCOUNT_FACTOR) return true;
        return false;
    }

    public static void main(String[] args){
        Maze m = new Maze();
        VIteration(m);
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
