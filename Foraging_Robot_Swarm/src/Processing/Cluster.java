package Processing;

import Setup.Position;
import Setup.Settings;
import java.util.ArrayList;
import java.util.Random;

/**
 * @author Nico
 */
public class Cluster {
    private Controller controller;
    
    public Cluster(Controller controller) {
        this.controller = controller;        
    }
    
    
    
    
    
    /*private final PickDrop pickDrop;
    private final float gamma_1, gamma_2;
    private final Random r;
    
    public Cluster(Controller controller) {
        this.pickDrop = new PickDrop(controller);
        this.controller = controller;
        this.gamma_1 = 0.5f;
        this.gamma_2 = 0.5f;
        this.r = new Random();
    }
    
    //Store pick up location density to determine if higher location found.......??????
    public void update(Robot robot) {
        
        Position p = controller.grid.movement.getAntNewPosition(robot); //Get new position
        robot.position.row = p.row;
        robot.position.column = p.column;
        
        ArrayList<Position> surround = pickDrop.getSurrounding(robot); //Get surrounding positions
        
        robot.clusterDensity = pickDrop.computeDensity(robot, surround); //Compute density from surrounds
   
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
    
    
    
    private float computePickPropability(float density) {
        
        return (float) Math.pow(gamma_1 / ( gamma_1 + density ), 2);
        
    }
    
    private float computeDropPropability(float density) {
        
        return density < gamma_2 ? 2 * density : 1;
        
    }
    
    private float random() {
        
        return r.nextFloat();
        
    }*/
}
