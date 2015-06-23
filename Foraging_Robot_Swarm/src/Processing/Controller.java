package Processing;

import Robot.Robot;
import Setup.RobotState;
import Setup.Settings;
import Setup.Utilities;
import Board.Grid;

/**
 * @author Nico
 */
public class Controller implements Runnable {
    
    private Robot[] robots;
    public Grid grid;
    public final Settings settings;
    public final Utilities utils;
    
    private final String ID;
    private int itterations;
    private int lastCarryItt;
    
    public Controller(Settings settings, int id) {
        this.settings = settings;
        this.utils = new Utilities();
        this.ID = String.valueOf(id);
    }
        
    @Override
    public void run() {
        itterations = 0;
        lastCarryItt = 0;
        
        setup();
        //utils.writeRobots(robots, settings, ID);
        
        System.out.println("Grid Size: " + grid.grid.length + 
                "\nGold: " + Settings.GOLD + " Rock: " + Settings.ROCK + 
                "\nAntGold: " + Settings.ANT_GOLD + " AntRock: " + Settings.ANT_ROCK);
        
        System.out.println("Starting positions:");
        for (Robot r : robots)
            System.out.println("\t" + r.getPosition().print());
        
        do {
            itterations++;
        
            for (Robot robot : robots) {
                robot.update();
            }
            
            if ((itterations % 100 == 0)){
                System.out.println("Iteration " + String.valueOf(itterations));
                System.out.println("\tRemainding objects : " + String.valueOf(grid.countRemainder()));
                utils.writeGrid(grid.grid, settings, String.valueOf(itterations));
                //utils.writeRobots(robots, settings, String.valueOf(itterations));
            }
            
        } while (testStoppingCondition());
                
        utils.writeGrid(grid.grid, settings, "DONE");
        System.out.println("Done... " + String.valueOf(itterations) + " " + String.valueOf(grid.countRemainder()));
        System.exit(0);
    }
    
    private void setup() {
        this.grid = new Grid(settings,utils);
        this.robots = new Robot[settings.RobotCount];
        
        for (int i = 0; i < settings.RobotCount; i++) {  
            this.robots[i] = new Robot(this, RobotState.ANT);
            //this.robots[i] = new Robot(this, RobotState.BEE);
        }
    }
    
    private boolean testStoppingCondition() {
        
        updateCarryItt();
        return !testStagnation() && itterations < 100000;//!grid.complete() !grid.isClustered()
    }
    
    public void updateCarryItt() { 
        
        for (Robot r : robots) {
            if (r.getLaden()) {
                lastCarryItt = itterations;
                return;
            }
        }
    }
    
    public boolean testStagnation() {
        
        return (itterations - lastCarryItt) > (grid.grid.length * 20);
    }
}
