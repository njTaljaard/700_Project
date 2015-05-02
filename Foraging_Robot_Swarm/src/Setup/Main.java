package Setup;

import Processing.Controller;
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
        
        final BlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(57600);
        ExecutorService executorService = new ThreadPoolExecutor(12, 57600,
                0L, TimeUnit.MILLISECONDS,
                queue);
                
        //Grid size
        /*for (int gSize = 0; gSize <= 2; gSize++) {
            
            //Amount of robots
            for (int rCount = 0; rCount <= 4; rCount++) {

                //Grid coverage
                for (int cover = 0; cover <= 3; cover++) {
                    
                    //Gold : Rock
                    for (int ratio = 0; ratio <= 7; ratio++) {
                        
                        //Grid pattern
                        for (int scatter = 0; scatter <= 3; scatter++) {
                                                        
                            if (Settings.scatterTypes[scatter] == Settings.UNIFORM ||  //Remove
                                    Settings.scatterTypes[scatter] == Settings.GAUSSIAN) { //Remove
                                
                                //Run a single configuration
                                for (int tests = 0; tests < 30; tests++) {
                                    executorService.execute(
                                            new Controller(
                                                new Settings(gSize, rCount, cover, 
                                                        ratio, scatter),
                                                id++));
                                }
                            }                            
                        }                        
                    }                    
                }                
            }            
        }*/
        
        executorService.execute(new Controller(new Settings(0, 0, 3, 0, 2), id++));
        
        //while (queue.remainingCapacity() > 0) {}
        
    }
    
}
