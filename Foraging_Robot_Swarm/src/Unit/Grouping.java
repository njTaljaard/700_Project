package Unit;

import Setup.Settings;
import java.util.ArrayList;

/**
 * @author Nico
 */
public class Grouping {
    
    Space[][] spaces;
    ArrayList<ArrayList<Space>> clusters;
    final int e = 6;
    
    public Grouping() {
        clusters = new ArrayList<>();
    }
    
    public boolean grouped(int[][] grid) {
        clusters.clear();
        createSpace(grid);
        
        //Set object clusters...
        for (int y = 0; y < grid.length; y++) {
            
            for (int x = 0; x < grid.length; x++) {
                
                if (grid[x][y] == Settings.GOLD || grid[x][y] == Settings.ROCK ||
                        grid[x][y] == Settings.ANT_GOLD || grid[x][y] == Settings.ANT_ROCK) {                    
                    testParented(grid, x, y);                    
                }                
            }            
        }
          
        //Get centroids...
        ArrayList<Space> centroids = new ArrayList<>();
        
        for (ArrayList<Space> cluster : clusters) {
            
            if (cluster.size() > 4) {
                
                //Get center point of group
                centroids.add(getCentroid(cluster));
                
            } else {
                
                //Reset cluster due to small
                for (Space s : cluster) {
                    spaces[s.x][s.y].parented = false;
                }   
            }            
        }        
        
        //Test clustered...
        for (ArrayList<Space> a : clusters) {
            for (Space s : a) {
                if (!s.parented) {
                    return false;
                }
            }
        }
        
        //Test inter & intra cluster
        for (int i = 0; i < centroids.size(); i++) {
            
            for (int j = 0; j < centroids.size(); j++) {
                
                if (manhattanDistance(centroids.get(i).x, centroids.get(j).x, centroids.get(i).y, centroids.get(j).y) < 4) {
                    return false;
                }
            }
        }
        
        return true;
    }
    
    private void testParented(int[][] grid, int x, int y) {
        ArrayList<Space> cluster = new ArrayList<>();
        spaces[x][y].object = grid[x][y];
        spaces[x][y].parented = true;
        cluster.add(spaces[x][y]);
        
        int toX = e + x;
        int toY = e + y;
        int fromX = x - e;
        int fromY = y - e;
        
        for (int i = fromX; i < toX; i++) {
            
            for (int j = fromY; j < toY; j++) {
                
                if (i >= 0 && j >= 0 && i < grid.length && j < grid[i].length &&
                        (i != x && j != y) ) {
                
                    if (grid[i][j] == spaces[i][j].object && !spaces[i][j].parented) {
                        spaces[i][j].x = i;
                        spaces[i][j].y = j;
                        spaces[i][j].parented = true;
                        cluster.add(spaces[i][j]);
                    }
                    
                }
            }
        }
        
        clusters.add(cluster);
    }
    
    private Space getCentroid(ArrayList<Space> cluster) {
        density(cluster);
        
       Space centroid = cluster.get(0);
       
       for (Space s : cluster) {    
           if (s.density > centroid.density) {
               centroid = s;
           }           
       }
       
       return centroid;
    }        
    
    private void density(ArrayList<Space> cluster) {
        
        for (int i = 0; i < cluster.size(); i++) {
            
            for (int j = 0; j < cluster.size(); j++) {
                
                if (j != i) {
                    
                    cluster.get(i).density += manhattanDistance(cluster.get(i).x, cluster.get(j).x, cluster.get(i).y, cluster.get(j).y);
                    
                }                
            }            
        }        
    }
    
    private int manhattanDistance(int x1, int x2, int y1, int y2) {
        return Math.abs(x2 - x1) + Math.abs(y2 - y1);
    }
    
    private void createSpace(int[][] grid) {
        spaces = new Space[grid.length][grid.length];
        
        for (int x = 0; x < grid.length; x++) {
            
            for (int y = 0; y < grid.length; y++) {
                spaces[x][y] = new Space(x, y, grid[x][y]);
            }            
        }
    }
    
    class Space {
        
        int x;
        int y;
        int object;
        int cluster;
        int density;
        boolean parented;
        
        public Space(int x, int y, int obj) {
            this.x = x;
            this.y = y;
            this.object = obj;
            this.parented = false;
        }
        
    }
} 
