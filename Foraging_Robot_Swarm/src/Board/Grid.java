package Board;

import Processing.Controller;
import Robot.Position;
import Robot.Robot;
import Setup.Settings;
import Setup.Utilities;
import java.util.ArrayList;

/**
 * @author Nico
 */
public class Grid {
    public int[][] grid;
    public Settings settings;
    private final Utilities utils;
    private final Grouping grouping;
    public ArrayList<Robot> dancers;
    
    public Grid(Settings settings, Utilities util) {
        this.utils = util;
        this.settings = settings;
        this.grid = new int[settings.GridSize][settings.GridSize];
        this.grouping = new Grouping();
        this.dancers = new ArrayList<>();
        
        createGrid();
        
        util.writeGrid(grid, settings, "0");
    }
    
    public int countRemainder() {
        int c = 0;
        
        for (int i = 0; i < grid.length; i++)
            for (int j = 0; j < grid.length; j++)
                if (grid[i][j] != Settings.EMPTY)
                    c++;
        
        return c;
    }
    
    public boolean isClustered() {
        return grouping.grouped(grid);
    }
    
    public boolean complete() {
        for (int i = 0; i < settings.GridSize; i++) {
            
            for (int j = 0; j < settings.GridSize; j++) {
                
                if (grid[i][j] == Settings.GOLD || grid[i][j] == Settings.ANT_GOLD || 
                        grid[i][j] == Settings.BEE_GOLD) {
                    return false;
                }                
            }            
        }
        
        return true;
    }
    
    public void clear() {
        for (int i = 0; i < settings.GridSize; i++) {
            
            for (int j = 0; j < settings.GridSize; j++) {
                
                if (grid[i][j] == Settings.ANT_GOLD) {
                    grid[i][j] = Settings.GOLD;
                } else if (grid[i][j] == Settings.ANT_ROCK) {
                    grid[i][j] = Settings.ROCK;
                }                
            }            
        }
    }
    
    public int getPoint(Position pos) {
        return grid[pos.row][pos.column];
    }
    
    public void setPoint(Position pos, int type, boolean ANT) {
        if (ANT) {
            if (type == Settings.GOLD)
                grid[pos.row][pos.column] = Settings.ANT_GOLD;
            else if (type == Settings.ROCK)
                grid[pos.row][pos.column] = Settings.ANT_ROCK;
            else
                grid[pos.row][pos.column] = Settings.EMPTY;
        } else {
            if (type == Settings.GOLD)
                grid[pos.row][pos.column] = Settings.BEE_GOLD;
            else if (type == Settings.ROCK)
                grid[pos.row][pos.column] = Settings.BEE_ROCK;
            else
                grid[pos.row][pos.column] = Settings.EMPTY;
        }
            
    }
    
    public int pickUpItem(Position pos, boolean ant) {
        
        if (ant) {
            
            if (grid[pos.row][pos.column] == Settings.GOLD) {
                
                grid[pos.row][pos.column] = Settings.ANT_GOLD;
                return Settings.GOLD;
                
            } else if (grid[pos.row][pos.column] == Settings.ROCK) {
                
                grid[pos.row][pos.column] = Settings.ANT_ROCK;
                return Settings.ROCK;
            }
        } else {
            
            if (grid[pos.row][pos.column] == Settings.GOLD) {
                
                grid[pos.row][pos.column] = Settings.BEE_GOLD;
                return Settings.GOLD;
                
            } else if (grid[pos.row][pos.column] == Settings.ROCK) {
                
                grid[pos.row][pos.column] = Settings.BEE_ROCK;
                return Settings.ROCK;
            }
        }
        
        return Settings.EMPTY;
    }
    
    public boolean dropItem(Position pos, int type) {
        
        if (type == Settings.EMPTY) {
            
            return true;
            
        } else if (type == Settings.GOLD) {
            
            return true;
            
        } else if (type == Settings.ROCK) {
            
            return true;
            
        }
        
        return false;
    }
    
    public ArrayList<Position> getOptions(Position origin, boolean laden) {
        
        ArrayList<Position> options = new ArrayList<>();
        
        int xStart  = utils.wrap(origin.row - 1, grid.length);
        int yStart  = utils.wrap(origin.column - 1, grid.length);
        int xEnd    = utils.wrap(origin.row + 1, grid.length);
        int yEnd    = utils.wrap(origin.column + 1, grid.length);
        
        for (int i = xStart; i <= xEnd; i++) {
            
            for (int j = yStart; j <= yEnd; j++) {
                
                if (laden) {
                    
                    if (!(i == origin.row && j == origin.column) && 
                            grid[i][j] == Settings.EMPTY) {

                        options.add(new Position(i, j));

                    }                    
                    
                } else {
                    
                    if (!(i == origin.row && j == origin.column) && 
                            (grid[i][j] == Settings.EMPTY || 
                            grid[i][j] == Settings.GOLD ||
                            grid[i][j] == Settings.ROCK)) {

                        options.add(new Position(i, j));
                    }
                } 
            }            
        }
        
        return options;
    }
   
    
    public ArrayList<Position> getAllSurrounding(Position origin, boolean laden, int carry) {
        
        ArrayList<Position> area = new ArrayList<>();
        area.clear();
        
        int xStart  = utils.wrap(origin.row - 5, grid.length);
        int yStart  = utils.wrap(origin.column - 5, grid.length);
        int xEnd    = utils.wrap(origin.row + 5, grid.length);
        int yEnd    = utils.wrap(origin.column + 5, grid.length);
        
        for (int i = xStart; i <= xEnd; i++) {
            
            for (int j = yStart; j <= yEnd; j++) {
                
                if (i != origin.row && j != origin.column) {
                    
                    if (laden) {
                        
                        if (carry == Settings.GOLD) {

                            if (grid[i][j] == Settings.GOLD || 
                                grid[i][j] == Settings.ANT_GOLD || 
                                grid[i][i] == Settings.BEE_GOLD) {

                                area.add(new Position(i, j));
                            }
                        } else if (carry == Settings.ROCK) {
                            
                            if (grid[i][j] == Settings.ROCK ||
                                grid[i][j] == Settings.BEE_ROCK || 
                                grid[i][j] == Settings.ANT_ROCK) {

                                area.add(new Position(i, j));
                            }
                        }

                    } else {
                        
                        if (grid[i][j] == Settings.GOLD || 
                            grid[i][j] == Settings.ROCK) {

                            area.add(new Position(i, j));
                        }
                    }
                }
            }            
        }
        
        return area;
    }
    
