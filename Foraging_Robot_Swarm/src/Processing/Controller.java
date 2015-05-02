package Processing;

import Setup.Settings;
import Setup.Utilities;
import Unit.Grid;
import Unit.Robot;

/**
 * @author Nico
 */
public class Controller implements Runnable {
    
    public Forage forage;
    public Cluster cluster;
    private Robot[] robots;
    protected Grid grid;
    public final Settings settings;
    private final Utilities utils;
    
    private final String ID;
    int itterations;
    
    public Controller(Settings settings, int id) {
        this.settings = settings;
        this.utils = new Utilities();
        this.ID = String.valueOf(id);
    }
        
    @Override
    public void run() {
        itterations = 0;
        
        System.out.println("Gold: " + Settings.GOLD + " Rock: " + Settings.ROCK + 
                "\nAntGold: " + Settings.ANT_GOLD + " AntRock: " + Settings.ANT_ROCK);
        
        setup();
        utils.writeRobots(robots, settings, ID);
        
        do {
            itterations++;
        
            for (Robot robot : robots) {
                robot.update();
            }
            
            //if ((itterations % 100 == 0))
                utils.writeGrid(grid.grid, settings, String.valueOf(itterations));
            //utils.writeRobots(robots, settings, String.valueOf(itterations));
        } while (testStoppingCondition());
        
        System.out.println("DONE!!!");
        
        utils.writeGrid(grid.grid, settings, "DONE...");
    }
    
    private void setup() {
        this.grid = new Grid(settings,utils);
        this.forage = new Forage(this);
        this.cluster = new Cluster(this);
        this.robots = new Robot[settings.RobotCount];
        
        for (int i = 0; i < settings.RobotCount; i++) {            
            this.robots[i] = new Robot(this);
        }
    }
    
    private boolean testStoppingCondition() {
        /*if (!grid.complete()) {
            return true;
        }
        
        return false;*/
        return !(itterations == 100000);//grid.isClustered() || !grid.complete();
    }
}
