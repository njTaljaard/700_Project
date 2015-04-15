package Setup;

import Unit.Robot;
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
    
    public Utilities() {}
    
    public void writeGrid(int[][] grid, Settings settings, String IT) {
        System.out.println(IT);
        try {
            File file = new File("./Grid/" + settings.GridSize + "-" 
                    + settings.RobotCount + "-" + settings.coverage + "-" 
                    + settings.ratio + "-" + settings.scatterType + "-" + IT + ".txt");
            
            boolean create = file.createNewFile();
            boolean write = file.canWrite();

            if (create && write) {
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
            } else {
                System.out.println("creat write error");
            }
        } catch (IOException ex) {
            Logger.getLogger(Utilities.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("shit");
        }
    }
    
    public void writeRobots(Robot[] bots, Settings config, String IT) {
        int[][] grid = new int[config.GridSize][config.GridSize];
        
        for (int i = 0; i < config.GridSize; i++) {
            for (int j = 0; j < config.GridSize; j++) {
                grid[i][j] = 0;
            }                
        }
        
        for (Robot bot : bots) {
            grid[bot.position.row][bot.position.column] = Settings.ANT;
        }
        
        try {
            File file = new File("./Robots/" + IT + ".txt");
            
            boolean create = file.createNewFile();
            boolean write = file.canWrite();

            if (create && write) {
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
            } else {
                System.out.println("creat write error");
            }
        } catch (IOException ex) {
            Logger.getLogger(Utilities.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("shit");
        }
    }
    
    public int getNextUniform(Settings settings) {
        return random.nextInt(settings.GridSize);
    }
    
    public int getNextGausion(Settings settings) {
        return (int) (random.nextGaussian() * settings.GridSize);
    }
}
