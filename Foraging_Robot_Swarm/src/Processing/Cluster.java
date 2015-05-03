package Processing;

import Setup.Position;
import Setup.Settings;
import Unit.Robot;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 * @author Nico
 */
public class Cluster {
    private final Controller controller;
    private final float gamma_1, gamma_2, alpha;
    private final Random r;
    
    public Cluster(Controller controller) {
        this.controller = controller;
        this.gamma_1 = 0.5f;
        this.gamma_2 = 0.5f;
        this.alpha = 0.8f;
        this.r = new Random();
    }
    
    //Store pick up location density to determine if higher location found.......??????
    public void update(Robot robot) {
        
        robot.position = controller.grid.movement.getNewPosition(robot); //Get new position
        
        ArrayList<Position> surround = getSurrounding(robot); //Get surrounding positions
        
        robot.clusterDensity = computeDensity(robot, surround); //Compute density from surrounds
   
        int r = controller.grid.grid[robot.position.row][robot.position.column];
        
        if (!robot.laden && (r == Settings.GOLD || r == Settings.ROCK)) { //Not carrying, On gold / rock location
            
            if (random() <= computePickPropability(robot.clusterDensity)) { //Test propability to pick up
                
                if (controller.grid.pickUpItem(robot.position.row, robot.position.column, true)) { //Pick up at point
                    robot.setCarry(controller.grid.grid[robot.position.row][robot.position.column], true, robot.clusterDensity);
                }
            }
            
        } else if (robot.laden) { //Already carrying
            robot.ladenCount++;

            if (robot.ladenCount > 15) { //Been carrying to long, tired

                if (controller.grid.dropItem(robot.position.row, robot.position.column, robot.getCarry())) { //Drop item
                    robot.setCarry(Settings.EMPTY, true, 0.0f);
                }

            } else { //Not tired

                if (robot.clusterDensity > robot.pickUpDensity && random() <= computeDropPropability(robot.clusterDensity)) { //Not tired, test propability to drop

                    if (controller.grid.dropItem(robot.position.row, robot.position.column, robot.getCarry())) { //Drop at point
                        robot.setCarry(Settings.EMPTY, true, 0.0f);
                    }
                }
            }
        }
    }
    
    private ArrayList<Position> getSurrounding(Robot robot) {
        ArrayList<Position> tmp = new ArrayList<>();
        
        for (int i = -5; i <= 5; i++) {
            
            for (int j = -5; j <= 5; j++) {
            
                int tmpRow = i + robot.position.row;
                int tmpCol = j + robot.position.column;
                
                if (robot.laden) {
                    if (robot.getCarry() == Settings.ANT_GOLD || robot.getCarry() == Settings.BEE_GOLD) {

                        if (!(i == 0 && j == 0) && wrap(tmpRow, tmpCol) && 
                                (controller.grid.grid[tmpRow][tmpCol] == Settings.GOLD || 
                                controller.grid.grid[tmpRow][tmpCol] == Settings.ANT_GOLD || 
                                controller.grid.grid[tmpRow][tmpCol] == Settings.BEE_GOLD)) {

                            tmp.add(new Position(tmpRow, tmpCol));
                        }
                    } else if (robot.getCarry() == Settings.ANT_ROCK || robot.getCarry() == Settings.BEE_ROCK) {
                        if (!(i == 0 && j == 0) && wrap(tmpRow, tmpCol) && 
                                (controller.grid.grid[tmpRow][tmpCol] == Settings.ROCK ||
                                controller.grid.grid[tmpRow][tmpCol] == Settings.BEE_ROCK || 
                                controller.grid.grid[tmpRow][tmpCol] == Settings.ANT_ROCK)) {

                            tmp.add(new Position(tmpRow, tmpCol));
                        }
                    }
                } else {
                    if (!(i == 0 && j == 0) && wrap(tmpRow, tmpCol) && 
                            (controller.grid.grid[tmpRow][tmpCol] == Settings.GOLD ||
                            controller.grid.grid[tmpRow][tmpCol] == Settings.ROCK ||
                            controller.grid.grid[tmpRow][tmpCol] == Settings.ANT_GOLD || 
                            controller.grid.grid[tmpRow][tmpCol] == Settings.ANT_ROCK)) {

                        tmp.add(new Position(tmpRow, tmpCol));
                    }       
                }
            }            
        }
        
        return tmp;
    }
    
    private float computeDensity(Robot robot, ArrayList<Position> surrounding) {
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
        
    }
    
    private float computePickPropability(float density) {
        
        return (float) Math.pow(gamma_1 / ( gamma_1 + density ), 2);
        
    }
    
    private float computeDropPropability(float density) {
        
        return density < gamma_2 ? 2 * density : 1;
        
    }
    
    private boolean wrap(int p1, int p2) {
        return p1 < controller.grid.settings.GridSize && p2 < controller.grid.settings.GridSize && p1 >= 0 && p2 >= 0;
    }
    
    private float random() {
        
        return r.nextFloat();
        
    }
}
