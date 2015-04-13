package Processing;

import Setup.Position;
import Setup.Settings;
import Unit.Grid;
import java.util.ArrayList;
import java.util.Random;

/**
 * @author Nico
 */
public class Movement {
    
    private Grid grid;
    
    public Movement(Grid grid) {
        this.grid = grid;
    }
    
    //Scouting
    public Position getNewPosition(Position origin) { // Can see 5 units ahead
        ArrayList<Position> availablePosition = new ArrayList<>();
        int fromX = wrapPosition(origin.row-5, true);
        int toX = wrapPosition(origin.row+5, false);
        int fromY = wrapPosition(origin.column-5, true);
        int toY = wrapPosition(origin.column+5, false);
        boolean goldFound = false;
        
        for (int i = fromX; i < toX; i++) {
            
            for (int j = fromY; j < toY; j++) {
                
                if (grid.grid[i][j] == Settings.GOLD || grid.grid[i][j] == Settings.ROCK ||
                        grid.grid[i][j] == Settings.ANT_GOLD || grid.grid[i][j] == Settings.ANT_ROCK){
                    availablePosition.add(new Position(i, j));
                        
                    if (grid.grid[i][j] == Settings.GOLD || grid.grid[i][j] == Settings.ANT_GOLD)
                        goldFound = true;
                }
                
            }
            
        }
        
        if (availablePosition.isEmpty()) { //No priority found random direction...     
            
            return setDefaultPos(origin);
            
        }  else { 
        
            Position course = getClosest(availablePosition, origin, goldFound);

            return getMoveTo(course, origin);
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
        
        if (testUse(origin.row-1, origin.column) && 
                grid.grid[origin.row-1][origin.column] == Settings.EMPTY) {
            availablePosition.add(new Position(origin.row-1, origin.column));    
        } 
        
        if (testUse(origin.row, origin.column-1) && 
                grid.grid[origin.row][origin.column-1] == Settings.EMPTY) {
            availablePosition.add(new Position(origin.row, origin.column-1));    
        } 
        
        if (testUse(origin.row+1, origin.column) && 
                grid.grid[origin.row+1][origin.column] == Settings.EMPTY) {
            availablePosition.add(new Position(origin.row+1, origin.column));    
        } 
        
        if (testUse(origin.row, origin.column+1) && 
                grid.grid[origin.row][origin.column+1] == Settings.EMPTY) {
            availablePosition.add(new Position(origin.row, origin.column+1));  
        } 
        
        if (testUse(origin.row-1, origin.column-1) && 
                grid.grid[origin.row-1][origin.column-1] == Settings.EMPTY) {
            availablePosition.add(new Position(origin.row-1, origin.column-1));    
        } 
        
        if (testUse(origin.row+1, origin.column+1) && 
                grid.grid[origin.row+1][origin.column+1] == Settings.EMPTY) {
            availablePosition.add(new Position(origin.row+1, origin.column+1));
        } 
        
        if (testUse(origin.row-1, origin.column+1) && 
                grid.grid[origin.row-1][origin.column+1] == Settings.EMPTY) {
            availablePosition.add(new Position(origin.row-1, origin.column+1));    
        }
        
        if (testUse(origin.row+1, origin.column-1) && 
                grid.grid[origin.row+1][origin.column-1] == Settings.EMPTY) {
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
        for (Position distPos : availablePosition) {
            dist2 = distance(distPos, origin);

            if (goldFound) {
                if (dist2 < dist && grid.grid[distPos.row][distPos.column] == Settings.GOLD) {
                    dist = dist2;
                    course = distPos;
                }
            } else {
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
    }
}
