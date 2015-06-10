package Robot;

import Processing.Controller;
import Setup.RobotState;
import Setup.Settings;
import java.util.ArrayList;
import java.util.Random;

/**
 * @author Nico
 */
public class BeeBot {
    
    private Controller controller;
    private Position baringVector;
    
    private int state;
    private int ladenCount;
    private int forageCount;
    private int carry;  
    
    private boolean laden;
    private boolean dancing;
    private boolean employed;
    
    private float gamma_1 = 0.5f;
    private float gamma_2 = 0.5f;
    
    public BeeBot(Controller controller) {
        this.controller = controller;
        this.employed = controller.utils.getRandom() > 0.5;
    }
    
    public Position update(Robot bot, Position position) {
        return getNewPosition(bot, position);
    }
    
    private Position getNewPosition(Robot bot, Position position) {
        Position pos = null;
        
        switch(state) {
            case RobotState.Bee_WAIT :
                pos = _wait(bot, position);
                        
                break;
            case RobotState.Bee_SCOUT :
                pos = _scout(position);
                
                break;
            case RobotState.Bee_FORAGE :
                pos = _forage(position);
                
                break;
            case RobotState.Bee_SOLO_FORAGE :
                pos = _solo_forage(position);               
                
                break;
            case RobotState.Bee_DANCE :
                pos = _dance(bot, position);
                
                break;
        }
                        
        if (pos != null) 
            return position;
        
        return pos;
    }
    
    public void setCarry(Position pos, int type) {
        if (laden) {
            pos.dropDensity = pos.currentDensity;
            pos.pickupDensity = 0;
        } else {
            pos.pickupDensity = pos.currentDensity;
            pos.dropDensity = 0;
        }
        
        carry = type;
        ladenCount = 0;
        laden = !laden;
    }
        
    public int getState() {
        return state;
    }
    
    public int getCarry() {
        return carry;
    }
    
    public boolean getLaden() {
        return laden;
    }
    
    public Position getBareing() {
        return baringVector;
    }
    
    /*
     * Movement functions
     */
    
    private Position _wait(Robot bot, Position position) {
        
        ArrayList<Robot> dances = controller.grid.dancers;
        
        if (dances != null) {
            
            for (Robot r : dances) {
                                
                if (controller.utils.distance(position, r.getPosition().row, r.getPosition().column) < 4) {
                    
                    baringVector = r.getBareing();
                    state = RobotState.Bee_FORAGE;
                    return getNewPosition(bot, position);
                }
            }
        }   
        
        return null;
    }
    
    private Position _scout(Position position) {
        Position pos = getHighestDensity(controller.grid.getOptions(position, laden), 
                controller.grid.getAllSurrounding(position, laden, carry));
        
        if (controller.grid.getPoint(pos) != Settings.EMPTY) {
            
            if (computePickPropability(pos.currentDensity) > 0.5) {
                
                int c = controller.grid.pickUpItem(pos, laden);
                
                if (c != Settings.EMPTY) {
                    setCarry(pos, c);
                }
            }
        }
        
        return pos;
    }
    
    private Position _forage(Position position) {
        
        
        return new Position(controller.settings.GridSize);
    }
    
    private Position _solo_forage(Position position) {
        
        
        return new Position(controller.settings.GridSize);
    }
    
    private Position _dance(Robot bot, Position position) {
        if (dancing) {
            controller.grid.dancers.remove(position);
            dancing = false;
            state = RobotState.Bee_FORAGE;
            return getNewPosition(bot, position);
        } else {
            //determine solo forage...
            if (false) { // solo forage
                state = RobotState.Bee_SOLO_FORAGE;
                return getNewPosition(bot, position);
            } else {
                controller.grid.dancers.add(bot);
                dancing = true;
            }
        }
        
        return position;
    }
    
    private Position getHighestDensity(ArrayList<Position> options, ArrayList<Position> area) {
        Position pos = null;
        float tmp = 0;
        float tmp2;
        
        for (Position opt : options) {
            tmp2 = controller.grid.getDensity(opt, area);
            opt.currentDensity = tmp2;
            
            if (tmp2 > tmp) {
                pos = opt;
                tmp = tmp2;
            }
        }
        
        if (pos != null) {
            return pos;
        } else {
            return options.get((int) (controller.utils.getRandom() * options.size()));
        }
    }
    
    private double computePickPropability(double density) {
        
        return Math.pow(gamma_1 / ( gamma_1 + density ), 2);
        
    }
    
    private double computeDropPropability(double density) {
        
        return density < gamma_2 ? 2 * density : 1;
        
    }
    
    /*
    private Position getLowestDensity(ArrayList<Position> options, ArrayList<Position> area) {
        Position pos = null;
        float tmp = Float.MAX_VALUE;
        float tmp2;
        
        for (Position opt : options) {
            tmp2 = controller.grid.getDensity(opt, area);
            opt.currentDensity = tmp2;
            
            if (tmp2 > 0) {
                
                if (tmp2 < tmp) {
                    
                    pos = opt;
                    tmp = tmp2;
                }
            }
        }
        
        if (pos != null) {
            
            return pos;
        } else {
            
            int r = (int) (new Random(System.nanoTime()).nextDouble() * options.size());
            return options.get(r);
        }
    }*/
}
