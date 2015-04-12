package Setup;

/**
 * @author Nico
 */
public class Position {
    public int row;
    public int column;
    public double distCount;

    public Position() {
        this.row = 0;
        this.column = 0;
        this.distCount = Double.MAX_VALUE;
    }
    
    public Position(int x, int y) {
        this.row = x;
        this.column = y;
    }
}
