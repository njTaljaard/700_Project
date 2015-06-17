package Robot;

import Processing.Controller;
import Setup.RobotState;
import Setup.Settings;
import java.util.ArrayList;
import java.util.Collections;

/**
 * @author Nico
 */
public class BeeBot {
    
    private Controller controller;
    private Position baringVector;
    private int bareCount = 0;
    
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
        this.employed = controller.utils.getRandom() > 0.45;
        
        if (controller.utils.getRandom() > 0.45) {
            this.state = RobotState.Bee_SCOUT;
        } else {
            this.state = RobotState.Bee_WAIT;
        }
    }
    
    public Position update(Robot bot, Position position) {
        return getNewPosition(bot, position);
    }
    
    private Position getNewPosition(Robot bot, Position position) {
        Position pos = null;
        
        switch(state) {
            case RobotState.Bee_WAIT :
                //System.out.println("Waiting...");
                pos = _wait(bot, position);
                        
                break;
            case RobotState.Bee_SCOUT :
                pos = _scout(position);
                System.out.println(this.toString() + " Scout " + position.print() + " -> " + pos.print());
                
                break;
            case RobotState.Bee_FORAGE :
                pos = _forage(bot, position);
                
                if (pos != null)
                    System.out.println("Forage " + position.print() + " -> " + pos.print());
                
                break;
            case RobotState.Bee_DANCE :
                pos = _dance(bot, position);
                System.out.println("Dance " + pos.print());
                
                break;
        }
        
        return pos;
    }
    
    public void setCarry(Position pos, int type) {
        if (type == Settings.EMPTY && laden) {
            pos.pickupDensity = 0;
            laden = false;
        } else {
            pos.pickupDensity = pos.currentDensity;
            laden = true;
        }
        
        carry = type;
        ladenCount = 0;
        forageCount = 0;
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
                                
                //if (controller.utils.distance(position, r.getPosition().row, r.getPosition().column) < 4) {
                if (controller.utils.getRandom() > 0.6) {                    
                    baringVector = r.getBareing();
                    state = RobotState.Bee_FORAGE;
                    return getNewPosition(bot, position);
                }
            }
        }   
        
        return null;
    }
    
    private Position _scout(Position position) {
        Position pos = getHighestDensity(position, controller.grid.getOptions(position, laden), 
                controller.grid.getAllSurrounding(position, laden, carry));
        
        if (pos == null)
            return position;
                
        if (controller.grid.getPoint(pos) != Settings.EMPTY) {
            
            if (computePickPropability(pos.currentDensity) > 0.1) {
                
                setCarry(pos, controller.grid.pickUpItem(pos, false));
                pos.pickupDensity = pos.currentDensity;
                this.baringVector = pos;
                this.state = RobotState.Bee_FORAGE;
                System.out.println("PICK-UP");
            }
        }
        
        return pos;
    }
    
    private Position _forage(Robot bot, Position position) {
        Position pos = null;
        if (laden) {
            
            pos = moveToHome(position);
            baringVector.bareCount++;
            
            System.out.println("POS " + pos.print());
            
            if (pos.column == 0) {
                System.out.println("Home base");
                if (controller.grid.dropItem(pos, Settings.EMPTY)) {
                    System.out.println("Drop");
                    setCarry(pos, Settings.EMPTY);
                    
                    if (controller.utils.getRandom() > 0.4) {
                        
                        if (employed) {
                            controller.grid.dancers.add(bot);
                        }
                    }
                }
            }           
        } else {
            
            bareCount++;            
            if (bareCount > (baringVector.bareCount * 1.2)) {
                
                this.state = RobotState.Bee_SCOUT;
                bareCount = 0;               
            } else {
                
                pos = moveToBare(position);
            
                if (controller.grid.getPoint(pos) != Settings.EMPTY) {
                    
                    if (computePickPropability(pos.currentDensity) > 0.5) {
                
                        setCarry(pos, controller.grid.pickUpItem(pos, false));
                    }
                }
            }
        }        
        
        return pos;
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
    
    private Position getHighestDensity(Position p, ArrayList<Position> options, ArrayList<Position> area) {
        Position pos = null;
            
        if (options.size() > 0) {
            float tmp = (float) p.pickupDensity;
            float tmp2;
                        
            for (Position opt : options) {
                
                tmp2 = controller.grid.getDensity(opt, area);
                opt.currentDensity = tmp2;
                
                if (tmp2 > tmp) {
                    pos = opt;
                    tmp = tmp2;
                    System.out.println(pos.currentDensity);
                }
            }

            if (pos != null) {
                return pos;
            } else {
                Collections.shuffle(options);
                return options.get((int) (controller.utils.getRandom() * options.size()));
            }
        }
        
        return pos;
    }
    
    private Position moveToHome(Position position) {
        ArrayList<Position> options = controller.grid.getOptions(position, laden);
        Position pos = null;
        double dist = Double.MAX_VALUE;
        double tmp;
        
        for (Position p : options) {
            
            tmp = controller.utils.distance(p, p.row, 0);
            
            if (tmp < dist) {
                
                dist = tmp;
                pos = p;
            }
        }
        
        return pos;
    }
    
    private Position moveToBare(Position position) {
        ArrayList<Position> options = controller.grid.getOptions(position, laden);
        Position pos = null;
        
        double dist = Double.MAX_VALUE;
        double tmp;
        
        for (Position p : options) {
            
            tmp = controller.utils.distance(p, baringVector.row, baringVector.column);
            
            if (tmp < dist) {
                
                dist = tmp;
                pos = p;
            }
        }
        
        forageCount++;
            
        if (forageCount > 25) {

            if (controller.grid.getPoint(pos) != Settings.EMPTY) {

                if (computePickPropability(pos.currentDensity) > 0.5) {

                    int c = controller.grid.pickUpItem(pos, false);

                    if (c != Settings.EMPTY) {
                        setCarry(pos, c);
                    }
                }
            }
        }
        
        return pos;
    }
    
    private double computePickPropability(double density) {
        
        return Math.pow(gamma_1 / ( gamma_1 + density ), 2);
        
    }
    
    private double computeDropPropability(double density) {
        
        return density < gamma_2 ? 2 * density : 1;
        
    }
}
