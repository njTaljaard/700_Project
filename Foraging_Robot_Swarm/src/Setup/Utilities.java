package Setup;

import Robot.Position;
import Robot.Robot;
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
        try {
            File file = new File("./Grid/" + settings.GridSize + "-" 
                    + settings.RobotCount + "-" + settings.coverage + "-" 
                    + settings.ratio + "-" + settings.scatterType + "-" + IT + ".txt");
            
            boolean create = file.createNewFile();
            boolean write = file.canWrite();

            if (create && write) {
                BufferedWriter writer = new BufferedWriter(new FileWriter(file));
                String line = "-------------------------------------------------------------\n";
                writer.write(line);

                for (int i = 0; i < grid.length; i++) {
                    line = "|";
                    for (int j = 0; j < grid[i].length; j++) {
                        line += grid[i][j] + "|";
                    }
                    writer.write(line + "\n");
                }

                writer.close();
                
                int x = 0;
                int y = 0;

                for (int i = 0; i < grid.length; i++) {
                    for (int j = 0; j < grid[i].length; j++) {
                        if (grid[i][j] == Settings.GOLD || grid[i][j] == Settings.ANT_GOLD)
                            x++;
                        if (grid[i][j] == Settings.ROCK || grid[i][j] == Settings.ANT_ROCK)
                            y++;
                    }
                }
                
            } else {
                System.out.println("creat write error " + IT);
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
                        
            if (bot.getLaden()) {
                grid[bot.getPosition().row][bot.getPosition().column] = bot.getCarry();
            } else {
                grid[bot.getPosition().row][bot.getPosition().column] = Settings.ANT;
            }
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
        int tmp;
        
        while ((tmp = random.nextInt(settings.GridSize)) <= 0) {}
        
        return tmp;
    }
    
    public int getNextGausion(Settings settings) {
        
        double tmp = Math.abs(random.nextGaussian());
        while (tmp > 1) 
            tmp -= 1;
            
        tmp = tmp * settings.GridSize;
        
        return (int) tmp;
    }
    
    public int getBetween(int start, int end, Settings settings) {
        int tmp = 0;
        
        while (true) {
            tmp = (int) (random.nextDouble() * settings.GridSize);

            if (tmp >= start && tmp <= end) {
                break;
            }
        }
        
        return tmp;
    }
    
    public double distance(Position ya, int ybX, int ybY) {        
        return Math.sqrt(Math.pow(Math.abs(ya.row - ybX), 2) + Math.sqrt(Math.pow(Math.abs(ya.column - ybY), 2)));
    }
    
    public double getRandom() {
        double r = Math.abs(new Random(System.nanoTime()).nextGaussian());
        while (r > 1) r--;
        
        return r;
    }
    
    public int wrap(int x, int length) {
        if (x < 0) {
            return 0;
        } else if (x >= length) {
            return length - 1;
        } else {
            return x;
        }
    }
}
