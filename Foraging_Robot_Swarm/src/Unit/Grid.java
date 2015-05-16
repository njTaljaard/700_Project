package Unit;

import Processing.Movement;
import Setup.Position;
import Setup.Settings;
import Setup.Utilities;
import java.util.ArrayList;

/**
 * @author Nico
 */
public class Grid {
    public int[][] grid;
    public Movement movement;
    public Settings settings;
    private final Utilities utils;
    private final Grouping grouping;
    
    public Grid(Settings settings, Utilities util) {
        this.utils = util;
        this.settings = settings;
        this.grid = new int[settings.GridSize][settings.GridSize];
        this.grouping = new Grouping();
        
        createGrid();
        
        util.writeGrid(grid, settings, "0");
        
        movement = new Movement(this);
    }
    
    public boolean isClustered() {
        return grouping.grouped(grid);
    }
    
    public boolean complete() {
        for (int i = 0; i < settings.GridSize; i++) {
            
            for (int j = 0; j < settings.GridSize; j++) {
                
                if (grid[i][j] == Settings.GOLD) {
                    return false;
                }
                
            }
            
        }
        
        return true;
    }
    
    public boolean pickUpItem(int i, int j, boolean ant) {
        
        if (ant) {
            if (grid[i][j] == Settings.GOLD) {
                grid[i][j] = Settings.ANT_GOLD;
                return true;
            } else if (grid[i][j] == Settings.ROCK) {
                grid[i][j] = Settings.ANT_ROCK;
                return true;
            }
        } else {
            if (grid[i][j] == Settings.GOLD) {
                grid[i][j] = Settings.BEE_GOLD;
                return true;
            } else if (grid[i][j] == Settings.ROCK) {
                grid[i][j] = Settings.BEE_ROCK;
                return true;
            }
        }
        
        return false;
    }
    
    public boolean dropItem(int i, int j, int type) {
        
        if (type == Settings.ANT_GOLD || type == Settings.BEE_GOLD) {
            grid[i][j] = Settings.GOLD;
            return true;
        } 
        
        if (type == Settings.ANT_ROCK || type == Settings.BEE_ROCK) {
            grid[i][j] = Settings.ROCK;
            return true;
        }
        
        return false;
    }
    
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
            case Settings.CLUSTERD: //@TODO: CHANGE
                System.out.println("Clustered");
                for (place = 0; place < gold;) {
                    x = 0;
                    y = 0;
                    
                    if (grid[x][y] == Settings.EMPTY) {
                        grid[x][y] = Settings.GOLD;
                        place++;
                    }
                }
                
                for (place = 0; place < rock;) {
                    x = 0;
                    y = 0;
                    
                    if (grid[x][y] == Settings.EMPTY) {
                        grid[x][y] = Settings.ROCK;
                        place++;
                    }
                }
                
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
