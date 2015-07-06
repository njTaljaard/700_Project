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
    
    private float pickup = 0.975f;
    
    private boolean useBaring;
    
    public BeeBot(Controller controller) {
        this.controller = controller;        
        this.baringVector = new Position();
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
                pos = _wait(bot, position);
                //System.out.println("Wait " + pos.print());
                
                break;
            case RobotState.Bee_SCOUT :
                pos = _scout(position);
                //System.out.println("Scout\n\t " + this.toString() + " " + position.print() 
                //        + " -> " + pos.print() + " : " + controller.grid.getPoint(pos));
                
                break;
            case RobotState.Bee_FORAGE :
                pos = _forage(bot, position);
                
                /*if (pos != null) {
                    if (laden)
                        System.out.println("Forage home\n\t " //+ this.toString() + " " 
                                + position.print() + " -> " + pos.print() + " : " 
                                + controller.grid.getPoint(pos));
                    else 
                        System.out.println("Forage bare\n\t " //+ this.toString() + " " 
                                + position.print() + " -> " + pos.print() + " : "
                                + controller.grid.getPoint(pos));
                }*/
                
                break;
            case RobotState.Bee_DANCE :
                pos = _dance(bot, position);
                
                if (pos == null)
                    return position;
                
                //System.out.println("Dance " + pos.print());
                
                break;
        }
        
        if (laden) {
            controller.grid.setPoint(position, Settings.EMPTY, false);

            if (pos != null) {

                controller.grid.setPoint(pos, carry, false);
                return pos;
            }
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
        bareCount = 0;
        useBaring = true;
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
                                
                if (controller.utils.distance(position, r.getPosition().row, r.getPosition().column) < 6) {               
                    
                    baringVector = r.getBareing();
                    state = RobotState.Bee_FORAGE;
                    return getNewPosition(bot, position);
                }
            }
        }   
        
        return position;
    }
    
    private Position _scout(Position position) {
                
        Position pos = getHighestDensity(position, controller.grid.getOptions(position, laden), 
                controller.grid.getAllSurrounding(position, laden, carry));
         
        if (pos == position) {
            return position;
        }

        if (controller.grid.getPoint(pos) != Settings.EMPTY) {
            //System.out.println(computePickPropability(pos.currentDensity));
            if (computePickPropability(pos.currentDensity) > 0.1) {
                
                setCarry(pos, controller.grid.pickUpItem(pos, false));
                pos.pickupDensity = pos.currentDensity;
                
                baringVector = new Position(pos.row, pos.column);
                baringVector.bareCount = 0;
                baringVector.currentDensity = pos.currentDensity;
                
                this.state = RobotState.Bee_FORAGE;
                //System.out.println(this.toString() + " PICK-UP " + pos.print());
            }
        }
        
        return pos;
    }
    
    private Position _forage(Robot bot, Position position) {
        Position pos = null;
        ArrayList<Position> options = controller.grid.getOptions(position, laden);
        
        if (laden) {
            
            pos = moveToHome(position, options);
            baringVector.bareCount++;
            ladenCount++;
            
            if (ladenCount > (controller.grid.grid.length * 1.4)) {
                
                if (pos != null && //controller.grid.getPoint(pos) == Settings.EMPTY &&
                        controller.grid.dropItem(pos, carry)) {
                    
                    //System.out.println("\n" + this.toString() + " Drop - Revert to scouting " + pos.print() + "\n");
                    setCarry(pos, Settings.EMPTY);
                    controller.grid.setPoint(pos, carry, false);
                    controller.grid.setPoint(position, Settings.EMPTY, false);
                    
                    this.state = RobotState.Bee_SCOUT;
                    position.pickupDensity = 0.0;
                    bareCount = 0;
                }
            }
            
            if (pos != null) {

                if (pos.column == 0) {
                    //System.out.println("Home base");
                    if (controller.grid.dropItem(pos, Settings.EMPTY)) {
                        System.out.println(this.toString() + " Drop " + pos.print() + "\n");
                        
                        setCarry(pos, Settings.EMPTY);
                        controller.grid.setPoint(position, Settings.EMPTY, false);
                        useBaring = true;
                        
                        if (position.pickupDensity > 0.5) {

                            if (employed) {
                                state = RobotState.Bee_DANCE;
                            }
                        }
                    }
                } /*else {
                    return pos;
                }*/
                
            }
            
        } else { //MOVE TO BARE...
            
		bareCount++;       
            if (bareCount > (baringVector.bareCount * 1.5)) {
                
                //System.out.println("\nForage - Revert to scout\n");
                this.state = RobotState.Bee_SCOUT;
                position.pickupDensity = 0.0;
                bareCount = 0;
                
            } else {
                
                pos = moveToBare(position, controller.grid.getOptions(position, laden), controller.grid.getSurrounding(position, laden, carry));
                
                if (pos != null && controller.grid.getPoint(pos) != Settings.EMPTY) {
                    
                    if (computePickPropability(pos.currentDensity) > 0.1) {
                
                        setCarry(pos, controller.grid.pickUpItem(pos, false));
                        pos.pickupDensity = pos.currentDensity;
                        
                        baringVector = new Position(pos.row, pos.column);
                        baringVector.bareCount = 0;
                        baringVector.currentDensity = pos.currentDensity;
                        
                        this.state = RobotState.Bee_FORAGE;
                        //System.out.println("PICK-UP");
                    }
                }
            }
        }        
        
        return pos;
    }
    
    private Position _dance(Robot bot, Position position) {
        
        if (dancing) {
            
            controller.grid.dancers.remove(bot);
            dancing = false;
            state = RobotState.Bee_FORAGE;
            
            return getNewPosition(bot, position);
            
        } else {
            
            //determine solo forage...
            /*System.out.println("Test this " + position.pickupDensity);
            if (position.pickupDensity < 0.99) { // solo forage
                
                state = RobotState.Bee_SOLO_FORAGE;
                return getNewPosition(bot, position);
                
            } else {*/
                
                controller.grid.dancers.add(bot);
                dancing = true;
                
            //}
        }
        
        return position;
    }
    
    private Position getHighestDensity(Position p, ArrayList<Position> options, ArrayList<Position> area) {
        Position pos = null;
        
        if (options.size() > 0) {
            float tmp = Float.MIN_VALUE; 
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
                
                Collections.shuffle(options);
                return options.get((int) (controller.utils.getRandom() * options.size()));
            }
        }
        
        return p;
    }
    
    private Position moveToHome(Position position, ArrayList<Position> options) {
        
        ArrayList<Position> use = new ArrayList<>();
        Position pos = null;
        double dist = Double.MAX_VALUE;
        double tmp;
        
        for (Position p : options) {
            
            tmp = controller.utils.distance(p, p.row, 0);
            
            if (tmp < dist) {
                
                dist = tmp;
                pos = p;
                
                use.clear();
                use.add(p);
                
            } else if (tmp == dist) {
                use.add(p);
            }
        }
        
        if (!use.isEmpty()) {
            Collections.shuffle(use);
            pos = use.get((int) (controller.utils.getRandom() * use.size()));
        }
        
        return pos;
    }
    
    private Position moveToBare(Position position, ArrayList<Position> options, ArrayList<Position> area) {

	ArrayList<Position> use = new ArrayList<>();
        Position pos = getHighestDensity(position, options, area);
        
        if (useBaring) {
            
            if (pos.currentDensity > (position.pickupDensity * 0.4)) {
                System.out.println(this.toString() + " Leave bare " + pos.print());
                useBaring = false;
                return pos;

            } else {
                
                double dist = Double.MAX_VALUE;
                double tmp;
                
                for (Position p : options) {
                    
                    tmp = controller.utils.distance(p, baringVector.row, baringVector.column);

                    if (tmp < dist) {
                        
                        dist = tmp;
                        pos = p;

                        use.clear();
                        use.add(p);

                    } else if (tmp == dist) {
                        use.add(p);
                    }
                }

                if (!use.isEmpty()) {
                    Collections.shuffle(use);
                    pos = use.get((int) (controller.utils.getRandom() * use.size()));
                }   
            }      
        } 

        return pos;
	    
        /*Position pos = getHighestDensity(position, options, area);
        
        if (useBaring) {
            
            if (pos.currentDensity > (baringVector.pickupDensity * 0.5)) {
                System.out.println(this.toString() + " Leave bare " + pos.print());
                useBaring = false;
                return pos;

            } else {
                
                ArrayList<Position> use = new ArrayList<>();
                Position spare = pos;
                double dist = Double.MAX_VALUE;
                double tmp;
                
                for (Position p : options) {
                    
                    tmp = controller.utils.distance(p, baringVector.row, baringVector.column);
                    
                    if (tmp < dist) {
                        
                        dist = tmp;
                        pos = p;

                        use.clear();
                        use.add(p);

                    } else if (tmp == dist) {
                        use.add(p);
                    }
                }

                if (use.size() > 1) {
                    Collections.shuffle(use);
                    pos = use.get((int) (controller.utils.getRandom() * use.size()));
                }
            }      
        }
        
        return pos;*/
    }
    
    private double computePickPropability(double density) {
        
        return Math.pow(gamma_1 / ( gamma_1 + density ), 2);
        
    }
}
