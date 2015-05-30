package Robot;

import Processing.Cluster;
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
    private Cluster cluster;
    private Position position;
    
    private int state;
    private int carry;
    private int ladenCount;  
    
    private boolean laden;
    
    private float gamma_1 = 0.5f;
    private float gamma_2 = 0.5f;
    
    public AntBot(Controller controller) {
        this.controller = controller;
    }
    
    public void update() {
        position = getNewPosition();
    }
    
    private Position getNewPosition() {
        
        ArrayList<Position> options = controller.grid.getOptions(position, laden);

        if (!options.isEmpty()) {
            
            ArrayList<Position> area = controller.grid.getAllSurrounding(position, laden, carry);
            
            if (!area.isEmpty()) {
                
                switch (state) {
                    case RobotState.Ant_SEARCH :
                        position = _search(options, area);
                        break;
                    case RobotState.Ant_CARRY :
                        position = _carry(options, area);
                        break;
                }
            }
            
            int r = (int) (controller.utils.getRandom() * options.size());
            Collections.shuffle(options, new Random(System.nanoTime()));

            if (laden) {
                controller.grid.grid[position.row][position.column] = Settings.EMPTY;
                controller.grid.grid[options.get(r).row][options.get(r).column] = carry;
            }

            return options.get(r);
        }
                        
        return position;
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
    
    private Position _search(ArrayList<Position> option, ArrayList<Position> area) {
        Position tmp = testAntDensity(option, area);
        
        
        
        return tmp;
    }
    
    private Position _carry(ArrayList<Position> option, ArrayList<Position> area) {
        Position tmp = testAntDensity(option, area);
        
        
        
        return tmp;
    }
    
    private Position testAntDensity(ArrayList<Position> options, ArrayList<Position> area) {        
        Position pos = null;
        float tmp;
        float tmp2;
        boolean test = true;
        
        if (laden) {
            tmp = 0.0f;
        } else {
            tmp = Float.MAX_VALUE;
        }
        
        for (Position opt : options) {
            tmp2 = getDensity(opt, area);
            opt.currentDensity = tmp2;
             
            if (laden) {
                if (tmp2 > tmp) {
                    pos = opt;
                    tmp = tmp2;
                    test = false;
                }
            } else {
                if (tmp2 != 0) {
                    if (tmp2 < tmp) {
                        pos = opt;
                        tmp = tmp2;
                        test = false;
                    }
                }
            }
        }
        
        if (test) {
            long seed = System.nanoTime();
            Random r = new Random(seed);
            Collections.shuffle(options, r);
            return options.get(0);
        }
        
        return pos;
    }
    
    public float getDensity(Position pos, ArrayList<Position> area) {
        float alpha = (float) 0.80;
        float lamda = (float) (1 / controller.grid.grid.length);
        float tmp = 0.0f;
                
        for (Position test : area) {
            
            tmp += ( controller.utils.distance(pos, test.row, test.column) / alpha );
                 
        }
        
        tmp *= (1 / Math.pow(controller.grid.grid.length, 2));
          
        if (tmp < 0)
            return 0;
        else 
            return tmp;
    }
}
