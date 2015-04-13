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
    
    public Grid(Settings settings, Utilities util) {
        this.settings = settings;
        this.utils = util;
        
        this.grid = new int[settings.GridSize][settings.GridSize];
        
        createGrid();
        
        util.writeGrid(grid, settings, "0");
        
        movement = new Movement(this);
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
    
    public int pickUpItem(int i, int j, boolean ant, boolean gold) {
        int r = grid[i][j];
        
        if (ant) {
            if (gold) {
                grid[i][j] = Settings.ANT_GOLD;
            } else {
                grid[i][j] = Settings.ANT_ROCK;
            }
        } else {
            if (gold) {
                grid[i][j] = Settings.BEE_GOLD;
            } else {
                grid[i][j] = Settings.BEE_ROCK;
            }
        }
        
        return grid[i][j];
    }
    
    public boolean dropItem(int i, int j, int type) {
        if (type == Settings.ANT_GOLD || type == Settings.BEE_GOLD) {
            grid[i][j] = Settings.GOLD;
            return true;
        } else if (type == Settings.ANT_ROCK || type == Settings.BEE_ROCK) {
            grid[i][j] = Settings.ROCK;
            return true;
        }
        
        return false;
    }
    
    private void createGrid() {
        //init possitions of rocks & gold
        int placement = (int) ((settings.GridSize * 2) * settings.coverage);
        int rock = (int) (placement / (settings.ratio + 1));
        int gold = (int) (settings.ratio * rock);
        int place, x, y;
        
        switch (settings.scatterType) {
            case Settings.UNIFORM:
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
            case Settings.VEIN: //@TODO: CHANGE
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
            case Settings.GAUSSIAN:
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
    
    
    public ArrayList<Position> getSurroundPositions(Position pos) {
        ArrayList<Position> list = new ArrayList<>();
        
        for (int i = pos.row-1; i < pos.row+1; i++) {
            
            for (int j = pos.column-1; j < pos.column+1; j++) {
                
                if (i >= 0 && j >= 0 && i < grid.length && j < grid[i].length &&
                        (i != pos.row && j != pos.column) ) {
                    
                    list.add(new Position(i, j));
                    
                }
                
            }
            
        }
        
        return list;
    }
    
    /*
     * Test if grid is clustered... 
     */
    
    public boolean isClustered() {
        ArrayList<Position> centroids = getCentroids();
        
        return intra(centroids) && inter(centroids);
    }
    
    public ArrayList<Position> getCentroids() {
        
        ArrayList<Position> centroids = new ArrayList<>();
        
        for (int i = 0; i < grid.length; i++) {
            
            for (int j = 0; j < grid[i].length; j++) {
                
                if (grid[i][j] == Settings.GOLD) {
                    Position pos = new Position(i,j);
                    
                    for (int x = 0; x < grid.length; x++) {
                        
                        for (int y = 0; y < grid[x].length; y++) {
                         
                            pos.distCount += distance(i, j, x, y);
                            
                        }                        
                    }
                    
                    centroids.add(pos);
                    
                    if (centroids.size() > 10) {
                        
                        double tmp = centroids.get(0).distCount;
                        int rm = 0;
                        
                        for (int k = 0; k < centroids.size(); k++) {
                            if (centroids.get(k).distCount > tmp) {
                                rm = k;
                            }
                        }
                        
                        centroids.remove(rm);
                    }
                }                
            }            
        }
        
        return centroids;
    }
    
    public boolean intra(ArrayList<Position> centroids) { //inner cluster density
        boolean test = false;
        
        for (int x = 0; x < grid.length; x++) {
            
            for (int y = 0; y < grid[x].length; y++) {
                
                if (grid[x][y] == Settings.GOLD) {
                    
                    for (Position p : centroids) {

                        if (distance(x, y ,p.row, p.column) > settings.intraLimit) {
                            test = true;
                            break;
                        }
                        
                    }

                    if (test) {
                        return test;
                    }
                }
            }
        }
        
        return test;
    }
    
    public boolean inter(ArrayList<Position> centroids) { //cluster seperation
        return centroids.stream().noneMatch((p1) -> 
                (!centroids.stream().noneMatch((p2) -> 
                        (distance(p2.row, p1.row, p2.column, p1.column) < settings.interLimit))));
    }
    
    private double distance(double p1x, double p1y, double p2x, double p2y) {
        return Math.sqrt(Math.pow(p2x - p1x, 2) + Math.pow(p2y - p1y ,2));
    }
}
