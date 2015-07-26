package Processing;

import Processing.Controller;
import Setup.Settings;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

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
        int id1 = 0;
        int id2 = 0;
        
        final BlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(38880);
        ExecutorService executorService = new ThreadPoolExecutor(12, 38880,
                1000, TimeUnit.MILLISECONDS,
                queue);

        /*
         * ONLY FORAGE!!!!   
         */
        
        //Gold : Rock
        for (int ratio = 0; ratio <= 8; ratio++) {

            //Pickup controbution
            for (int weight = 0; weight <= 8; weight++) {            

                //Grid pattern
                for (int scatter = 0; scatter <= 3; scatter++) {
                    
                    //Grid coverage
                    for (int cover = 0; cover <= 3; cover++) {

                        //Grid size
                        for (int gSize = 0; gSize <= 2; gSize++) {

                            //Amount of robots
                            for (int rCount = 0; rCount <= 4; rCount++) {

                                executorService.execute(
                                        new Controller(
                                            new Settings(gSize, rCount, cover, 
                                                    ratio, weight, scatter),
                                            id1++, false, false));
                            }                        
                        }
                    }                    
                }                
            }            
        }
        
        /*
         * PRE CLUSTER!!!!   
         */
        
        //Gold : Rock
        for (int ratio = 0; ratio <= 8; ratio++) {

            //Pickup controbution
            for (int weight = 0; weight <= 8; weight++) {            

                //Grid pattern
                for (int scatter = 0; scatter <= 3; scatter++) {
                    
                    //Grid coverage
                    for (int cover = 0; cover <= 3; cover++) {

                        //Grid size
                        for (int gSize = 0; gSize <= 2; gSize++) {

                            //Amount of robots
                            for (int rCount = 0; rCount <= 4; rCount++) {
                                    
                                executorService.execute(
                                        new Controller(
                                            new Settings(gSize, rCount, cover, 
                                                    ratio, weight, scatter),
                                            id2++, false, true));
                            }                        
                        }
                    }                    
                }                
            }            
        }
        
        
        /*
         * Division of Labor!!!!   
         */
        /*
        //Gold : Rock
        for (int ratio = 0; ratio <= 8; ratio++) {

            //Pickup controbution
            for (int weight = 0; weight <= 8; weight++) {            

                //Grid pattern
                for (int scatter = 0; scatter <= 3; scatter++) {
                    
                    //Grid coverage
                    for (int cover = 0; cover <= 3; cover++) {

                        //Grid size
                        for (int gSize = 0; gSize <= 2; gSize++) {

                            //Amount of robots
                            for (int rCount = 0; rCount <= 4; rCount++) {
                                    
                                executorService.execute(
                                        new Controller(
                                            new Settings(gSize, rCount, cover, 
                                                    ratio, weight, scatter),
                                            id2++, false, true));
                            }                        
                        }
                    }                    
                }                
            }            
        }*/
                
        executorService.shutdown();
        
        double complete;
        while (!executorService.isTerminated()) {
            try {
                Thread.sleep(10000);
                
                complete = ((double)queue.remainingCapacity()) / 38880 * 100;
                System.out.println(complete + "%");
                
            } catch (InterruptedException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        System.exit(0);
    }
    
}
