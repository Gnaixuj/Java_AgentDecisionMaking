package utilities;

import environment.Utility;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class DataHelper {

    public static String roundOff(double n) {
        return String.format("%.3f", n);
    }

    public static void writeToFile(List<Utility[][]> utilList, String fileName) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < Constants.NUM_OF_COLS; i++) {
            for (int j = 0; j < Constants.NUM_OF_ROWS; j++) {
                Iterator<Utility[][]> iter = utilList.iterator();
                while (iter.hasNext()) {
                    Utility[][] util = iter.next();
                    sb.append(roundOff(util[j][i].getUtility()));
                    if (iter.hasNext()) {
                        sb.append(",");
                    }
                }
                sb.append("\n");
            }
        }

        writeToFile(sb.toString().trim(), fileName + ".csv");
    }

    public static void writeToFile(String content, String fileName) {
        try {
            FileWriter fw = new FileWriter(new File(fileName), false);
            fw.write(content);
            fw.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

}

