package Unit;

import Processing.Controller;
import Setup.Position;
import Setup.RobotState;
import Setup.Settings;
import java.util.Random;

/**
 * @author Nico
 */
public class Robot {
    
    private final Controller controller;

    public Position position;
    public Position baringVector;
    
    public boolean moveBack;
    public boolean laden;
    
    public int distance;
    public int state;
    public int beeState;
    public int ladenCount;
    public int forageCount;
    private int carryType;
    
    public float pickUpDensity;    
    public float clusterDensity;
        
    public Robot(Controller controller, int robotType) {
        this.controller = controller;
        position = new Position(controller.settings.GridSize);
        state = robotType;
        carryType = Settings.EMPTY;
        clusterDensity = 0.0f;
        laden = false;
        ladenCount = 0;
        forageCount = 0;
        
        if (state == RobotState.BEE) {
            if (getRandom() > 0.5) {
                state = RobotState.EMPLOYED_BEE;
                beeState = RobotState.Bee_SCOUT;
            } else {
                state = RobotState.UNEMPLOYED_BEE;
                beeState = RobotState.Bee_WAIT;
            }            
        }
    }
    
    public void update() {
        switch (state) {
            case RobotState.BEE:
            case RobotState.EMPLOYED_BEE:
            case RobotState.UNEMPLOYED_BEE:
                controller.forage.update(this);
                break;
            case RobotState.ANT:
                controller.cluster.update(this);
                if (laden)
                    ladenCount++;
                break;
        }
    }  
    
    public int getCarry() {
        return carryType;
    }
    
    public void setCarry(int type, boolean ant, float density) {
        
        ladenCount = 0;
        forageCount = 0;
        carryType = type;

        if (type == Settings.EMPTY) {
            
            laden = false;
            
        } else {
            
            laden = true;
            pickUpDensity = density;
        }
    }
    
    public double getRandom() {
        long seed = System.nanoTime();
        Random rand = new Random(seed);
        return rand.nextDouble();
    }
}
