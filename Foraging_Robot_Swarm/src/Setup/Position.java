package Setup;

import java.util.Random;

/**
 * @author Nico
 */
public class Position {
    public int row;
    public int column;
    public int distance;
    public float dens;
    public boolean high;

    public Position(int size) {
        Random ron = new Random();
        double tmp = Math.abs(ron.nextGaussian());
        
        while (tmp > 1)
            tmp -= 1;
        
        row = (int)((size-1) * tmp);
        column = 0;
        dens = 0;
        high = false;
    }

    public Position() {
        row = 0;
        column = 0;
        dens = 0;
        high = false;
    }   
    
    
    public Position(int x, int y) {
        row = x;
        column = y;
        dens = 0;
        high = false;
    }
    
    public String Print() {
        return row + " " + column;
    }
}
