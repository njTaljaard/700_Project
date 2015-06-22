package Robot;

import Processing.Controller;
import Setup.RobotState;
import Setup.Settings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * @author Nico
 */
public class AntBot {
    
    private Controller controller;
    
    private int state;
    private int carry;
    private int ladenCount;  
    
    private boolean laden;
    
    private float gamma_1 = 0.5f;
    private float gamma_2 = 0.5f;
    
    public AntBot(Controller controller) {
        this.state = RobotState.Ant_SEARCH;
        this.controller = controller;
    }
    
    public Position update(Position position) {
        return getNewPosition(position);
    }
    
    private Position getNewPosition(Position position) {
        
        ArrayList<Position> options = controller.grid.getOptions(position, laden);
        Position tmp = null;
           
        if (!options.isEmpty()) {
            
            ArrayList<Position> area = controller.grid.getAllSurrounding(position, laden, carry);
            
            int r = (int) (controller.utils.getRandom() * options.size());
            //Collections.shuffle(options, new Random(System.nanoTime()));

            if (!area.isEmpty()) {
                
                switch (state) {
                    case RobotState.Ant_SEARCH :
                        tmp = _search(options, area);
                        
                        if (tmp == position) {
                            return options.get(r);
                        }
                        
                        break;
                    case RobotState.Ant_CARRY :
                        tmp = _carry(options, area);
                        break;
                }
            
                if (laden) {
                    controller.grid.setPoint(position, Settings.EMPTY);

                    if (tmp == position) {
                        
                        controller.grid.setPoint(options.get(r), carry);
                        return tmp;
                    } else if (tmp != null) {
                        
                        controller.grid.setPoint(tmp, carry);
                        return tmp;
                    }
                }
            }
            
            return options.get(r);
        }
                        
        return position;
    }
    
    public void setCarry(Position pos, int type) {
        if (laden) {
            pos.dropDensity = pos.currentDensity;
            pos.pickupDensity = 0;
            laden = false;
        } else {
            pos.pickupDensity = pos.currentDensity;
            pos.dropDensity = 0;
            laden = true;
        }
        
        carry = type;
        ladenCount = 0;
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
    
    private Position _search(ArrayList<Position> option, ArrayList<Position> area) {
        Position tmp = getLowestDensity(option, area);
        
        if (controller.grid.getPoint(tmp) != Settings.EMPTY) {
            
            if (computePickPropability(tmp.currentDensity) > 0.5) {
                
                int c = controller.grid.pickUpItem(tmp, laden);
                
                if (c != Settings.EMPTY) {
                    setCarry(tmp, c);
                }
            }
        }
        
        return tmp;
    }
    
    private Position _carry(ArrayList<Position> option, ArrayList<Position> area) {
        
        Position tmp = getHighestDensity(option, area);
        ladenCount++;
        
        if (ladenCount > 10) {
            
            if (controller.grid.dropItem(tmp, carry)) {
                
                setCarry(tmp, Settings.EMPTY);
            }
        }
        
        if (controller.grid.getPoint(tmp) != Settings.EMPTY) {
            
            if (tmp.currentDensity > tmp.pickupDensity && computeDropPropability(tmp.currentDensity) > 0.65) {
                                
                if (controller.grid.dropItem(tmp, carry)) {
                    setCarry(tmp, Settings.EMPTY);
                }
            }
        }
        
        return tmp;
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
            return options.get((int) (new Random(System.nanoTime()).nextDouble() * options.size()));
        }
    }
    
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
    }
    
    private double computePickPropability(double density) {
        
        return Math.pow(gamma_1 / ( gamma_1 + density ), 2);
        
    }
    
    private double computeDropPropability(double density) {
        
        return density < gamma_2 ? 2 * density : 1;
        
    }
}
