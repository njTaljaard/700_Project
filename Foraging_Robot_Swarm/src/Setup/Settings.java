package Setup;

/**
 * @author Nico
 */
public class Settings {
    public static final int FORAGE = 0;
    public static final int CLUSTER = 1;
    
    //Grid types
    public static int EMPTY = 0;
    public static int BEE = 1;
    public static int ANT = 2;
    public static int GOLD = 3;
    public static int ROCK = 4;
    
    //Scatter formation of gold on grid
    public static int scatterType;
    
    public static final int UNIFORM     = 1;
    public static final int CLUSTERD    = 2;
    public static final int VEIN        = 3;
    public static final int GAUSSIAN    = 4;
    
    public static int RobotCount;   //c = 10, 30, 50, 70, 100
    
    public static int GridSize;     //S = 50, 100, 200
    
    public static double coverage;  //p = 5%, 20%, 50%, 70%, 90%
    
    public static double ratio;     //r = 0.2, 0.25, 0.333, 0.5, 0.667, 0.75, 0.8, 1
    
    //@TODO: need stopping condition & max iterations...
    
    //Honey Bee foraging
    public static final int tMax = 200; //time steps    
    public static final int fMax = 100; //time steps    
    public static final double Phi = 0.8; //phi    
    public static final double Rho = 0.1; //rho
    
    //Desert Ant cemetary clustering
    
}
