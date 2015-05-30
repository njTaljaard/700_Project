package Robot;

import Processing.Controller;
import Setup.RobotState;
import java.util.ArrayList;

/**
 * @author Nico
 */
public class BeeBot {
    
    private Controller controller;
    private Position position;
    private Position baringVector;
    private ArrayList<Position> signal;
    
    private int state;
    private int ladenCount;
    private int forageCount;
    private int carryType;  
    
    private boolean laden;
    private boolean employed;
    
    public BeeBot(Controller controller) {
        this.controller = controller;
        this.signal = new ArrayList<Position>();
        this.employed = controller.utils.getRandom() > 0.5;
    }
    
    public void update() {
        position = getNewPosition();
    }
    
    private Position getNewPosition() {
        Position pos = new Position(controller.settings.GridSize);
        
        switch(state) {
            case RobotState.Bee_SCOUT :
                position = _scout();
                //test pickup
                break;
            case RobotState.Bee_FORAGE :
                position = _forage();
                //test drop
                break;
            case RobotState.Bee_SOLO_FORAGE :
                position = _solo_forage();
                //test drop
                break;
            case RobotState.Bee_WAIT :
                if (_wait()) {
                    //got signalled
                }
                break;
            case RobotState.Bee_DANCE :
                
                break;
        }
                        
        return pos;
    }
    
    public Position getPosition() {
        return position;
    }
    
    public int getState() {
        return state;
    }
    
    public boolean getLaden() {
        return laden;
    }
    
    /*
     * Movement functions
     */
    
    private boolean _wait() {
        
        for (int i = 0; i < signal.size(); i++) {
            if (controller.utils.distance(position, signal.get(i)) < 4.0f) {
                state = RobotState.Bee_FORAGE;
                // set baring vector
                return true;
            }
        }
        
        return false;
    }
    
    private Position _scout() {
        
        
        return new Position(controller.settings.GridSize);
    }
    
    private Position _forage() {
        
        
        return new Position(controller.settings.GridSize);
    }
    
    private Position _solo_forage() {
        
        
        return new Position(controller.settings.GridSize);
    }
    
    private Position _dance() {
        
        return new Position(controller.settings.GridSize);
    }
}
