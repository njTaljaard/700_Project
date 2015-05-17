package Processing;

import Setup.Position;
import Setup.RobotState;
import Setup.Settings;
import Unit.Robot;
import java.util.ArrayList;
import java.util.Random;

/**
 * @author Nico
 */
public class Forage {
    
    private final Controller controller;
    private final PickDrop pickDrop;
    private final ArrayList<Robot> signal;
    private final float gamma_1;
    
    public Forage(Controller controller) {
        this.controller = controller;
        this.pickDrop = new PickDrop(controller);
        this.signal = new ArrayList<>();
        this.gamma_1 = 0.5f;
    }
    
    public void update(Robot robot) {        
        //Remove Signal
        if (signal.contains(robot))
            signal.remove(robot);
            
        //Process state and update
        switch (robot.state) {
            case RobotState.Bee_FORAGE:
                if (robot.laden) {
                    //move to drop off
                    robot.position = controller.grid.movement.moveToSink(robot);
                    
                    if (robot.position.column == 0) {
                        //drop
                        robot.setCarry(Settings.EMPTY, false, 0);                    
                        //signal (recalculate baring vector do not use pick up position)
                        robot.baringVector = new Position(Math.abs(robot.baringVector.row - robot.position.row), 
                                Math.abs(robot.baringVector.column - robot.position.column));
                        signal.add(robot);
                    }
                } else {
                    //follow baring vector
                    robot.position = controller.grid.movement.moveToBare(robot);
                    
                    //Test if found another and pick up
                    if (testForageFound(robot)) {
                        
                        float density = pickDrop.computeDensity(robot, pickDrop.getSurrounding(robot));
                        
                        if (controller.grid.pickUpItem(robot.position.row, robot.position.column, false)) {
                            
                            robot.setCarry(Settings.GOLD, false, density);
                        }
                    }
                    
                    robot.forageCount++;
                    
                    //Follwed bair to long, scout
                    if (robot.forageCount > 20) {
                        robot.state = RobotState.Bee_SCOUT;
                    }
                }
                break;
            case RobotState.Bee_SOLO_FORAGE:
                if (robot.laden) {
                    //move to drop off
                    robot.position = controller.grid.movement.moveToSink(robot);
                
                    if (robot.position.column == 0) {
                        //drop
                        robot.setCarry(Settings.EMPTY, false, 0);
                    }
            
                } else {
                    //follow baring vector
                    robot.position = controller.grid.movement.moveToBare(robot);
                    
                    //Test if found another and pick up
                    if (testForageFound(robot)) {
                        
                        float density = pickDrop.computeDensity(robot, pickDrop.getSurrounding(robot));
                        
                        if (controller.grid.pickUpItem(robot.position.row, robot.position.column, false)) {
                            
                            robot.setCarry(Settings.GOLD, false, density);
                        }
                    }
                    
                    robot.forageCount++;
                    
                    //Follwed bair to long, scout
                    if (robot.forageCount > 20) {
                        robot.state = RobotState.Bee_SCOUT;
                    }
                }
                break;
            case RobotState.Bee_SCOUT:
                robot.position = controller.grid.movement.getNewPosition(robot);
                
                if (controller.grid.grid[robot.position.row][robot.position.column] != Settings.EMPTY) {
                    
                    //Found gold or rock
                    ArrayList<Position> options = pickDrop.getSurrounding(robot);
                    float density = pickDrop.computeDensity(robot, options);
                    long seed = System.nanoTime();
                    Random rand = new Random(seed);
                    float r = (float) Math.abs(rand.nextGaussian());
                    while (r > 1) r--;
                    
                    //Test probability of successful position found
                    if (r > computePickPropability(density)) {
                    
                        //If gold forage
                        if (controller.grid.grid[robot.position.row][robot.position.column] == Settings.GOLD) {
                            
                            //If forage or solo
                            if (computePickPropability(density) > 0.5) {
                                //Forage
                                robot.state = RobotState.Bee_FORAGE;
                                if (controller.grid.pickUpItem(robot.position.row, robot.position.column, false))
                                    robot.setCarry(Settings.GOLD, false, density);   
                                
                            } else {
                                //Solo
                                robot.state = RobotState.Bee_SOLO_FORAGE;
                                
                            }
                        
                        } 
                        //If rock solo carry...
                        if (controller.grid.grid[robot.position.row][robot.position.column] == Settings.ROCK) {  
                        
                        }
                    }
                }
                break;
            case RobotState.Bee_WAIT:
                //Wait for signal within your area
                for (Robot s : signal) {
                    
                    if (Math.abs(s.position.column - robot.position.column) <= 5) {
                    
                        robot.state = RobotState.Bee_FORAGE;
                        robot.baringVector = s.baringVector;
                    }
                }
                    
                break;
        }
    }
    
    private float computePickPropability(float density) {
        
        return (float) Math.pow(gamma_1 / ( gamma_1 + density ), 2);
        
    }
    
    private boolean testForageFound(Robot robot) {
        
        if (controller.grid.grid[robot.position.row][robot.position.column] != Settings.EMPTY) {
            
            //Found gold or rock
            ArrayList<Position> options = pickDrop.getSurrounding(robot);
            float density = pickDrop.computeDensity(robot, options);
            long seed = System.nanoTime();
            Random rand = new Random(seed);
            float r = (float) Math.abs(rand.nextGaussian());
            while (r > 1) r--;

            //Test probability of successful position found
            if (r > computePickPropability(density)) {

                //If gold forage
                if (controller.grid.grid[robot.position.row][robot.position.column] == Settings.GOLD) {
                    return true;
                } 
                //If rock solo carry...
                if (controller.grid.grid[robot.position.row][robot.position.column] == Settings.ROCK) {  
                    return true;                    
                }
            }
        }
        
        return false;
    }
}
