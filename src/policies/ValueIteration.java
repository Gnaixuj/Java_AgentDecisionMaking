package policies;

import environment.Maze;
import environment.State;
import environment.Utility;
import utilities.Constants;
import utilities.UtilityHelper;

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

        UtilityHelper.cloneUtilityArray(u, newUtility);
        utilityList.add(newUtility);

        for (int i = 0; i < Constants.NUM_OF_COLS; i++) {
            for (int j = 0; j < Constants.NUM_OF_ROWS; j++) {
                if (s[i][j].isWall()) continue;
                upU = UtilityHelper.getNextStateUtility(s, u, i, j, i, j-1);
                downU = UtilityHelper.getNextStateUtility(s, u, i, j, i, j+1);
                leftU = UtilityHelper.getNextStateUtility(s, u, i, j, i-1, j);
                rightU = UtilityHelper.getNextStateUtility(s, u, i, j, i+1, j);

                upEU = UtilityHelper.calculateUtility(upU, leftU, rightU);
                downEU = UtilityHelper.calculateUtility(downU, leftU, rightU);
                leftEU = UtilityHelper.calculateUtility(leftU, upU, downU);
                rightEU = UtilityHelper.calculateUtility(rightU, upU, downU);

                Utility optimalU = UtilityHelper.getOptimalUtility(upEU, downEU, leftEU, rightEU);
                newCurStateUtility = s[i][j].getReward() + Constants.DISCOUNT_FACTOR * optimalU.getUtility();
                newUtility[i][j].setUtility(newCurStateUtility);
                newUtility[i][j].setAction(optimalU.getAction());
            }
        }
        return newUtility;
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
