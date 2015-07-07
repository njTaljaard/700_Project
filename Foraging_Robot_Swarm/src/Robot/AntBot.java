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
    private float gamma_2 = 0.975f;
    
    private int iteration = 0;
    private int avoid = 0;
    
    public AntBot(Controller controller) {
        this.state = RobotState.Ant_SEARCH;
        this.controller = controller;
    }
    
    public Position update(Position position, int it) {        
        iteration = it;
        return getNewPosition(position);
    }
    
    private Position getNewPosition(Position position) {
        
        ArrayList<Position> options = controller.grid.getOptions(position, laden);
        Position tmp = null;
           
        if (!options.isEmpty()) {
            
            ArrayList<Position> area;
            
            int r = (int) (controller.utils.getRandom() * options.size());
            //Collections.shuffle(options, new Random(System.nanoTime()));

            switch (state) {
                case RobotState.Ant_SEARCH :
                    
                    area = controller.grid.getAllSurrounding(position, laden, carry);
                    tmp = _search(position, options, area);

                    if (tmp.row == position.row && tmp.column == position.column) {
                        /*System.out.println("Search Random : \n\t" + controller.grid.getPoint(options.get(r)) 
                                + " : " + position.print() + " -> " + options.get(r).print()
                                + " " + options.get(r).currentDensity);*/
                        return options.get(r);
                    }

                    /*System.out.println("Search Directed : \n\t" + controller.grid.getPoint(tmp) 
                            + " : " + position.print() + " -> " + tmp.print() + " " + tmp.currentDensity);*/

                    controller.grid.setPoint(tmp, carry, true);
                    return tmp;
                    
                case RobotState.Ant_CARRY :
                    
                    int carryTemp = carry;
                    boolean ladenTemp = laden;
                    
                    area = controller.grid.getAllSurrounding(position, laden, carry);                    
                    tmp = _carry(position, options, area);
                    
                    /*System.out.println("Carry : \n\t" + controller.grid.getPoint(tmp) 
                            + " : " + position.print() + " -> " + tmp.print() + " " + tmp.currentDensity);*/
                    
                    if (ladenTemp) {
                        controller.grid.setPoint(position, Settings.EMPTY, true);

                        if (tmp == position) {

                            controller.grid.setPoint(options.get(r), carryTemp, true);
                            options.get(r).pickupDensity = position.pickupDensity;
                            return options.get(r);
                            
                        } else if (tmp != null) {
                            
                            controller.grid.setPoint(tmp, carryTemp, true);
                            tmp.pickupDensity = position.pickupDensity;
                            return tmp;
                        }
                        
                        controller.grid.setPoint(position, carryTemp, true);
                    }
            }
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
    
    private Position _search(Position position, ArrayList<Position> option, ArrayList<Position> area) {
        
        if (avoid > 0) {
            Position tmp = fatherest(position, option);
            
            avoid--;
            return tmp;
        } else {
            
            Position tmp = getLowestDensity(option, area);

            if (controller.grid.getPoint(tmp) != Settings.EMPTY) {
                /*System.out.println("\tTest " + controller.grid.getPoint(tmp) + " " 
                        + computePickPropability(tmp.currentDensity) + " " + tmp.currentDensity);*/
                if (computePickPropability(tmp.currentDensity) < 0.119) {

                    int c = controller.grid.pickUpItem(tmp, true);

                    if (c != Settings.EMPTY) {
                        //System.out.println(this.toString() + " Pick-Up " + tmp.print() + "\t" + iteration);
                        controller.grid.setPoint(tmp, c, true);
                        setCarry(tmp, c);
                        state = RobotState.Ant_CARRY;
                    }
                }
            }

            return tmp;
        }
    }
    
    private Position _carry(Position position, ArrayList<Position> option, ArrayList<Position> area) {
        
        Position tmp = getHighestDensity(position, option, area);
        ladenCount++; 
                    
        if (ladenCount > controller.grid.grid.length) {
            
            //System.out.println("Laden to long " + controller.grid.getPoint(tmp) + " " + carry);
            if (controller.grid.getPoint(tmp) == Settings.EMPTY &&
                    controller.grid.dropItem(tmp, carry)) {
                
                //System.out.println(this.toString() + " Drop Tired " + tmp.print() + "\t" + iteration);
                
                setCarry(tmp, Settings.EMPTY);
                state = RobotState.Ant_SEARCH;
                avoid = (int) (controller.settings.GridSize * 0.1);
                
                return tmp;
            }
        }
        
        if (controller.grid.getPoint(tmp) == Settings.EMPTY) {
            
            /*System.out.println("\tTest2 " + tmp.currentDensity + " " + tmp.pickupDensity 
                    + " " + computeDropPropability(tmp.currentDensity));*/
            if (computeDropPropability(tmp.currentDensity) == 1.0) { //&& tmp.currentDensity > tmp.pickupDensity) {
                
                if (controller.grid.dropItem(tmp, carry)) {
                    
                    //System.out.println(this.toString() + " Drop " + tmp.print() + "\t" + iteration);                    
                    
                    setCarry(tmp, Settings.EMPTY);
                    state = RobotState.Ant_SEARCH;
                    avoid = (int) (controller.settings.GridSize * 0.1);
                    
                    return tmp;
                }
            }
        }
        
        return tmp;
    }
    
    private Position getHighestDensity(Position position, ArrayList<Position> options, ArrayList<Position> area) {
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
        } else if (options.size() > 0) {
            
            Collections.shuffle(options);
            return options.get((int) (controller.utils.getRandom() * options.size()));
        } 
        
        return position;
    }
    
    private Position getLowestDensity(ArrayList<Position> options, ArrayList<Position> area) {
        Position pos = null;
        float tmp = Float.MAX_VALUE;
        float tmp2;
                
        for (Position opt : options) {
            tmp2 = controller.grid.getDensity(opt, area);
            opt.currentDensity = tmp2;
            
            if (tmp2 > 0.0) {
                
                if (tmp2 < tmp) {
                    
                    pos = opt;
                    tmp = tmp2;
                }
            }
        }
        
        if (pos != null) {
            
            return pos;
        } else {
            
            return options.get((int) (controller.utils.getRandom() * options.size()));
        }
    }
    
    public Position fatherest(Position position, ArrayList<Position> options) {
        
        Position tmp = null;
        double dist = 0;
        double dist2 = 0;
        
        for (Position p : options) {
            
            if (controller.grid.getPoint(p) == Settings.EMPTY) {
                
                dist2 = controller.utils.distance(position, p.row, p.column);
                if (dist2 > dist) {
                    dist = dist2;
                    tmp = p;
                }
            }
        }
        
        if (tmp == null) {
            Collections.shuffle(options);
            tmp = options.get((int) (controller.utils.getRandom() * options.size()));
        }
        
        return tmp;
    }
    
    private double computePickPropability(double density) {
        
        return Math.pow(gamma_1 / ( gamma_1 + density ), 2);
        
    }
    
    private double computeDropPropability(double density) {
        
        return density < gamma_2 ? density : 1;
        
    }
}
