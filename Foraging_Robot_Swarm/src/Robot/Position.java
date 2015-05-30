package Robot;

import java.util.Random;

/**
 * @author Nico
 */
public class Position {
    
    public int row;
    public int column;
    public double currentDensity;
    public double pickupDensity;
    public double dropDensity;
    
    public Position(int gridSize) {
        Random rand = new Random(System.nanoTime());
        double r = Math.abs(rand.nextGaussian());
        while (r > 1) r--;
        
        row = (int) (r * gridSize);
        column = 0;
        currentDensity = 0.0;
        pickupDensity = 0.0;
        dropDensity = 0.0f;
    }
    
    public Position(int r, int c) {
        row = r;
        column = c;
    }
}
