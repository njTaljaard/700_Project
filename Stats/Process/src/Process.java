
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Nico
 */
public class Process {
        
    static ArrayList<Double> placed;
    static ArrayList<Double> picked;
    static ArrayList<Double> wait;
    static ArrayList<Double> iterations;
    
    static ArrayList<Double> percentages;
    static ArrayList<Double> times;
    
    static int[] scatterTypes = {1,2,3,4};
    static int[] robotCounts = {10,30,50,70,100};
    static int[] gridSizes = {50,100,200};
    static double[] coverages = {0.05,0.2,0.35,0.5};
    static double[] ratios = {0.2,0.25,0.33,0.5,0.667,0.75,0.8,1};
    static double[] weights = {0.2,0.25,0.33,0.5,0.667,0.75,0.8,1};
        
    public static void main(String[] args) {
        placed = new ArrayList<>();
        picked = new ArrayList<>();
        wait = new ArrayList<>();
        iterations = new ArrayList<>();
        
        percentages = new ArrayList<>();
        times = new ArrayList<>();
        
        loadFiles();
        
        mainValues();
        
        percentageAvg();
        
        percentageAllMean();
        
        percentageStd();
        
        timeAvg();
        
        timeAllMean();
        
        timeStd();
        
        countStagnation();
        
        countLimited();
        
        iterationAvg();
    }
    
    public static void loadFiles() {
        StringTokenizer tokenizer;
        String file;
        String line;
        
        //Drop ratio
        for (int ratio = 0; ratio <= 7; ratio++) {

            //Pickup controbution
            for (int weight = 0; weight <= 7; weight++) {            

                //Grid pattern
                for (int scatter = 1; scatter <= 3; scatter++) {

                    //Grid coverage
                    for (int cover = 0; cover <= 3; cover++) {

                        //Grid size
                        for (int gSize = 0; gSize <= 2; gSize++) {

                            //Amount of robots
                            for (int rCount = 0; rCount <= 4; rCount++) {
                                
                                file = "../1/" + ratios[ratio] + "-" + weights[weight] + "-" 
                                        + scatterTypes[scatter] + "-" + coverages[cover]
                                        + "-" + gridSizes[gSize] + "-" + robotCounts[rCount]
                                        + ".txt";
                                
                                try {
                                    BufferedReader read = new BufferedReader(new FileReader(file));
                                    read.readLine();
                                    
                                    for (int i = 0; i < 30; i++) {
                                        tokenizer = new StringTokenizer(read.readLine(), ",");
                                        
                                        if (tokenizer.countTokens() == 8) {
                                            iterations.add(Double.parseDouble(tokenizer.nextToken()));
                                            wait.add(Double.parseDouble(tokenizer.nextToken()));
                                            placed.add(Double.parseDouble(tokenizer.nextToken()));
                                            picked.add(Double.parseDouble(tokenizer.nextToken()));
                                        }
                                    }
                                    
                                } catch (FileNotFoundException ex) {
                                    Logger.getLogger(Process.class.getName()).log(Level.SEVERE, null, ex);
                                } catch (IOException ex) {
                                    Logger.getLogger(Process.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                        }                        
                    }
                }                    
            }                
        }            
        
        System.out.println("Iterations: " + Arrays.toString(iterations.toArray()));
        System.out.println("Wait: " + Arrays.toString(wait.toArray()));
        System.out.println("Places: " + Arrays.toString(placed.toArray()));
        System.out.println("Picked: " + Arrays.toString(picked.toArray()));
    }
    
    public static void mainValues() {
        
        for (int i = 0; i < 15360; i++) {
            percentages.add((double) (picked.get(i) / placed.get(i)));
            times.add((double) ((iterations.get(i) + wait.get(i)) / 100000 * percentages.get(i) ));
        }
        
        System.out.println("Percentages: " + Arrays.toString(percentages.toArray()));
        System.out.println("Times: " + Arrays.toString(times.toArray()));
    }
    
    public static void percentageAvg() {
        System.out.println("\nAvg Percentage Table");
       
        String str = "";
        int count = 0;
        double percent = 0.0;
        
        for (int i = 0; i < 8; i++) {
            
            for (int k = 0; k < 8; k++) {
                
                for (int j = 0; j < 240; j++) {
                    
                    if (count < 15360) {
                        percent += percentages.get(count);
                        count++;
                    }
                    
                }   
                
                str += percent / 240 + " ";
                percent = 0.0;
            }
            
            System.out.println(str);
            str = "";
        }
        
        System.out.println("\n");
    }
    
    public static void percentageAllMean() {
        System.out.println("Avg Percentage All");
        
        double percent = 0.0;
        
        for (int i = 0; i < 15360; i++) {

            percent += percentages.get(i);  
            
        }
        
        System.out.println(percent / 15360);
        System.out.println("\n");
    }
        
    public static void percentageStd() {
        double avg = 0.0;
        double sd = 0.0;
        
        for (int i = 0; i < 15360; i++) {
            
            avg += percentages.get(i);
            
        }
        
        avg = avg / 15360;
        
        for (int i = 0; i < 15360; i++) {
            
            sd += Math.pow((percentages.get(i) - avg), 2) / 15360;
            
        } 
        
        System.out.println("Percentages Std");
        System.out.println(Math.sqrt(sd));
        System.out.println("\n");
    }

    public static void timeAvg() {
        System.out.println("Avg Time Table");
        
        String str = "";
        int count = 0;
        double time = 0.0;
        
        for (int i = 0; i < 8; i++) {
            
            for (int k = 0; k < 8; k++) {
                
                for (int j = 0; j < 240; j++) {

                    time += times.get(count);
                    count++;
                }            

                str += time / 240 + " ";
                time = 0.0;
            }
            
            System.out.println(str);
            str = "";
        }
        
        System.out.println("\n");
    }

    public static void timeAllMean() {
        System.out.println("Avg Time All");
        
        double time = 0.0;
        
        for (int i = 0; i < 64; i++) {
            
            for (int j = 0; j < 240; j++) {

                time += times.get(i);
            }            
            
        }
        
        System.out.println(time / 15360);
        System.out.println("\n");
    }

    public static void timeStd() {
        double avg = 0.0;
        double sd = 0.0;
        
        for (int i = 0; i < 15360; i++) {
            
            avg += times.get(i);
            
        }
        
        avg = avg / 15360;
        
        for (int i = 0; i < 15360; i++) {
            
            sd += Math.pow((times.get(i) - avg), 2) / 15360;
            
        } 
        
        System.out.println("Percentages Std");
        System.out.println(Math.sqrt(sd));
        System.out.println("\n");
    }
    
    public static void countStagnation() {
        double count = 0.0;
        
        for (int i = 0; i < 15360; i++) {
            if (iterations.get(i) != 100000.0 && percentages.get(i) != 1.0) {
                count++;
            }
        }
        
        System.out.println("Amount Stagnated: " + (count/15360) + "\n");
    }
    
    public static void countLimited() {
        double count = 0.0;
        
        for (int i = 0; i < 15360; i++) {
            if (iterations.get(i) == 100000.0) {
                count++;
            }
        }
        
        System.out.println("Amount Limited: " + (count/15360) + "\n");
    }
    
    public static void iterationAvg() {
        double avg = 0.0;
        
        for (int i = 0; i < 15360; i++) {
            avg += iterations.get(i);
        }
        
        avg /= 15360;
        
        System.out.println("Avg iterations: " + avg);
    }
}
