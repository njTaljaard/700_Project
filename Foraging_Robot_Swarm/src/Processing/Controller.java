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
    public int clusterITT;
    
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
                file = new File("./Stats/0/" + settings.ratio + "-" + settings.weight + "-" 
                        + settings.scatterType + "-" + settings.coverage + "-"
                        + settings.GridSize + "-" + settings.RobotCount + ".txt");
            } else {
                file = new File("./Stats/1/" + settings.ratio + "-" + settings.weight + "-" 
                        + settings.scatterType + "-" + settings.coverage + "-"
                        + settings.GridSize + "-" + settings.RobotCount + ".txt");
            }

            file.createNewFile();

            System.out.println(settings.ratio + "-" + settings.weight + "-" 
                        + settings.scatterType + "-" + settings.coverage + "-"
                        + settings.GridSize + "-" + settings.RobotCount);
            
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                for (String write : fileOut)
                    writer.write(write + "\n");
            }
        } catch(IOException ex) {
            ex.printStackTrace();
        }
    }
    
    private void simulateConfig() {
                
        itterations = 0;
        lastCarryItt = 0;
        totalWaited = 0;
        
        this.grid = new Grid(settings, utils, this);
        this.grid.createGrid();
            
        if (preCluster) {  
            preClusterAnt(); 
            lastCarryItt = itterations;
        } else {
            itterations = 0;
            lastCarryItt = 0;
            totalWaited = 0;           
        }
        
        this.robots = new Robot[settings.RobotCount];
        
        for (int i = 0; i < settings.RobotCount; i++)
            this.robots[i] = new Robot(this, RobotState.BEE);
        
        totalPlacedGold = grid.countGold();
        totalPlacedRock = grid.countRock();
        
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
        
        int goldTmp = totalPlacedGold;
        int rockTmp = totalPlacedRock;
        
        totalForagedGold = goldTmp - grid.countGold();
        totalForagedRock = rockTmp - grid.countRock();
        
        if (ittGoldFinished != 0)
            ittGoldFinished = itterations;
        
        if (ittRockFinished != 0)
            ittRockFinished = itterations;
        
        if (preCluster)
            itterations += clusterITT;
        
        fileOut.add(itterations + "," + totalWaited +
                "," + totalPlacedGold + "," + totalForagedGold +
                "," + ittGoldFinished + "," + totalPlacedRock +
                "," + totalForagedRock + "," + ittRockFinished);
        //utils.writeState(this, settings);
        
        return;
    }
    
    private boolean testStoppingCondition() {
        
        updateCarryItt();
        
        if (preCluster) 
            return !testStagnation() && itterations < 100000;
        else
            return !grid.complete() && !testStagnation() && itterations < 200000;
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
        
        return (itterations - lastCarryItt) > (grid.grid.length * 400);
    }
    
    /**
     * Pre-cluster
     */
    public void preClusterAnt() { 
        itterations = 0;
        lastCarryItt = 0;
        totalWaited = 0;       
        this.robots = new Robot[settings.RobotCount];
        
        for (int i = 0; i < settings.RobotCount; i++)
            this.robots[i] = new Robot(this, RobotState.ANT);
         
        preCluster = true;
        
        do {
            itterations++;
        
            for (Robot robot : robots) {
                robot.update(itterations);
            }
            
        } while (testStoppingCondition());
        
        preCluster = true;
        
        this.grid.clear();
    }
    
    public void preClusterMap() {
        this.robots = new Robot[settings.RobotCount];
        
        for (int i = 0; i < settings.RobotCount; i++)
            this.robots[i] = new Robot(this, RobotState.ANT);
        
        boolean tmp = preCluster;
        
        preCluster = true;
        
        do {
            itterations++;
            
            for (Robot robot : robots) {
                robot.update(itterations);
            }
            
        } while (testStoppingCondition());
        
        preCluster = tmp;
        
        this.grid.clear();
    }
}
