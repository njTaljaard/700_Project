package Processing;

import Robot.Robot;
import Setup.RobotState;
import Setup.Settings;
import Setup.Utilities;
import Board.Grid;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

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
    
    public ArrayList<String> fileOut;
    /****************/
    
    public Controller(Settings settings, int id, boolean print, boolean preCluster) {
        this.settings = settings;
        this.utils = new Utilities();
        this.ID = String.valueOf(id);
        this.done = false;
        this.print = print;
        this.preCluster = preCluster;
        this.fileOut = new ArrayList<>();
    }
        
    @Override
    public void run() {
        fileOut.add("Itterations,totalWait,tPlaceGold,tForageGold,ittGoldFinish,tPlaceRock,tForageRock,ittRockFinish");
            /*controller.itterations + "," + controller.totalWaited +
            "," + controller.totalPlacedGold + "," + controller.totalForagedGold +
            "," + controller.ittGoldFinished + "," + controller.totalPlacedRock +
            "," + controller.totalForagedRock + "," + controller.ittRockFinished */
        for (int i = 1; i < 31; i++) {
            this.test = i;
            simulateConfig();
        }
        
        try {
            File file;
            if (preCluster) {
                file = new File("./Stats/0/" + settings.GridSize + "-" 
                        + settings.RobotCount + "-" + settings.coverage + "-" 
                        + settings.ratio + "-" + settings.scatterType+ "-" 
                        + settings.weight + "-" + ID + ".txt");
            } else {
                file = new File("./Stats/1/" + settings.GridSize + "-" 
                        + settings.RobotCount + "-" + settings.coverage + "-" 
                        + settings.ratio + "-" + settings.scatterType+ "-" 
                        + settings.weight + "-" + ID + ".txt");
            }

            file.createNewFile();

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                for (String write : fileOut)
                    writer.write(write + "\n");
            }
        } catch(IOException ex) {
            ex.printStackTrace();
        }
    }
    
    private void simulateConfig() {
        
        if (preCluster) {
            Controller control = new Controller(settings, 0, false, true);
            control.preCluster();

            while(!control.done){}

            grid = control.grid;
            done = false;
            
            this.robots = new Robot[settings.RobotCount];
        
            for (int i = 0; i < settings.RobotCount; i++) {  
                this.robots[i] = new Robot(this, RobotState.BEE);
            }
            
        } else {
            setup();
        }
            
        totalPlacedGold = grid.countGold();
        totalPlacedRock = grid.countRock();
        
        itterations = 0;
        lastCarryItt = 0;
        
        do {
            itterations++;
        
            for (Robot robot : robots) {
                robot.update(itterations);
            }
            
            if (ittGoldFinished == 0 && grid.countGold() == 0)
                ittGoldFinished = itterations;
            
            if (ittRockFinished == 0 && grid.countRock() == 0) 
                ittRockFinished = itterations;
            
        } while (testStoppingCondition());
        
        totalForagedGold = totalPlacedGold - grid.countGold();
        totalForagedRock = totalPlacedRock - grid.countRock();
        
        if (ittGoldFinished != 0)
            ittGoldFinished = itterations;
        
        if (ittRockFinished != 0)
            ittRockFinished = itterations;
        
        fileOut.add(itterations + "," + totalWaited +
                "," + totalPlacedGold + "," + totalForagedGold +
                "," + ittGoldFinished + "," + totalPlacedRock +
                "," + totalForagedRock + "," + ittRockFinished);
        //utils.writeState(this, settings);
        
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
