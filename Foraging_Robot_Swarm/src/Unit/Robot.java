package Unit;

import Processing.Controller;
import Setup.Position;
import Setup.Settings;

/**
 * @author Nico
 */
public class Robot {
    
    private final Controller controller;

    public Position foragePostion;
    public Position position;
    public float clusterDensity;
    public boolean laden;
    public int ladenCount;
    public int state;
    public float pickUpDensity;    
    protected int carryType;
        
    public Robot(Controller controller) {
        this.controller = controller;
        this.position = new Position(controller.settings.GridSize);
        this.state = Settings.CLUSTER;
        this.carryType = Settings.EMPTY;
        this.clusterDensity = 0.0f;
        this.laden = false;
        this.ladenCount = 0;
    }
    
    public void update() {
        switch (state) {
            case Settings.FORAGE:
                controller.forage.update(this);
                break;
            case Settings.CLUSTER:
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
        laden = false;

        if (type == Settings.EMPTY) {
            
            carryType = type;
            laden = false;
            pickUpDensity = 0.0f;
            
        } else {
            
            laden = true;
            pickUpDensity = density;
            
            if (ant) {                
                carryType = type;
            } else {
                carryType = type;
                
            }
        }
    }
}
