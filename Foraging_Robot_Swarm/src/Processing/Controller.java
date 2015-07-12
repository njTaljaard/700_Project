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
    
    public final String ID;
    private int lastCarryItt;
    public int itterations;
    
    public boolean done;
    private boolean print;
    private boolean preCluster;
    
    /* Stats values */
    public int test;
    public int totalWaited = 0;
    
    public int totalForagedGold = 0;
    public int totalPlacedGold = 0;
    public int ittGoldFinished = 0;
    
    public int totalForagedRock = 0;
    public int totalPlacedRock = 0;
    public int ittRockFinished = 0;
    /****************/
    
    public Controller(Settings settings, int id, boolean print, boolean preCluster, int test) {
        this.settings = settings;
        this.utils = new Utilities();
        this.ID = String.valueOf(id);
        this.test = test;
        this.done = false;
        this.print = print;
        this.preCluster = preCluster;
    }
        
    @Override
    public void run() {
        
        /*System.out.println("Start " + settings.GridSize + "-" 
                + settings.RobotCount + "-" + settings.coverage + "-" 
                + settings.ratio + "-" + settings.scatterType+ "-" 
                + settings.weight + "-" + ID);//*/
        
        setup();
        totalPlacedGold = grid.countGold();
        totalPlacedRock = grid.countRock();
        //utils.writeRobots(robots, settings, ID);
        
        /*if (print) {
            System.out.println("Grid Size: " + grid.grid.length + 
                    "\nGold: " + Settings.GOLD + " Rock: " + Settings.ROCK + 
                    "\nAntGold: " + Settings.ANT_GOLD + " AntRock: " + Settings.ANT_ROCK);
        } */
        
        itterations = 0;
        lastCarryItt = 0;
        
        do {
            itterations++;
        
            for (Robot robot : robots) {
                robot.update(itterations);
            }
            
            /*if (print) {
                if ((itterations % 100 == 0)){
                    System.out.println("Iteration " + String.valueOf(itterations));
                    System.out.println("\tRemainding objects : " + String.valueOf(grid.countRemainder()));
                    utils.writeGrid(grid.grid, settings, String.valueOf(itterations));
                    //utils.writeRobots(robots, settings, String.valueOf(itterations));
                }
            }*/
            
            if (ittGoldFinished == 0 && grid.countGold() == 0)
                ittGoldFinished = itterations;
            
            if (ittRockFinished == 0 && grid.countRock() == 0) 
                ittRockFinished = itterations;
            
        } while (testStoppingCondition());
            
        /*if (print) {
            utils.writeGrid(grid.grid, settings, "DONE");
            System.out.println("Done... " + String.valueOf(itterations) + " " + String.valueOf(grid.countRemainder()));
        } else {
            System.out.println("Cluster done...");
        }*/
        
        System.out.println(ID + "\t\t" + String.valueOf(itterations) 
                + " " + String.valueOf(grid.countRemainder()) + "\t" + settings.GridSize + "-" 
                + settings.RobotCount + "-" + settings.coverage + "-" 
                + settings.ratio + "-" + settings.scatterType+ "-" 
                + settings.weight + " " + test);
        
        totalForagedGold = totalPlacedGold - grid.countGold();
        totalForagedRock = totalPlacedRock - grid.countRock();
        
        if (ittGoldFinished != 0)
            ittGoldFinished = itterations;
        
        if (ittRockFinished != 0)
            ittRockFinished = itterations;
        
        utils.writeState(this, settings);
        
        this.done = true;        
        return;
    }
    
    private void setup() {
        
        if (preCluster) {
            settings.scatterType = 1;
            //System.out.println("Create grid : " + settings.scatterType);
            this.grid = new Grid(settings, utils);
        } else {
            //System.out.println("Create grid : " + settings.scatterType);
            this.grid = new Grid(settings,utils);
        }
        
        this.robots = new Robot[settings.RobotCount];
        
        for (int i = 0; i < settings.RobotCount; i++) {  
            if (preCluster)
                this.robots[i] = new Robot(this, RobotState.ANT);
            else
                this.robots[i] = new Robot(this, RobotState.BEE);
        }
    }
    
    private boolean testStoppingCondition() {
        
        updateCarryItt();
        
        if (preCluster) 
            return !testStagnation() && itterations < 20000;
        else
            return !grid.complete() && !testStagnation() && itterations < 100000;
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
    
    /**
     * Side line
     */
    public void preCluster() {
        itterations = 0;
        lastCarryItt = 0;
        
        setup();
        //utils.writeRobots(robots, settings, ID);
        
        //System.out.println("PreCluster " + grid.countRemainder());
        //utils.writeGrid(grid.grid, settings, "PreCluster");
        
        do {
            itterations++;
        
            for (Robot robot : robots) {
                robot.update(itterations);
            }
            
            /*if (print) {
                if (itterations % 100 == 0) {
                    utils.writeGrid(grid.grid, settings, String.valueOf(itterations) + " cluster");
                }
            }*/
            
        } while (testStoppingCondition());
        
        grid.clear();
            
        //utils.writeGrid(grid.grid, settings, "PreCluster Done");
        //System.out.println("PreCluster Done " + grid.countRemainder());
        this.done = true;
    }
}
