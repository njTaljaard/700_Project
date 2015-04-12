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
    public int state;
    
    protected int carryType;
        
    public Robot(Controller controller) {
        this.controller = controller;
        this.position = new Position();
        this.state = Settings.CLUSTER;
        this.carryType = Settings.EMPTY;
        this.clusterDensity = 0.0f;
        this.laden = false;
    }
    
    public void update() {
        switch (state) {
            case Settings.FORAGE:
                controller.forage.update(this);
                break;
            case Settings.CLUSTER:
                controller.cluster.update(this);
                break;
        }
    }  
    
    public int getCarry() {
        return carryType;
    }
    
    public void setCarry(int type) {
        carryType = type;
    }
}