    public ArrayList<Position> getSurrounding(Position origin, boolean laden, int carry) {
        
        ArrayList<Position> area = new ArrayList<>();
        
        int yTmp = origin.row;
        int xTmp = origin.column;
        
        for (int i = -5+yTmp; i <= 5+yTmp; i++) {
            
            for (int j = -5+xTmp; j <= 5+xTmp; j++) {
                
                if (wrap(i, j) && (i != origin.row && j != origin.column)) {
                    if (laden) {
                        
                        if (grid[i][j] == Settings.GOLD || grid[i][j] == Settings.ROCK) {

                            area.add(new Position(i, j));
                        }
                    }
                }
            }            
        }
        
        return area;
    }
   
    public float getDensity(Position pos, ArrayList<Position> area) {
        float alpha = (float) 0.80;
        float lamda = (float) (1 / grid.length);
        double tmp = 0.0f;
        double add;    
        
        for (Position test : area) {
            
            add = 1 - utils.distance(pos, test.row, test.column) / alpha ;
            
            if (getPoint(test) == Settings.ROCK || getPoint(test) == Settings.BEE_ROCK
                    || getPoint(test) == Settings.ANT_ROCK) {
                add *= settings.weight; 
            }
            
            tmp += add;
        }
        
        tmp = Math.abs( tmp * (1 / Math.pow(grid.length, 2)));
        
        if (tmp <= 0)
            return 0;
        
        tmp = 1 - tmp;
        
        return (float) tmp;
    }
    
    private boolean wrap(int x, int y) {
        return x < grid.length && x >= 0 && y < grid.length && y >= 0;
    }
    
    /**
     * 
     * Grid creation!!!!!!!!!!!!!!!!!!!!!!!!!
     * 
     */
    
    private void createGrid() {
        //init possitions of rocks & gold
        int placement = (int) ((Math.pow(settings.GridSize, 2)) * settings.coverage);
        int gold = (int) (placement / (settings.ratio + 1));
        int rock = (int) (settings.ratio * gold);
        int place, x, y;
        
        System.out.println("Size : " + Math.pow(settings.GridSize, 2) + " Place : " + placement + " Rock : " + rock + " Gold : " + gold);
        
        switch (settings.scatterType) {
            case Settings.UNIFORM:
                System.out.println("Uniform");
                for (place = 0; place < gold;) {
                    x = utils.getNextUniform(settings);
                    y = utils.getNextUniform(settings);
                    
                    if (grid[x][y] == Settings.EMPTY) {
                        grid[x][y] = Settings.GOLD;
                        place++;
                    }
                }
                
                for (place = 0; place < rock;) {
                    x = utils.getNextUniform(settings);
                    y = utils.getNextUniform(settings);
                    
                    if (grid[x][y] == Settings.EMPTY) {
                        grid[x][y] = Settings.ROCK;
                        place++;
                    }
                }
                    
                break;
            case Settings.CLUSTERD: 
                System.out.println("Clusterd");
                
                Controller control = new Controller(settings, 0, false, true);
                control.preCluster();
                
                while(!control.done){}
                
                settings.scatterType = 1;
                grid = control.grid.grid;
                
                break;
            case Settings.VEIN:
                System.out.println("Vein");
                int start = (int) ( (settings.GridSize / 2) - (settings.GridSize * (settings.coverage / 2)) );
                int end = (int) ( (settings.GridSize / 2) + (settings.GridSize * (settings.coverage / 2)) ); 
                
                System.out.println(start + " " + end);
                
                for (place = 0; place < gold;) {
                    x = utils.getNextUniform(settings);
                    y = utils.getBetween(start, end, settings);
                                        
                    if (grid[x][y] == Settings.EMPTY) {
                        grid[x][y] = Settings.GOLD;
                        place++;
                    }
                }
                
                for (place = 0; place < rock;) {
                    x = utils.getNextUniform(settings);
                    y = utils.getBetween(start, end, settings);
                    
                    if (grid[x][y] == Settings.EMPTY) {
                        grid[x][y] = Settings.ROCK;
                        place++;
                    }
                }
                
                break;
            case Settings.GAUSSIAN:
                System.out.println("Gaussian");
                for (place = 0; place < gold;) {
                    x = utils.getNextGausion(settings);
                    y = utils.getNextGausion(settings);
                    
                    if (grid[x][y] == Settings.EMPTY) {
                        grid[x][y] = Settings.GOLD;
                        place++;
                    }
                }
                
                for (place = 0; place < rock;) {
                    x = utils.getNextGausion(settings);
                    y = utils.getNextGausion(settings);
                    
                    if (grid[x][y] == Settings.EMPTY) {
                        grid[x][y] = Settings.ROCK;
                        place++;
                    }
                }
                
                break;
        }
    }
}
