package Processing;

import Setup.Position;
import Setup.Settings;
import Unit.Grid;
import Unit.Robot;
import java.util.ArrayList;
import java.util.Random;

/**
 * @author Nico
 */
public class Movement {
    
    private Grid grid;
    private ArrayList<Position> area;
    private ArrayList<Position> options;
    private Random rand;
    
    public Movement(Grid grid) {
        this.grid = grid;
        this.area = new ArrayList<>();
        this.options = new ArrayList<>();
        this.rand = new Random();
    }
    
    public Position getNewPosition(Robot robot) { // Can see 5 units ahead
        try {
            area = getSurrounding(robot.position);
            options = getOptions(robot.position);

            if (area.isEmpty()) { // no surrounding gold found       

                if (options.isEmpty()) { // im stuck

                    System.out.println("Im stuck " + robot.position.row + " " + robot.position.column);
                    return robot.position;

                } 
            } else {

                Position move = testDensity(robot);

                if (move != null) {
                    return move;
                }
            }
            
            // use random movement
            if (!options.isEmpty()) {
                double tmp = Math.abs(rand.nextGaussian());
                
                while (tmp > 1)
                    tmp -= 1;
                
                int r = (int)((options.size()-1) * tmp);
                
                return options.get(r);
            } else {
                return robot.position;
            }
            
        } finally {
            area.clear();
            options.clear();
        }
    }    
    
    private ArrayList<Position> getSurrounding(Position origin) {
        ArrayList<Position> tmp = new ArrayList<>();
        
        for (int i = -5; i <= 5; i++) {
            
            for (int j = -5; j <= 5; j++) {
            
                int tmpRow = i + origin.row;
                int tmpCol = j + origin.column;
                
                if (!(i == 0 && j == 0) && wrap(tmpRow, tmpCol) && 
                        (grid.grid[tmpRow][tmpCol] == Settings.EMPTY ||
                        grid.grid[tmpRow][tmpCol] == Settings.ANT_GOLD || 
                        grid.grid[tmpRow][tmpCol] == Settings.ANT_ROCK)) {
                    
                    tmp.add(new Position(i+origin.row, j+origin.column));
                }               
            }            
        }
        
        return tmp;
    }
    
    private ArrayList<Position> getOptions(Position origin) {
        ArrayList<Position> tmp = new ArrayList<>();
        
        for (int i = -1; i <= 1; i++) {
            
            for (int j = -1; j <= 1; j++) {
            
                int tmpRow = i + origin.row;
                int tmpCol = j + origin.column;
                
                if (!(i == 0 && j == 0) && wrap(tmpRow, tmpCol) && 
                        grid.grid[tmpCol][tmpCol] == Settings.EMPTY) {
                    
                    tmp.add(new Position(tmpRow, tmpCol));
                    
                }                
            }            
        }
        
        return tmp;
    }
    
    private Position testDensity(Robot robot) {        
        Position pos = null;
        float tmp = 0.0f;
        float tmp2;
        
        for (Position opt : options) {
            tmp2 = getDensity(opt, robot);
            if (tmp2 > tmp) {
                pos = opt;
                tmp = tmp2;
            }
        }
        
        return pos;
    }
    
    private float getDensity(Position pos, Robot robot) {
        float alpha = (float) Math.sqrt(5);
        float lamda = (float) (1 / 25);
        
        float tmp = 0.0f;
                
        for (Position test : area) {
            
            if (robot.getCarry() == Settings.ANT_GOLD) {

                if (grid.grid[test.row][test.column] == Settings.GOLD || 
                        grid.grid[test.row][test.column] == Settings.ANT_GOLD ||
                        grid.grid[test.row][test.column] == Settings.BEE_GOLD) {

                    tmp += ( 1 - distance(pos, test.row, test.column) / alpha );

                }

            } else if (robot.getCarry() == Settings.ANT_ROCK) {                

                if (grid.grid[test.row][test.column] == Settings.ROCK || 
                    grid.grid[test.row][test.column] == Settings.ANT_ROCK ||
                    grid.grid[test.row][test.column] == Settings.BEE_ROCK) {

                    tmp += ( 1 - distance(pos, test.row, test.column) / alpha ) / 2;

                }

            } else {

                if (grid.grid[test.row][test.column] == Settings.GOLD || 
                        grid.grid[test.row][test.column] == Settings.ANT_GOLD ||
                        grid.grid[test.row][test.column] == Settings.BEE_GOLD) {

                    tmp += ( 1 - distance(pos, test.row, test.column) / alpha );

                } else if (grid.grid[test.row][test.column] == Settings.ROCK || 
                    grid.grid[test.row][test.column] == Settings.ANT_ROCK ||
                    grid.grid[test.row][test.column] == Settings.BEE_ROCK) {

                    tmp += ( 1 - distance(pos, test.row, test.column) / alpha ) / 2;

                }
            }            
        }
        
        lamda *= tmp;
        
        return lamda < 0.0f ? 0.0f : lamda;
    }
    
    private float distance(Position ya, int ybX, int ybY) {
        
        return (float) Math.sqrt(Math.pow(ybX - ya.row, 2) + Math.pow(ybY - ya.column, 2));
        
    }
    
    private boolean wrap(int p1, int p2) {
        return p1 < grid.settings.GridSize && p2 < grid.settings.GridSize && p1 >= 0 && p2 >= 0;
    }
    
