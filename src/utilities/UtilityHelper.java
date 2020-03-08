package utilities;

import environment.State;
import environment.Utility;

public class UtilityHelper {

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

    public static Utility getOptimalUtility(double upEU, double downEU, double leftEU, double rightEU) {
        if (upEU > downEU && upEU > leftEU && upEU > rightEU) return new Utility("UP", upEU);
        if (downEU > upEU && downEU > leftEU && downEU > rightEU) return new Utility("DOWN", downEU);
        if (leftEU > downEU && leftEU > upEU && leftEU > rightEU) return new Utility("LEFT", leftEU);
        return new Utility("RIGHT", rightEU);
    }
}
