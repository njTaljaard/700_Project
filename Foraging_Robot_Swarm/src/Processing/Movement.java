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
                
                System.out.println("Im stuck " + robot.position.row + " " + robot.position.column);
                
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
        
        for (int i = -5; i <= 5; i++) {
            
            for (int j = -5; j <= 5; j++) {
            
                int tmpRow = i + origin.row;
                int tmpCol = j + origin.column;
                
                if (laden) {
                    if (carry == Settings.ANT_GOLD || carry == Settings.BEE_GOLD) {

                        if (!(i == 0 && j == 0) && wrap(tmpRow, tmpCol) && 
                                (grid.grid[tmpRow][tmpCol] == Settings.GOLD || 
                                grid.grid[tmpRow][tmpCol] == Settings.ANT_GOLD || 
                                grid.grid[tmpRow][tmpCol] == Settings.BEE_GOLD)) {

                            area.add(new Position(tmpRow, tmpCol));
                        }
                    } else if (carry == Settings.ANT_ROCK || carry == Settings.BEE_ROCK) {
                        if (!(i == 0 && j == 0) && wrap(tmpRow, tmpCol) && 
                                (grid.grid[tmpRow][tmpCol] == Settings.ROCK ||
                                grid.grid[tmpRow][tmpCol] == Settings.BEE_ROCK || 
                                grid.grid[tmpRow][tmpCol] == Settings.ANT_ROCK)) {

                            area.add(new Position(tmpRow, tmpCol));
                        }
                    }
                    
                } else {
                    if (!(i == 0 && j == 0) && wrap(tmpRow, tmpCol) && 
                            (grid.grid[tmpRow][tmpCol] == Settings.GOLD || 
                            grid.grid[tmpRow][tmpCol] == Settings.ROCK)) {

                        area.add(new Position(tmpRow, tmpCol));
                    }
                }
            }            
        }
    }
    
    private void getOptions(Position origin, boolean laden) {
        
        for (int i = -1; i <= 1; i++) {
            
            for (int j = -1; j <= 1; j++) {
            
                int tmpRow = i + origin.row;
                int tmpCol = j + origin.column;
                
                if (wrap(tmpRow, tmpCol)) {

                    if (laden) {
                        if (!(i == 0 && j == 0) && grid.grid[tmpRow][tmpCol] == Settings.EMPTY) {

                            options.add(new Position(tmpRow, tmpCol));
                            
                        }                    
                    } else {
                        if (!(i == 0 && j == 0) && 
                                (grid.grid[tmpRow][tmpCol] == Settings.EMPTY || 
                                grid.grid[tmpRow][tmpCol] == Settings.GOLD ||
                                grid.grid[tmpRow][tmpCol] == Settings.ROCK)) {

                            options.add(new Position(tmpRow, tmpCol));
                        }
                    }            
                }
            }            
        }
    }
    
    private Position testDensity(Robot robot) {        
        Position pos = null;
        float tmp = 0.0f;
        float tmp2;
        boolean test = true;
        
        for (Position opt : options) {
            tmp2 = getDensity(opt, robot);
            opt.dens = tmp2;
            
            if (tmp2 > tmp) {
                pos = opt;
                tmp = tmp2;
                test = false;
            }
        }
        
        if (test) {
            Collections.shuffle(options);
            return options.get(0);
        }
        
        return pos;
    }
    
    public float getDensity(Position pos, Robot robot) {
        float alpha = (float) 0.80;
        float lamda = (float) (1 / Math.pow(grid.settings.GridSize, 2));
        
        float tmp = 0.0f;
                
        for (Position test : area) {
            
            tmp += ( 1 - distance(pos, test.row, test.column) / alpha );
                 
        }
        
        lamda *= tmp;
                
        return lamda < 0.0f ? 0.0f : lamda;
    }
    
    private float distance(Position ya, int ybX, int ybY) {        
        return (float) Math.sqrt(Math.pow(Math.abs(ya.row - ybX), 2) + Math.pow(Math.abs(ya.column - ybY), 2));
    }
    
    private boolean wrap(int p1, int p2) {
        return p1 < grid.settings.GridSize && p2 < grid.settings.GridSize && p1 >= 0 && p2 >= 0;
    }
    
}
