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
        this.gamma_1 = 0.8f;
    }
    
    public void update(Robot robot) {
        //Remove Signal
        if (signal.contains(robot)) {
            System.out.println("Remove my signal");
            signal.remove(robot);
        }
        
        Position p;
        //Process state and update
        switch (robot.beeState) {
            
            case RobotState.Bee_WAIT: System.out.println("Wait");
            
                //Wait for signal within your area
                for (Robot s : signal) {
                    
                    if (Math.abs(s.position.column - robot.position.column) <= 5) {
                        System.out.println("I am signaled");
                        robot.beeState = RobotState.Bee_FORAGE;
                        robot.baringVector = s.baringVector;
                    }
                }
                
                break;
                
            case RobotState.Bee_SCOUT: System.out.println("Scout");
             
                p = controller.grid.movement.getBeeNewPosition(robot);
                robot.position.row = p.row;
                robot.position.column = p.column;
                robot.position.dens = p.dens;
                
                if (controller.grid.grid[robot.position.row][robot.position.column] != Settings.EMPTY) {
                        
                    if (controller.grid.pickUpItem(robot.position.row, robot.position.column, false)) {
                        
                        robot.setCarry(controller.grid.grid[robot.position.row][robot.position.column], false, robot.position.dens);   
                        robot.beeState = RobotState.Bee_FORAGE;

                        robot.baringVector = new Position(robot.position.row, robot.position.column);
                        robot.baringVector.distance = getDistance(robot.baringVector, robot.position);
                        robot.pickUpDensity = robot.position.dens;
                        robot.forageCount = 0;
                        System.out.println("PickUp");
                    }
                }
                
                break;
                
            case RobotState.Bee_FORAGE:
            
                if (robot.laden) {
                    System.out.println("Forage laden");
                    //move to drop off
                    System.out.println("Before " + robot.position.row + " " + robot.position.column);
                    p = controller.grid.movement.moveToSink(robot);
                    robot.position.row = p.row;
                    robot.position.column = p.column;
                    robot.position.dens = p.dens;
                    System.out.println("After " + robot.position.row + " " + robot.position.column);
                    
                    if (robot.position.column == 0) { 
                        
                        if (controller.grid.dropItem(robot.position.row, robot.position.column, Settings.EMPTY)) {
                        
                            System.out.println("Drop");
                            robot.setCarry(Settings.EMPTY, false, 0); 
                            robot.baringVector = new Position(Math.abs(robot.baringVector.row - robot.position.row), 
                                    Math.abs(robot.baringVector.column - robot.position.column));

                            //Signal
                            double r = robot.getRandom();
                            if (r > testDanceTime(robot.pickUpDensity) && robot.getCarry() == Settings.BEE_GOLD)
                                signal.add(robot);   
                            
                            if (robot.state == RobotState.EMPLOYED_BEE) {
                                System.out.println("Revert to Scout");
                                robot.beeState = RobotState.Bee_SCOUT;
                                robot.forageCount = 0;

                            } else if (robot.state == RobotState.UNEMPLOYED_BEE) {
                                System.out.println("Revert to Wait");
                                robot.beeState = RobotState.Bee_WAIT;
                                robot.forageCount = 0;

                            }
                        }
                    }
                    
                } else {
                    System.out.println("Forage bare");
                    //follow baring vector
                    System.out.println("Before " + robot.position.row + " " + robot.position.column);
                    p = controller.grid.movement.moveToBare(robot);
                    robot.position.row = p.row;
                    robot.position.column = p.column;
                    robot.position.dens = p.dens;
                    System.out.println("After " + robot.position.row + " " + robot.position.column);
                    
                    //Test if found another and pick up
                    if (controller.grid.grid[robot.position.row][robot.position.column] == Settings.GOLD ||
                            controller.grid.grid[robot.position.row][robot.position.column] == Settings.ROCK) {
                        
                        if (controller.grid.pickUpItem(robot.position.row, robot.position.column, false)) {
                        
                            robot.setCarry(controller.grid.grid[robot.position.row][robot.position.column], false, robot.position.dens);   
                            robot.beeState = RobotState.Bee_FORAGE;

                            robot.baringVector = new Position(robot.position.row, robot.position.column);
                            robot.baringVector.distance = getDistance(robot.baringVector, robot.position);
                            robot.pickUpDensity = robot.position.dens;
                            System.out.println("PickUp");
                            
                            break;
                        }
                    }
                    
                    robot.forageCount++;
                    
                    //Follwed bair to long, scout
                    if (robot.forageCount > 20) {
                        
                        if (robot.state == RobotState.EMPLOYED_BEE) {
                            
                            robot.beeState = RobotState.Bee_SCOUT;
                            
                        } else if (robot.state == RobotState.UNEMPLOYED_BEE) {
                            
                            robot.beeState = RobotState.Bee_WAIT;
                            
                        }
                    }
                }
                break;
        }
    }
    
    private float computePickPropability(float density) {
        
        return (float) Math.pow(gamma_1 / ( gamma_1 + density ), 2);
        
    }
    
    private float testDanceTime(float density) {
        return (float) Math.pow(density / 100, 2);
    }
    
    private int getDistance(Position in1, Position in2) {
        return (int) Math.abs(Math.pow(in1.row - in2.column, 2) + Math.pow(in1.column - in2.column, 2));
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
