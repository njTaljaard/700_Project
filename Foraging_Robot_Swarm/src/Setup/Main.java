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
        for (int gSize = 1; gSize <= 3; gSize++) {
            
            switch (gSize) { //S = 50, 100, 200
                case 1:
                    Settings.GridSize = 50;
                    break;
                case 2:
                    Settings.GridSize = 100;
                    break;
                case 3:
                    Settings.GridSize = 200;
                    break;
            }
            
            for (int rCount = 1; rCount <= 5; rCount++) {

                switch (rCount) { //c = 10, 30, 50, 70, 100
                    case 1:
                        Settings.RobotCount = 10;
                        break;
                    case 2:
                        Settings.RobotCount = 30;
                        break;
                    case 3:
                        Settings.RobotCount = 50;
                        break;
                    case 4:
                        Settings.RobotCount = 70;
                        break;
                    case 5:
                        Settings.RobotCount = 100;
                        break;
                }

                for (int cover = 1; cover <= 5; cover++) {
                
                    switch(cover) { //p = 5%, 20%, 50%, 70%, 90%
                        case 1:
                        Settings.coverage = 0.05;
                        break;
                    case 2:
                        Settings.coverage = 0.2;
                        break;
                    case 3:
                        Settings.coverage = 0.5;
                        break;
                    case 4:
                        Settings.coverage = 0.7;
                        break;
                    case 5:
                        Settings.coverage = 0.9;
                        break;
                    }
                    
                    for (int ratio = 1; ratio <= 8; ratio++) {
                        
                        switch (ratio) { //r = 0.2, 0.25, 0.333, 0.5, 0.667, 0.75, 0.8, 1
                            case 1:
                                Settings.ratio = 0.2;
                                break;
                            case 2:
                                Settings.ratio = 0.25;
                                break;
                            case 3:
                                Settings.ratio = 0.333;
                                break;
                            case 4:
                                Settings.ratio = 0.5;
                                break;
                            case 5:
                                Settings.ratio = 0.667;
                                break;
                            case 6:
                                Settings.ratio = 0.75;
                                break;
                            case 7:
                                Settings.ratio = 0.8;
                                break;
                            case 8:
                                Settings.ratio = 1.0;
                                break;
                        }
                        
                        for (int scatter = 1; scatter <= 4; scatter++) {
                            
                            switch (scatter) { 
                                case 1:
                                    Settings.scatterType = Settings.UNIFORM;
                                    break;
                                case 2:
                                    Settings.scatterType = Settings.CLUSTERD;
                                    break;
                                case 3:
                                    Settings.scatterType = Settings.VEIN;
                                    break;
                                case 4:
                                    Settings.scatterType = Settings.GAUSSIAN;
                                    break;
                            }
                            
                            
                            if (Settings.scatterType == Settings.UNIFORM || Settings.scatterType == Settings.GAUSSIAN) {
                                //RUN SINGLE PROCESS TEST
                                ExecutorService exec = Executors.newFixedThreadPool(30);
                                for (int tests = 0; tests < 30; tests++) {

                                    exec.submit(new Controller());

                                }

                                //Shut down finished threads.
                                exec.shutdown();

                                //Wait for all updates
                                while (!exec.isTerminated()) {}
                            }
                            
                        }
                        
                    }
                    
                }
                
            }
            
        }
        
    }
    
}
