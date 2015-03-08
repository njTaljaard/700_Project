package Unit;

import Processing.Cluster;
import Processing.Controller;
import Processing.Forage;
import Setup.Position;
import Setup.Settings;

/**
 * @author Nico
 */
public class Robot {
    
    private final Controller controller;

    public Position foragePostion;
    public Position position;
    public int state;
        
    public Robot(Controller controller) {
        this.controller = controller;
        this.position = new Position();
        this.state = Settings.FORAGE;
    }
    
    public void update() {
        switch (state) {
            case Settings.FORAGE:
                controller.forage.update(this);
                break;
            case Settings.CLUSTER:
                controller.cluser.update(this);
                break;
        }
    }    
    
    
}
