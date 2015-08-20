package Processing;

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
    
    static int ratio;
    static int scatter;
    static String type;
    
    public static void main(String[] args) {
        Main mn = new Main();
        
        if (args.length == 3) {
            ratio = Integer.parseInt(args[0]);
            scatter = Integer.parseInt(args[1]);
            type = args[2];
            mn.runSimulation();
        } else {
            System.out.println("Use arguments:");
            System.out.println("1. Ratio: 0, 0.2, 0.25, 0.33, 0.5, 0.667, 0.75, 0.8, 1");
            System.out.println("2. Scatter: 0-4");
            System.out.println("3. Type: 0 / 1");
            System.out.println("");
            System.out.println("PreCluster = 0 : Scatter = 2, 3, 4");
            System.out.println("No Cluster = 1 : Scatter = 2");
        }
    }
    
    public Main() {
        //Set other values.
    }
    
    public void runSimulation() {
        int id1 = 0;
        int id2 = 0;
        //38880
        final BlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(480);
        ExecutorService executorService = new ThreadPoolExecutor(40, 480,
                1000, TimeUnit.MILLISECONDS, queue);

        if ("0".equals(type)) {
            
            /*
             * PRE CLUSTER!!!!   
             */

            //Gold : Rock
            //for (int ratio = 0; ratio <= 8; ratio++) {

                //Pickup controbution
                for (int weight = 0; weight <= 8; weight++) {            

                    //Grid pattern
                    //for (int scatter = 1; scatter <= 3; scatter++) {

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
                    //}                
                }            
            //}
        } else if ("1".equals(type)) {
        
            /*
             * ONLY FORAGE!!!!   
             */
        
            //Gold : Rock
            //for (int ratio = 0; ratio <= 8; ratio++) {

                //Pickup controbution
                for (int weight = 0; weight <= 8; weight++) {            

                    //Grid pattern
                    //for (int scatter = 0; scatter <= 3; scatter++) {

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
                    //}                
                }            
            //}
        }
                        
        executorService.shutdown();
        
        double complete = 0.0;
        while (queue.remainingCapacity() != 480) {//!executorService.isTerminated()) {
            try {
                Thread.sleep(10000);
                
                complete = ((double)queue.remainingCapacity()) / 480 * 100;
                System.out.println(complete + "%");
                
            } catch (InterruptedException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
                
        System.out.println("I am done");
     
    }
}
