package Setup;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Nico
 */
public class Utilities {
    private static final Random random = new Random();
    
    public static void writeGrid(int[][] grid) {
        try {
            File file = new File("./Grid/" + Settings.GridSize + "-" 
                    + Settings.RobotCount + "-" + Settings.coverage + "-" 
                    + Settings.ratio + "-" + Settings.scatterType);
            
            boolean flag = file.createNewFile();
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            
            String line = "----------------------------\n";
            writer.write(line);

            for (int i = 0; i < grid.length; i++) {
                line = "|";

                for (int j = 0; j < grid[i].length; j++) {
                    line += grid[i][j] + "|";
                }

                writer.write(line + "\n");
            }
            
            writer.close();
        } catch (IOException ex) {
            Logger.getLogger(Utilities.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static int getNextUniform() {
        return random.nextInt(Settings.GridSize);
    }
    
    public static int getNextGausion() {
        return (int) (random.nextGaussian() * Settings.GridSize);
    }
}
