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
            case Settings.VEIN:
                
                int start = (int) ( (settings.GridSize / 2) - (settings.GridSize * settings.coverage) );
                int end = (int) ( (settings.GridSize / 2) + (settings.GridSize * settings.coverage) ); 
                
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
                
                Position tmp = new Position(i, j);
                tmp.dens = getDensity(tmp);
                    
                shouldAdd(centroids, tmp);
            }            
        }
        
        return centroids;
    }
    
    public void shouldAdd(ArrayList<Position> centroids, Position pos) {
        boolean inCluster = false;
        boolean highDens = false;
        
        for (Position p : centroids) {
            if (distance(pos.row, p.row, pos.column, p.column) < settings.interLimit) {
                inCluster = true;
                if (pos.dens > p.dens) {
                    highDens = true;
                    pos.high = true;
                    centroids.remove(p);
                    centroids.add(pos);
                    return;
                }
            }
        }
        
        if (!inCluster) {
            centroids.add(pos);
        } else if (inCluster && !highDens) {
            centroids.add(pos);
            pos.high = false;
        }
    }
    
    private float getDensity(Position p) {
        float lamda = (float) (1 / Math.pow(grid.length, 2));
        float alpha = (float) Math.sqrt(settings.GridSize);
        float tmp = 0.0f;
        
        int type = grid[p.row][p.column];
        
        for (int i = 0; i < grid.length; i++) {
            
            for (int j = 0; j < grid[i].length; j++) {
            
                int test = grid[i][j];

                if (type == Settings.ANT_GOLD) {

                    if (test == Settings.GOLD || test == Settings.ANT_GOLD || test == Settings.BEE_GOLD) {

                        tmp += ( 1 - distance(p.row, i, p.column, j) / alpha );

                    }

                } else if (type == Settings.ANT_ROCK) {                

                    if (test == Settings.ROCK || test == Settings.ANT_ROCK || test == Settings.BEE_ROCK) {

                        tmp += ( 1 - distance(p.row, i, p.column, j) / alpha );

                    }

                } else {

                    if (test == Settings.GOLD || test == Settings.ANT_GOLD || test == Settings.BEE_GOLD) {

                        tmp += ( 1 - distance(p.row, i, p.column, j) / alpha );

                    } else if (test == Settings.ROCK || test == Settings.ANT_ROCK || test == Settings.BEE_ROCK) {

                        tmp += ( 1 - distance(p.row, i, p.column, j) / alpha );

                    }
                }            
            }
        }
        
        lamda *= tmp;
        
        return lamda < 0.0f ? 0.0f : lamda;
    }
    
    private boolean intra(ArrayList<Position> centroids) { //inner cluster density
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
    
    private boolean inter(ArrayList<Position> centroids) { //cluster seperation
        return centroids.stream().noneMatch((p1) -> 
                (!centroids.stream().noneMatch((p2) -> 
                        (distance(p2.row, p1.row, p2.column, p1.column) < settings.interLimit))));
    }
    
    private double distance(double p1x, double p1y, double p2x, double p2y) {
        return Math.sqrt(Math.pow(Math.abs(p2x - p1x), 2) + Math.pow(Math.abs(p2y - p1y),2));
    }
}
