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
    
    public Grid() {
        grid = new int[Settings.GridSize][Settings.GridSize];
        
        createGrid();
        Utilities.writeGrid(grid);
        
        movement = new Movement(this);
    }
    
    public boolean complete() {
        for (int i = 0; i < Settings.GridSize; i++) {
            
            for (int j = 0; j < Settings.GridSize; i++) {
                
                if (grid[i][j] == Settings.GOLD) {
                    return false;
                }
                
            }
            
        }
        
        return true;
    }
    
    public int pickUpItem(int i, int j) {
        return grid[i][j];
    }
    
    public boolean dropItem(int i, int j, int type) {
        if (grid[i][j] == Settings.EMPTY) {
            grid[i][j] = type;
            return true;
        }
        
        return false;
    }
    
    private void createGrid() {
        //init possitions of rocks & gold
        int placement = (int) ((Settings.GridSize * 2) * Settings.coverage);
        int rock = (int) (placement / (Settings.ratio + 1));
        int gold = (int) (Settings.ratio * rock);
        int place, x, y;
        
        switch (Settings.scatterType) {
            case Settings.UNIFORM:
                for (place = 0; place < gold;) {
                    x = Utilities.getNextUniform();
                    y = Utilities.getNextUniform();
                    
                    if (grid[x][y] == Settings.EMPTY) {
                        grid[x][y] = Settings.GOLD;
                        place++;
                    }
                }
                
                for (place = 0; place < rock;) {
                    x = Utilities.getNextUniform();
                    y = Utilities.getNextUniform();
                    
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
                    x = Utilities.getNextGausion();
                    y = Utilities.getNextGausion();
                    
                    if (grid[x][y] == Settings.EMPTY) {
                        grid[x][y] = Settings.GOLD;
                        place++;
                    }
                }
                
                for (place = 0; place < rock;) {
                    x = Utilities.getNextGausion();
                    y = Utilities.getNextGausion();
                    
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
        boolean test = true;
        
        for (int x = 0; x < grid.length; x++) {
            
            for (int y = 0; y < grid[x].length; y++) {
                
                if (grid[x][y] == Settings.GOLD) {
                    
                    for (Position p : centroids) {

                        if (distance(x, y ,p.row, p.column) > Settings.intraLimit) {
                            test = false;
                            break;
                        }
                        
                    }

                    if (!test) {
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
                        (distance(p2.row, p1.row, p2.column, p1.column) < Settings.interLimit))));
    }
    
    private double distance(double p1x, double p1y, double p2x, double p2y) {
        return Math.sqrt(Math.pow(p2x - p1x, 2) + Math.pow(p2y - p1y ,2));
    }
}
