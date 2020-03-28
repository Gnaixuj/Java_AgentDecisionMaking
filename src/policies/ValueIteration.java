package policies;

import environment.Maze;
import environment.State;
import environment.Utility;
import utilities.Constants;
import utilities.DataHelper;
import utilities.UtilityHelper;

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
            maxChange = calcMaxChange(mazeState, mazeUtility, newUtility, maxChange);
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

    public static double calcMaxChange(State[][] s, Utility[][] oldU, Utility[][] newU, double maxChange) {
        double absDiff;
        double max = maxChange;
        for (int i = 0; i < Constants.NUM_OF_COLS; i++) {
            for (int j = 0; j < Constants.NUM_OF_ROWS; j++) {
                if (s[i][j].isWall()) continue;
                absDiff = Math.abs(newU[i][j].getUtility() - oldU[i][j].getUtility());
                if (absDiff > max) max = absDiff;
            }
        }
        return max;
    }

    public static boolean convergenceTest(double maxChange) {
        if (maxChange < Constants.E * (1 - Constants.DISCOUNT_FACTOR) / Constants.DISCOUNT_FACTOR) return true;
        return false;
    }

    public static void main(String[] args){
        Maze m = new Maze();
        long startTime = System.nanoTime();
        VIteration(m);
        long elapsedTime = System.nanoTime() - startTime;
        State[][] s = m.getMazeState();
        Utility[][] u = m.getMazeUtility();

        System.out.println("Optimal Policy: ");
        for (int i = 0; i < Constants.NUM_OF_COLS; i++) {
            System.out.print("|");
            for (int j = 0; j < Constants.NUM_OF_ROWS; j++) {
                if (s[j][i].isWall()) System.out.print(" W |");
                else System.out.print(" " + u[j][i].getAction() + " |");
            }
            System.out.println();
        }
        System.out.println("Key: U - UP   | D - DOWN");
        System.out.println("     L - LEFT | R - RIGHT");
        System.out.println("     W - WALL");
        System.out.println();
        System.out.println("State Utilities: ");
        for (int i = 0; i < Constants.NUM_OF_COLS; i++) {
            System.out.print("|");
            for (int j = 0; j < Constants.NUM_OF_ROWS; j++) {
                if (u[j][i].getUtility() == 0) System.out.print(" 00.000 |");
                else if (u[j][i].getUtility() >= 99.9995) System.out.print(" 100.00 |");
                else if (u[j][i].getUtility() < 10) System.out.print(" 0" + DataHelper.roundOff(u[j][i].getUtility()) + " |");
                else System.out.print(" " + DataHelper.roundOff(u[j][i].getUtility()) + " |");
            }
            System.out.println();
        }
        System.out.println();
        System.out.println("No. Of Iterations: " + noOfIterations);
        System.out.println("Time (in ms): " + elapsedTime / 1000000);

//        DataHelper.writeToFile(utilityList, "valueIteration");
    }
}
