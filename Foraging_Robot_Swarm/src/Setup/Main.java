package Setup;

import Processing.Controller;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Nico
 */
public class Main {
    
    public static void main(String[] args) {
        Main mn = new Main();
        mn.runSimulation();
    }
    
    public Main() {
        //Set other values.
    }
    
    public void runSimulation() {
        ExecutorService exec = Executors.newFixedThreadPool(12);        
        
        //Grid size
        for (int gSize = 1; gSize <= 3; gSize++) {
            
            Settings.GridSize = Settings.gridSizes[gSize];
            
            //Amount of robots
            for (int rCount = 1; rCount <= 5; rCount++) {

                Settings.RobotCount = Settings.robotCounts[rCount];

                //Grid coverage
                for (int cover = 1; cover <= 5; cover++) {
                    
                    Settings.coverage = Settings.coverages[cover];
                    
                    //Gold : Rock
                    for (int ratio = 1; ratio <= 8; ratio++) {
                        
                        Settings.ratio = Settings.ratios[ratio];
                        
                        //Grid pattern
                        for (int scatter = 1; scatter <= 4; scatter++) {
                            
                            Settings.scatterType = Settings.scatterTypes[scatter];                            
                            
                            if (Settings.scatterType == Settings.UNIFORM ||  //Remove
                                    Settings.scatterType == Settings.GAUSSIAN) { //Remove
                                
                                //Run a single configuration
                                for (int tests = 0; tests < 30; tests++) {
                                    exec.submit(new Controller());
                                }
                            }
                            
                        }
                        
                    }
                    
                }
                
            }
            
        }
        
        exec.shutdown();
        
        while (!exec.isTerminated()) {}
        
    }
    
}
