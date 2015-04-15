package Setup;

import java.util.Random;

/**
 * @author Nico
 */
public class Position {
    public int row;
    public int column;
    public double distCount;

    public Position(int size) {
        Random ron = new Random();
        double tmp = ron.nextGaussian();
        row = (int)(size * tmp);
        column = 0;
        distCount = 0;
    }

    public Position() {
        row = 0;
        column = 0;
        distCount = 0;
    }   
    
    
    public Position(int x, int y) {
        row = x;
        column = y;
    }
}