    //Scouting
    /*public Position getNewPosition(Position origin) { // Can see 5 units ahead
        ArrayList<Position> availablePosition = new ArrayList<>();
        int fromX = wrapPosition(origin.row-5, true);
        int toX = wrapPosition(origin.row+5, false);
        int fromY = wrapPosition(origin.column-5, true);
        int toY = wrapPosition(origin.column+5, false);
        boolean goldFound = false;
        Position tmp;
        
        for (int i = fromX; i < toX; i++) {
            
            for (int j = fromY; j < toY; j++) {
                
                tmp = new Position(i, j);
                
                if (grid.grid[i][j] == Settings.GOLD || grid.grid[i][j] == Settings.ANT_GOLD || grid.grid[i][j] == Settings.BEE_GOLD) {
                    goldFound = true;
                    
                    if (distance(origin, tmp) == 1.0) {
                        if (grid.grid[i][j] == Settings.EMPTY || grid.grid[i][j] == Settings.GOLD) {
                            availablePosition.add(tmp);
                        }
                    } else {
                        availablePosition.add(tmp);
                    }
                    
                } else if (grid.grid[i][j] == Settings.ROCK || grid.grid[i][j] == Settings.ANT_ROCK || grid.grid[i][j] == Settings.BEE_ROCK) {
                    
                    if (distance(origin, tmp) == 1.0) {
                        if (grid.grid[i][j] == Settings.EMPTY || grid.grid[i][j] == Settings.ROCK) {
                            availablePosition.add(tmp);
                        }
                    } else {
                        availablePosition.add(tmp);
                    }
                }
            }
            
        }
        
        if (availablePosition.isEmpty()) { //No priority found random direction...     
            
            Position test = setDefaultPos(origin);
            
            //System.out.println(origin.row + " " + origin.column + "\t" + test.row + " " + test.column);
            
            return test;//setDefaultPos(origin);
            
        }  else { 
        
            Position course = getClosest(availablePosition, origin, goldFound);

            Position test = getMoveTo(course, origin);
            
            //System.out.println(origin.row + " " + origin.column + "\t" + test.row + " " + test.column);
            
            return test;// getMoveTo(course, origin);
        }
    }
    
    private Position getMoveTo(Position course, Position origin) {
        Position moveTo = origin;
        
        if (course.row < origin.row && course.column == origin.column) {
            moveTo.row -= 1;
        } else if (course.row > origin.row && course.column == origin.column) {
            moveTo.row += 1;
        } else if (course.row == origin.row && course.column < origin.column) {
            moveTo.column -= 1;
        } else if (course.row == origin.row && course.column > origin.column) {
            moveTo.column += 1;
        } else if (course.row < origin.row && course.column < origin.column) {
            moveTo.row -= 1;
            moveTo.column -= 1;
        } else if (course.row > origin.row && course.column > origin.column) {
            moveTo.row += 1;
            moveTo.column += 1;
        } else if (course.row < origin.row && course.column > origin.column) {
            moveTo.row -= 1;
            moveTo.column += 1;
        } else if (course.row > origin.row && course.column < origin.column) {
            moveTo.row += 1;
            moveTo.column -= 1;
        }  
        
        return moveTo;
    }
    
    private Position setDefaultPos(Position origin) {
        ArrayList<Position> availablePosition = new ArrayList<>();
        
        if (testUse(origin.row-1, origin.column)) {
            availablePosition.add(new Position(origin.row-1, origin.column));    
        } 
        
        if (testUse(origin.row, origin.column-1)) {
            availablePosition.add(new Position(origin.row, origin.column-1));    
        } 
        
        if (testUse(origin.row+1, origin.column)) {
            availablePosition.add(new Position(origin.row+1, origin.column));    
        } 
        
        if (testUse(origin.row, origin.column+1)) {
            availablePosition.add(new Position(origin.row, origin.column+1));  
        } 
        
        if (testUse(origin.row-1, origin.column-1)) {
            availablePosition.add(new Position(origin.row-1, origin.column-1));    
        } 
        
        if (testUse(origin.row+1, origin.column+1)) {
            availablePosition.add(new Position(origin.row+1, origin.column+1));
        } 
        
        if (testUse(origin.row-1, origin.column+1)) {
            availablePosition.add(new Position(origin.row-1, origin.column+1));    
        }
        
        if (testUse(origin.row+1, origin.column-1)) {
            availablePosition.add(new Position(origin.row+1, origin.column-1));
        }
        
        Random rnd = new Random();
        int use = (int) (rnd.nextFloat() * availablePosition.size());
        
        return availablePosition.get(use);
    }
    
    private Position getClosest(ArrayList<Position> availablePosition, Position origin, boolean goldFound) {
        double dist = Double.MAX_VALUE;
        double dist2;
        Position course = new Position();
        
        if (goldFound) {
            for (Position distPos : availablePosition) {
                dist2 = distance(distPos, origin);

                if (dist2 < dist && grid.grid[distPos.row][distPos.column] == Settings.GOLD) {
                    dist = dist2;
                    course = distPos;
                }
            }
        } else {
            for (Position distPos : availablePosition) {
                dist2 = distance(distPos, origin);

                if (dist2 < dist) {
                    dist = dist2;
                    course = distPos;
                }
            }
        }
        
        return course;
    }
    
    private double distance(Position pos, Position origin) {
        return Math.sqrt((pos.row - origin.row) * (pos.row - origin.row) + 
                (pos.column - origin.column) * (pos.column - origin.column));
    }
    
    private boolean testUse(int x, int y) {
        return x >= 0 && y >=0 && x < grid.settings.GridSize && y < grid.settings.GridSize;
    }
    
    private int wrapPosition(int pos, boolean type) {
        if (!type) {
            
            if (pos >= grid.settings.GridSize-1) {
                return grid.settings.GridSize-1;
            } else {
                return pos;
            }
            
        } else {
            
            if (pos < 0) {
                return 0;
            } else {
                return pos;
            }
            
        }
    }*/
}
