package Processing;

import Setup.Position;
import Setup.Settings;
import Unit.Grid;
import Unit.Robot;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * @author Nico
 */
public class Movement {
    
    private Grid grid;
    private ArrayList<Position> area;
    private ArrayList<Position> options;
    
    public Movement(Grid grid) {
        this.grid = grid;
        this.area = new ArrayList<>();
        this.options = new ArrayList<>();
    }
    
    public Position getNewPosition(Robot robot) { // Can see 5 units ahead
        area.clear();
        options.clear();
        
        try {
            getOptions(robot.position, robot.laden);
                        
            if (options.isEmpty()) {
                
                //System.out.println("Im stuck " + robot.position.row + " " + robot.position.column);
                
                return robot.position;
                
            } else {
                
                getSurrounding(robot.position, robot.laden, robot.getCarry());

                if (!area.isEmpty()) { // no surrounding gold found       
                    
                    Position move = testDensity(robot); //of open options
                    
                    if (move != null) { //Move to best position
                        
                        if (robot.laden) {
                            grid.grid[robot.position.row][robot.position.column] = Settings.EMPTY;
                            grid.grid[move.row][move.column] = robot.getCarry();
                        }

                        return move;
                    }
                }

                // use random movement

                long seed = System.nanoTime();
                Collections.shuffle(options, new Random(seed));

                if (robot.laden) {
                    grid.grid[robot.position.row][robot.position.column] = Settings.EMPTY;
                    grid.grid[options.get(0).row][options.get(0).column] = robot.getCarry();
                }

                return options.get(0);
            }
        } finally {
            area.clear();
            options.clear();
        }
    }    
    
    private void getSurrounding(Position origin, boolean laden, int carry) {
        
        int xStart  = wrap(-5 + origin.row);
        int yStart  = wrap(-5 + origin.column);
        int xEnd    = wrap(5 + origin.row);
        int yEnd    = wrap(5 + origin.column);
        
        for (int i = xStart; i <= xEnd; i++) {
            
            for (int j = yStart; j <= yEnd; j++) {
            
                /*int tmpRow = i + origin.row;
                int tmpCol = j + origin.column;*/
                
                if (laden) {
                    if (carry == Settings.ANT_GOLD || carry == Settings.BEE_GOLD) {

                        if (!(i == origin.row && j == origin.column) && 
                                (grid.grid[i][j] == Settings.GOLD || 
                                grid.grid[i][j] == Settings.ANT_GOLD || 
                                grid.grid[i][i] == Settings.BEE_GOLD)) {

                            area.add(new Position(i, j));
                        }
                    } else if (carry == Settings.ANT_ROCK || carry == Settings.BEE_ROCK) {
                        if (!(i == origin.row && j == origin.column) && 
                                (grid.grid[i][j] == Settings.ROCK ||
                                grid.grid[i][j] == Settings.BEE_ROCK || 
                                grid.grid[i][j] == Settings.ANT_ROCK)) {

                            area.add(new Position(i, j));
                        }
                    }
                    
                } else {
                    if (!(i == origin.row && j == origin.column) && 
                            (grid.grid[i][j] == Settings.GOLD || 
                            grid.grid[i][j] == Settings.ROCK)) {

                        area.add(new Position(i, j));
                    }
                }
            }            
        }
    }
    
    private int wrap(int x) {
        if (x < 0) {
            return 0;
        } else if (x >= grid.grid.length) {
            return grid.grid.length - 1;
        } else {
            return x;
        }
    }
    
    private void getOptions(Position origin, boolean laden) {
        
        int xStart  = wrap(-1 + origin.row);
        int yStart  = wrap(-1 + origin.column);
        int xEnd    = wrap(1 + origin.row);
        int yEnd    = wrap(1 + origin.column);
        
        for (int i = xStart; i <= xEnd; i++) {
            
            for (int j = yStart; j <= yEnd; j++) {

                if (laden) {
                    if (!(i == origin.row && j == origin.column) && grid.grid[i][j] == Settings.EMPTY) {

                        options.add(new Position(i, j));

                    }                    
                } else {
                    if (!(i == origin.row && j == origin.column) && 
                            (grid.grid[i][j] == Settings.EMPTY || 
                            grid.grid[i][j] == Settings.GOLD ||
                            grid.grid[i][j] == Settings.ROCK)) {

                        options.add(new Position(i, j));
                    }
                } 
            }            
        }
    }
    
    private Position testDensity(Robot robot) {        
        Position pos = null;
        float tmp;
        float tmp2;
        boolean test = true;
        
        if (robot.laden) {
            tmp = 0.0f;
        } else {
            tmp = Float.MAX_VALUE;
        }
        
        for (Position opt : options) {
            tmp2 = getDensity(opt, robot);
            opt.dens = tmp2;
             
            if (robot.laden) {
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
    
    public float getDensity(Position pos, Robot robot) {
        float alpha = (float) 0.80;
        float lamda = (float) (1 / grid.grid.length);
        float tmp = 0.0f;
                
        for (Position test : area) {
            
            tmp += ( distance(pos, test.row, test.column) / alpha );
                 
        }
          
        if (tmp < 0)
            return 0;
        else 
            return tmp;
    }
    
    private float distance(Position ya, int ybX, int ybY) {        
        return (float) Math.sqrt(Math.pow(Math.abs(ya.row - ybX), 2) + Math.sqrt(Math.pow(Math.abs(ya.column - ybY), 2)));
    }
    
    private boolean wrap(int p1, int p2) {
        return p1 < grid.settings.GridSize && p2 < grid.settings.GridSize && p1 >= 0 && p2 >= 0;
    }
    
}
