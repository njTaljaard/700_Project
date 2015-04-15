package Processing;

import Setup.Position;
import Setup.Settings;
import Unit.Robot;
import java.util.ArrayList;
import java.util.Random;

/**
 * @author Nico
 */
public class Cluster {
    private final Controller controller;
    private boolean clustered;
    private float gamma_1, gamma_2, alpha;
    private Random r;
    
    public Cluster(Controller controller) {
        this.controller = controller;
        this.clustered = false;
        this.gamma_1 = 0.4f;
        this.gamma_2 = 0.7f;
        this.alpha = (float) Math.sqrt(controller.settings.GridSize);
        this.r = new Random();
    }
    
    public void update(Robot robot) {
        if (!clustered) {
            
            //if (robot.clusterDensity == 0.0f)
                robot.clusterDensity = computeDensity(robot);
            
            ArrayList<Position> positions = controller.grid.getSurroundPositions(robot.position);
            
            positions.stream().forEach((pos) -> {
                if (!robot.laden && (controller.grid.grid[pos.row][pos.column] == Settings.ROCK || 
                        controller.grid.grid[pos.row][pos.column] == Settings.GOLD)) {
                    
                    if (random() <= computePickPropability(robot.clusterDensity)) {
                        robot.setCarry(controller.grid.pickUpItem(pos.row, pos.column, 
                                true, Settings.ANT_GOLD == robot.getCarry()), true);
                    }
                    
                } else if (robot.laden && (controller.grid.grid[pos.row][pos.column] != Settings.ROCK || 
                        controller.grid.grid[pos.row][pos.column] != Settings.ROCK)) {
                    
                    if (random() <= computeDropPropability(robot.clusterDensity)) {
                        
                        if (controller.grid.dropItem(pos.row, pos.column, robot.getCarry())) {
                            robot.setCarry(Settings.EMPTY, true);
                        }
                        
                    }
                    
                }
            });
            
            robot.position = controller.grid.movement.getNewPosition(robot.position);
        }
    }
        
    private float computeDensity(Robot robot) {
        float lamda = (float) (1 / Math.pow(controller.grid.grid.length, 2));
        
        float tmp = 0.0f;
        for (int i = 0; i < controller.grid.grid.length; i++) {
            
            for (int j = 0; j < controller.grid.grid[i].length; j++) {
                
                if (robot.getCarry() == Settings.ANT_GOLD) {
                    
                    if (controller.grid.grid[i][j] == Settings.GOLD || 
                            controller.grid.grid[i][j] == Settings.ANT_GOLD ||
                            controller.grid.grid[i][j] == Settings.BEE_GOLD) {

                        tmp += ( 1 - distance(robot.position, i, j) / alpha );

                    }
                    
                } else if (robot.getCarry() == Settings.ANT_ROCK) {                
                    
                    if (controller.grid.grid[i][j] == Settings.ROCK || 
                        controller.grid.grid[i][j] == Settings.ANT_ROCK ||
                        controller.grid.grid[i][j] == Settings.BEE_ROCK) {
                        
                        tmp += ( 1 - distance(robot.position, i, j) / alpha ) / 2;
                        
                    }
                    
                } else {
                    
                    if (controller.grid.grid[i][j] == Settings.GOLD || 
                            controller.grid.grid[i][j] == Settings.ANT_GOLD ||
                            controller.grid.grid[i][j] == Settings.BEE_GOLD) {

                        tmp += ( 1 - distance(robot.position, i, j) / alpha );

                    } else if (controller.grid.grid[i][j] == Settings.ROCK || 
                        controller.grid.grid[i][j] == Settings.ANT_ROCK ||
                        controller.grid.grid[i][j] == Settings.BEE_ROCK) {
                        
                        tmp += ( 1 - distance(robot.position, i, j) / alpha ) / 2;
                        
                    }
                    
                }
                
            }
            
        }
        
        lamda *= tmp;
        
        return lamda < 0.0f ? 0.0f : lamda;
    }
    
    private float distance(Position ya, int ybX, int ybY) {
        
        return (float) Math.sqrt(Math.pow(ybX - ya.row, 2) + Math.pow(ybY - ya.column, 2));
        
    }
    
    private float computePickPropability(float density) {
        
        return (float) Math.pow(gamma_1 / ( gamma_1 + density ), 2);
        
    }
    
    private float computeDropPropability(float density) {
        
        return density < gamma_2 ? 2 * density : 1;
        
    }
    
    private float random() {
        
        return r.nextFloat();
        
    }
}
