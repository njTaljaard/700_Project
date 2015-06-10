package Board;

import Setup.Settings;
import java.util.ArrayList;
import java.util.Random;

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
        for (int y = 0; y < spaces.length; y++) {
            
            for (int x = 0; x < spaces[y].length; x++) {
                
                if (spaces[x][y].object != Settings.EMPTY && !spaces[x][y].parented) {
                    setChildren(x, y); 
                }
            }            
        }
          
        if (clusters.isEmpty()) {
            return false;
        }
        
        //Get centroids...
        ArrayList<Space> centroids = getCentroids();
                
        
        
        //Test if all are clustered...
        for (int i = 0; i < spaces.length; i++) {
            
            for (int j = 0; j < spaces[i].length; j++) {
                
                if (!spaces[i][j].parented /*&& (spaces[i][j].object == Settings.GOLD || 
                        spaces[i][j].object == Settings.ANT_GOLD || spaces[i][j].object == Settings.BEE_GOLD)*/) {
                    
                    return false;
                }
            }
        }
        
        //Test inter & intra cluster
        /*for (int i = 0; i < centroids.size(); i++) {
            
            for (int j = 0; j < centroids.size(); j++) {
                    
                if (manhattanDistance(centroids.get(i).x, centroids.get(j).x, centroids.get(i).y, centroids.get(j).y) >= 6) {
                    return true;
                    
                }
            }
        }*/
        
        return true;
    }
    
    private void setChildren(int x, int y) {
        ArrayList<Space> cluster = new ArrayList<>();
        cluster.add(spaces[x][y]);
        spaces[x][y].parented = true;
        
        for (int i = x - e; i < e + x; i++) {
            
            for (int j = y - e; j < e + y; j++) {
                
                if (wrap(i, j) && !spaces[i][j].parented && spaces[i][j].object == spaces[x][y].object)  {
                    
                    spaces[i][j].parented = true;
                    cluster.add(spaces[i][j]);
                    
                }
            }            
        }
        
        clusters.add(cluster);
    }
    
    private boolean wrap(int x, int y) {
        return x >= 0 && x < spaces.length && y >= 0 && y < spaces.length;
    }
    
    private ArrayList<Space> getCentroids() {
        ArrayList<Space> centroids = new ArrayList<>();
        
        for (ArrayList<Space> cluster : clusters) {
            
            if (cluster.size() >= 5) {
                
                //Get center point of group
                centroids.add(getCentroid(cluster));
                
            } else {
                
                //Reset cluster due to small
                for (Space s : cluster) {
                    spaces[s.x][s.y].parented = false;
                }   
            }            
        } 
        
        return centroids;
    }
    
    private Space getCentroid(ArrayList<Space> cluster) {
        Space centroid = null;
        int d = 0;
        
        for (Space sp : cluster) {
            int tmp = density(cluster, sp);
            if (tmp > d) {
                d = tmp;
                centroid = sp;
            }
        }
        
        if (centroid == null) {
            return cluster.get((int) (new Random(System.nanoTime()).nextDouble() * cluster.size()));            
        }
        
        return centroid;
    }      
    
    private int density(ArrayList<Space> cluster, Space test) {
        int density = 0;
        
        for (int i = 0; i < cluster.size(); i++) {

            if (cluster.get(i) != test) {

                density += manhattanDistance(cluster.get(i).x, test.x, cluster.get(i).y, test.y);

            }                
        }      
        
        return density;
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
