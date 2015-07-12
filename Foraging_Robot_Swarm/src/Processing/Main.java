package Processing;

import Processing.Controller;
import Setup.Settings;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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
        int id = 0;
        
        final BlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(5000000);
        ExecutorService executorService = new ThreadPoolExecutor(12, 5000000,
                1000, TimeUnit.MILLISECONDS,
                queue);
                
        //Grid size
        for (int gSize = 0; gSize <= 2; gSize++) {
            
            //Amount of robots
            for (int rCount = 0; rCount <= 4; rCount++) {

                //Grid coverage
                for (int cover = 0; cover <= 3; cover++) {
                    
                    //Gold : Rock
                    for (int ratio = 0; ratio <= 7; ratio++) {

                        //Grid pattern
                        for (int scatter = 0; scatter <= 3; scatter++) {
                        
                            //Pickup controbution
                            for (int weight = 0; weight <= 7; weight++) {
                                
                                /*System.out.println(id + " " + Settings.gridSizes[gSize] + "-" + Settings.robotCounts[rCount] 
                                        + "-" + Settings.coverages[cover] + "-" + Settings.ratios[ratio] + "-" 
                                        + Settings.scatterTypes[scatter] + "-" + Settings.weights[weight]);//*/
                                
                                //Run a single configuration
                                for (int tests = 0; tests < 30; tests++) {
                                    
                                    executorService.execute(
                                            new Controller(
                                                new Settings(gSize, rCount, cover, 
                                                        ratio, weight, scatter),
                                                id++, false, false, tests));//*/
                                    
                                    //executorService.execute(new Controller(new Settings(0, 0, 3, 0, 3, 1), id++, false, false, tests));
                                }
                            }                        
                        }
                    }                    
                }                
            }            
        }//*/
        
        executorService.shutdown();
        while (!executorService.isTerminated()) {}
        
        System.exit(0);
    }
    
}
