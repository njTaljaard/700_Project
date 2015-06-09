package Processing;

import Setup.Position;
import Setup.Settings;
import Board.Grid;
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
    /*public Position getBeeNewPosition(Robot robot) { // Can see 5 units ahead
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
            
            //tmp += (distance(pos, test.row, test.column) / alpha );
                 
        }
        
        tmp *= (1 / Math.pow(grid.grid.length, 2));
          
        if (tmp < 0)
            return 0;
        else 
            return tmp;
    }*/
    
    /*
     * ANT
     */
    
    
    
}
