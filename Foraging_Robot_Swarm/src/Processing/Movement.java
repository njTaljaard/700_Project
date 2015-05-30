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
    
    /*
     * BEE 
     */
    public Position getBeeNewPosition(Robot robot) { // Can see 5 units ahead
        area.clear();
        options.clear();
        
        try {
            getOptions(robot.position, robot.laden);
                                     
            if (options.isEmpty()) {
                System.out.println("Options are empty");
                return robot.position;
                
            } else {
                
                getBeeSurrounding(robot.position);
                                    
                if (!area.isEmpty()) { // no surrounding objects found       
                    
                    Position move = testBeeDensity(robot); //prefered direction of options
                    
                    if (move != null) { //Move to best position
                        System.out.println("Found move from test");
                        if (robot.laden) {
                            grid.grid[robot.position.row][robot.position.column] = Settings.EMPTY;
                            grid.grid[move.row][move.column] = robot.getCarry();
                        }

                        return move;
                    }
                }
                
                // use random movement
                System.out.println("Surroundings empty, move random");
                int r = (int) (robot.getRandom() * options.size());
                Collections.shuffle(options, new Random(System.nanoTime()));
                
                if (robot.laden) {
                    grid.grid[robot.position.row][robot.position.column] = Settings.EMPTY;
                    grid.grid[options.get(r).row][options.get(r).column] = robot.getCarry();
                }

                return options.get(r);
            }
        } finally {
            area.clear();
            options.clear();
        }
    }   
    
    private void getBeeSurrounding(Position origin) {
        int yTmp = origin.row;
        int xTmp = origin.column;
        
        for (int i = -5+yTmp; i <= 5+yTmp; i++) {
            
            for (int j = -5+xTmp; j <= 5+xTmp; j++) {
                
                if (wrap(i, j) && (i != origin.row && j != origin.column)) {
                    
                    if (grid.grid[i][j] == Settings.GOLD || 
                            grid.grid[i][j] == Settings.ROCK) {
                        
                        area.add(new Position(i, j));
                    }
                }
            }            
        }
    }
    
    public Position moveToSink(Robot robot) {
        
        if (grid.grid[robot.position.row][robot.position.column-1] == Settings.EMPTY) {
            
            return new Position(robot.position.row, robot.position.column-1);
            
        } else if (robot.position.row+1 < grid.grid.length) {
            
            if (grid.grid[robot.position.row+1][robot.position.column-1] == Settings.EMPTY) {
                
                return new Position(robot.position.row+1, robot.position.column-1);
                
            } else if (grid.grid[robot.position.row+1][robot.position.column] == Settings.EMPTY) {
                
                return new Position(robot.position.row+1, robot.position.column);
                
            }
                
        } else if (robot.position.row-1 > 0) {
            
            if (grid.grid[robot.position.row-1][robot.position.column-1] == Settings.EMPTY) {
                
                return new Position(robot.position.row-1, robot.position.column-1);
                
            } else if (grid.grid[robot.position.row-1][robot.position.column] == Settings.EMPTY) {
                
                return new Position(robot.position.row-1, robot.position.column);
                
            }
            
        } 
        
        return robot.position;
        
        /*ArrayList<Position> opt = new ArrayList<>();
        
        int yTmp = robot.position.row;
        int xTmp = -1 + robot.position.column;
        
        if (xTmp < 0)
            xTmp++;
        
        for (int i = -1+yTmp; i < 1+yTmp; i++) {
            
            if (i >= 0 && i < grid.grid.length) {
                
                System.out.println(i + " " + xTmp + " " + grid.grid[i][xTmp]);
                
                if (xTmp == 0 && grid.grid[i][xTmp] == Settings.EMPTY) {
                    
                    return new Position(i, xTmp);
                    
                } else if (grid.grid[i][xTmp] == Settings.EMPTY) {
                
                    opt.add(new Position(i, xTmp));
                }
            }
        }
        
        if (opt.isEmpty()) {
            System.out.println("No options");
            return robot.position;
        } else {
            Collections.shuffle(opt);
            grid.grid[robot.position.row][robot.position.column] = Settings.EMPTY;
            grid.grid[opt.get(0).row][opt.get(0).column] = robot.getCarry();
            return opt.get(0);
        }*/
    }
    
    public Position moveToBare(Robot robot) {
        options.clear();
        
        try {
            getOptions(robot.position, false);
            
            if (options.size() > 0) {
                Position rtn = null;
                double dist = Double.MAX_VALUE;
                
                for (Position opt : options) {
                    if (vectorAngle(robot, opt) < dist) {
                        rtn = opt;
                    }
                }
                
                if (rtn != null) {
                    int r = (int) (robot.getRandom() * options.size());
                    Collections.shuffle(options, new Random(System.nanoTime()));
                    return options.get(r);
                } 
            }
            
            return robot.position;
        } finally {
            options.clear();
        }
    }
    
    private double vectorAngle(Robot robot, Position tmp) {
        Position tmpBare = new Position(Math.abs(robot.baringVector.row - tmp.row), 
            Math.abs(robot.baringVector.column - tmp.column));
        
        return Math.sqrt(Math.abs(robot.baringVector.row - tmpBare.row) + 
                Math.abs(robot.baringVector.column - tmpBare.column));
    }
    
    private Position testBeeDensity(Robot robot) {        
        Position pos = null;
        float tmp = 0.0f;
        float tmp2 ;
        boolean test = true;
        
        for (Position opt : options) {
            tmp2 = getBeeDensity(opt, robot);
            robot.position.dens = tmp2;
            
            if (tmp2 > tmp) {
                pos = opt;
                tmp = opt.dens;
                test = false;
            }            
        }
        
        if (test) {
            System.out.println("Move random, no good surroundings");
            //Collections.shuffle(options, new Random(System.nanoTime()));
            return options.get((int)(robot.getRandom() * options.size()));
        }
        
        return pos;
    }
    
    public float getBeeDensity(Position pos, Robot robot) {
        float alpha = (float) 0.50;
        float lamda = (float) (1 / grid.grid.length);
        float tmp = 0.0f;
                
        for (Position test : area) {
            
            tmp += (distance(pos, test.row, test.column) / alpha );
                 
        }
        
        tmp *= (1 / Math.pow(grid.grid.length, 2));
          
        if (tmp < 0)
            return 0;
        else 
            return tmp;
    }
    
    /*
     * ANT
     */
    
    public Position getAntNewPosition(Robot robot) { // Can see 5 units ahead
        area.clear();
        options.clear();
        
        try {
            getOptions(robot.position, robot.laden);
                        
            if (options.isEmpty()) {
                
                //System.out.println("Im stuck " + robot.position.row + " " + robot.position.column);
                
                return robot.position;
                
            } else {
                
                getAntSurrounding(robot.position, robot.laden, robot.getCarry());

                if (!area.isEmpty()) { // no surrounding gold found       
                    
                    Position move = testAntDensity(robot); //of open options
                    
                    if (move != null) { //Move to best position
                        
                        if (robot.laden) {
                            grid.grid[robot.position.row][robot.position.column] = Settings.EMPTY;
                            grid.grid[move.row][move.column] = robot.getCarry();
                        }

                        return move;
                    }
                }

                // use random movement

                int r = (int) (robot.getRandom() * options.size());
                Collections.shuffle(options, new Random(System.nanoTime()));
                
                if (robot.laden) {
                    grid.grid[robot.position.row][robot.position.column] = Settings.EMPTY;
                    grid.grid[options.get(r).row][options.get(r).column] = robot.getCarry();
                }

                return options.get(r);
            }
        } finally {
            area.clear();
            options.clear();
        }
    }    
    
    private void getAntSurrounding(Position origin, boolean laden, int carry) {
        
        int yTmp = origin.row;
        int xTmp = origin.column;
        
        for (int i = -5+yTmp; i <= 5+yTmp; i++) {
            
            for (int j = -5+xTmp; j <= -5+xTmp; j++) {
                            
                if (wrap(i, j) && (i != origin.row && j != origin.column)) {
                    if (laden) {
                        if (carry == Settings.ANT_GOLD || carry == Settings.BEE_GOLD) {

                            if (grid.grid[i][j] == Settings.GOLD || 
                                    grid.grid[i][j] == Settings.ANT_GOLD || 
                                    grid.grid[i][i] == Settings.BEE_GOLD) {

                                area.add(new Position(i, j));
                            }
                        } else if (carry == Settings.ANT_ROCK || carry == Settings.BEE_ROCK) {
                            if (grid.grid[i][j] == Settings.ROCK ||
                                    grid.grid[i][j] == Settings.BEE_ROCK || 
                                    grid.grid[i][j] == Settings.ANT_ROCK) {

                                area.add(new Position(i, j));
                            }
                        }

                    } else {
                        if (grid.grid[i][j] == Settings.GOLD || 
                                grid.grid[i][j] == Settings.ROCK) {

                            area.add(new Position(i, j));
                        }
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
    
    private Position testAntDensity(Robot robot) {        
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
        
        tmp *= (1 / Math.pow(grid.grid.length, 2));
          
        if (tmp < 0)
            return 0;
        else 
            return tmp;
    }
    
    private float distance(Position ya, int ybX, int ybY) {        
        return (float) Math.sqrt(Math.pow(Math.abs(ya.row - ybX), 2) + Math.sqrt(Math.pow(Math.abs(ya.column - ybY), 2)));
    }
    
    private boolean wrap(int p1, int p2) {
        return p1 >= 0 && p2 >= 0 && p1 < grid.settings.GridSize && p2 < grid.settings.GridSize;
    }
    
}
