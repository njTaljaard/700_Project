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
    
    public static int ANT_GOLD = 6;
    public static int BEE_GOLD = 7;
    public static int ANT_ROCK = 8;
    public static int BEE_ROCK = 9;
    
    //Scatter formation of gold on grid
    public int scatterType;
    public static int[] scatterTypes = new int[] {1,2,3,4};
    public static final int UNIFORM     = 1;
    public static final int CLUSTERD    = 2;
    public static final int VEIN        = 3;
    public static final int GAUSSIAN    = 4;
    
    public int RobotCount;   //c = 10, 30, 50, 70, 100
    public static int[] robotCounts = new int[] {10,30,50,70,100};
    
    public int GridSize;     //S = 50, 100, 200
    public static int[] gridSizes = new int[] {50,100,200};
    
    public double coverage;  //p = 5%, 20%, 50%, 70%, 90%
    public static double[] coverages = new double[] {0.05,0.2,0.35,0.5};
    
    public double ratio;     //r = 0.2, 0.25, 0.333, 0.5, 0.667, 0.75, 0.8, 1
    public static double[] ratios = new double[] {0.2,0.25,0.33,0.5,0.667,0.75,0.8,1};
    
    public double weight;
    public static double[] weights = new double[] {0.2,0.25,0.33,0.5,0.667,0.75,0.8,1};
    
    //@TODO: need stopping condition & max iterations...
    
    //Honey Bee foraging
    public static final int tMax = 200; //time steps    
    public static final int fMax = 100; //time steps    
    public static final double Phi = 0.8; //phi    
    public static final double Rho = 0.1; //rho
    
    //Desert Ant cemetary clustering
    public double interLimit = 12.0;
    public double intraLimit = 5.0;   
    
    public Settings(int gSize, int rCount, int cover, int ratio, int weight, int scatter) {
        this.GridSize = gridSizes[gSize];
        this.RobotCount = robotCounts[rCount];
        this.coverage = coverages[cover];
        this.ratio = ratios[ratio];
        this.scatterType = scatterTypes[scatter];
        this.weight = weights[weight];
        this.interLimit = GridSize / 25;
        this.intraLimit = GridSize / 25;
    }
}
