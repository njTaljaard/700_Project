package Processing;

import Setup.Position;
import Setup.Settings;
import java.util.ArrayList;

/**
 * @author Nico
 */
public class PickDrop {
    
    /*private final Controller controller;
    private final float alpha = 0.8f;
    
    public PickDrop(Controller col) {
        controller = col;
    }
    
    public ArrayList<Position> getSurrounding(Robot robot) {
        int xStart  = wrap(-5 + robot.position.row);
        int yStart  = wrap(-5 + robot.position.column);
        int xEnd    = wrap(5 + robot.position.row);
        int yEnd    = wrap(5 + robot.position.column);
        ArrayList<Position> sur = new ArrayList<>();
        
        for (int i = xStart; i <= xEnd; i++) {
            
            for (int j = yStart; j <= yEnd; j++) {
                
                if (robot.laden) {
                    if (robot.getCarry() == Settings.ANT_GOLD || robot.getCarry() == Settings.BEE_GOLD) {

                        if (!(i == robot.position.row && j == robot.position.column) && 
                                (controller.grid.grid[i][j] == Settings.GOLD || 
                                controller.grid.grid[i][j] == Settings.ANT_GOLD || 
                                controller.grid.grid[i][i] == Settings.BEE_GOLD)) {

                            sur.add(new Position(i, j));
                        }
                    } else if (robot.getCarry() == Settings.ANT_ROCK || robot.getCarry() == Settings.BEE_ROCK) {
                        if (!(i == robot.position.row && j == robot.position.column) && 
                                (controller.grid.grid[i][j] == Settings.ROCK ||
                                controller.grid.grid[i][j] == Settings.BEE_ROCK || 
                                controller.grid.grid[i][j] == Settings.ANT_ROCK)) {

                            sur.add(new Position(i, j));
                        }
                    }
                    
                } else {
                    if (!(i == robot.position.row && j == robot.position.column) && 
                            (controller.grid.grid[i][j] == Settings.GOLD || 
                            controller.grid.grid[i][j] == Settings.ROCK)) {

                        sur.add(new Position(i, j));
                    }
                }
            }            
        }
        
        return sur;
    }
    
    private int wrap(int x) {
        if (x < 0) {
            return 0;
        } else if (x >= controller.grid.grid.length) {
            return controller.grid.grid.length - 1;
        } else {
            return x;
        }
    }
    
    public float computeDensity(Robot robot, ArrayList<Position> surrounding) {
        float lamda = (float) (1 / Math.pow(controller.grid.grid.length, 2));
        
        float tmp = 0.0f;
        
        for (Position pos : surrounding) {
            
            tmp += ( 1 - distance(robot.position, pos.row, pos.column) / alpha );
            
        }
        
        lamda *= tmp;
        
        return lamda < 0.0f ? 0.0f : lamda;
    }
    
    private float distance(Position ya, int ybX, int ybY) {
        
        return (float) Math.abs(Math.sqrt(Math.pow(Math.abs(ybX - ya.row), 2) + Math.pow(Math.abs(ybY - ya.column), 2)));
        
    }*/
}
