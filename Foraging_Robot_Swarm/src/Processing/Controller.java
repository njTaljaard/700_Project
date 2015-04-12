package Processing;

import Setup.Settings;
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
    
    public Controller() {}
        
    @Override
    public void run() {
        int itterations = 0;
        
        setup();
        
        do {
            itterations++;
        
            for (Robot robot : robots) {
                robot.update();
            }
            
        } while (testStoppingCondition());
    }
    
    private void setup() {
        grid = new Grid();
        forage = new Forage(this);
        cluster = new Cluster(this);
        
        for (int i = 0; i < Settings.RobotCount; i++) {            
            robots[i] = new Robot(this);
        }
    }
    
    private boolean testStoppingCondition() {
        if (!grid.complete()) {
            return true;
        }
        
        return false;
    }
}
