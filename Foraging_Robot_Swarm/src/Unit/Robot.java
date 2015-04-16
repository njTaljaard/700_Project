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
    
    public void setCarry(int type, boolean ant) {
        if (type == Settings.EMPTY) {
            carryType = type;
            ladenCount = 0;
            laden = false;
        } if (ant) {
            if (type == Settings.GOLD) {
                carryType = Settings.ANT_GOLD;
                laden = true;
            } else {
                carryType = Settings.ANT_ROCK;
                laden = true;
            }
        } else {
            if (type == Settings.GOLD) {
                carryType = Settings.BEE_GOLD;
                laden = true;
            } else {
                carryType = Settings.BEE_ROCK;
                laden = true;
            }
        }
    }
}
