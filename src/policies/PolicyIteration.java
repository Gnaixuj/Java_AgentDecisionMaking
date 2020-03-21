package policies;

import environment.Maze;
import environment.State;
import environment.Utility;
import utilities.Constants;
import utilities.DataHelper;
import utilities.UtilityHelper;

import java.util.ArrayList;
import java.util.List;

public class PolicyIteration {

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
            m.setMazeUtility(newUtility);
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

        UtilityHelper.cloneUtilityArray(u, newUtility);
        utilityList.add(newUtility);

        while (k < Constants.K) {
            UtilityHelper.cloneUtilityArray(newUtility, utilityClone);
            for (int i = 0; i < Constants.NUM_OF_COLS; i++) {
                for (int j = 0; j < Constants.NUM_OF_ROWS; j++) {
                    if (s[i][j].isWall()) continue;
                    newCurStateUtility = UtilityHelper.simplifiedBellmanEqn(s, utilityClone, i, j);
                    newUtility[i][j].setUtility(newCurStateUtility);
                }
            }
            k++;
        }
        return newUtility;
    }

    public static Utility[][] policyImprovement(State[][] s, Utility[][] u) {
        Utility[][] bestUtility = new Utility[Constants.NUM_OF_COLS][Constants.NUM_OF_ROWS];
        double upEU, downEU, leftEU, rightEU;
        Utility upU, downU, leftU, rightU;

        for (int i = 0; i < Constants.NUM_OF_COLS; i++) {
            for (int j = 0; j < Constants.NUM_OF_ROWS; j++) {
                bestUtility[i][j] = new Utility();
                if (s[i][j].isWall()) continue;
                upU = UtilityHelper.getNextStateUtility(s, u, i, j, i, j-1);
                downU = UtilityHelper.getNextStateUtility(s, u, i, j, i, j+1);
                leftU = UtilityHelper.getNextStateUtility(s, u, i, j, i-1, j);
                rightU = UtilityHelper.getNextStateUtility(s, u, i, j, i+1, j);

                upEU = UtilityHelper.calculateUtility(upU, leftU, rightU); 
                downEU = UtilityHelper.calculateUtility(downU, leftU, rightU);
                leftEU = UtilityHelper.calculateUtility(leftU, upU, downU);
                rightEU = UtilityHelper.calculateUtility(rightU, upU, downU);

                Utility optimalUtility = UtilityHelper.getOptimalUtility(upEU, downEU, leftEU, rightEU);
                bestUtility[i][j].setAction(optimalUtility.getAction());
                bestUtility[i][j].setUtility(optimalUtility.getUtility());
            }
        }
        return bestUtility;
    }

    public static boolean convergenceTest(State[][] s, Utility[][] newU, Utility[][] bestU) {
        boolean converge = true;

        for (int i = 0; i < Constants.NUM_OF_COLS; i++) {
            for (int j = 0; j < Constants.NUM_OF_ROWS; j++) {
                if (bestU[i][j].getUtility() > UtilityHelper.getCurStateUtility(s, newU, i, j)) {
                    newU[i][j].setAction(bestU[i][j].getAction());
                    converge = false;
                }
            }
        }
        return converge;
    }

    public static void main(String[] args){
        Maze m = new Maze();
        long startTime = System.nanoTime();
        PIteration(m);
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

//        DataHelper.writeToFile(utilityList, "policyIteration");
    }
}
