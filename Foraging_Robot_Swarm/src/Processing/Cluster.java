package Processing;

import Setup.Position;
import Setup.Settings;
import Unit.Robot;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 * @author Nico
 */
public class Cluster {
    private final Controller controller;
    private boolean clustered;
    private float gamma_1, gamma_2;
    private Random r;
    
    public Cluster(Controller controller) {
        this.controller = controller;
        this.clustered = false;
        this.gamma_1 = 0.0f;
        this.gamma_2 = 0.0f;
        this.r = new Random();
    }
    
    public void update(Robot robot) {
        if (!clustered) {
            
            if (robot.clusterDensity == 0.0f)
                robot.clusterDensity = computeDensity(robot);
            
            ArrayList<Position> positions = controller.grid.getSurroundPositions(robot.position);
            
            for (Position pos : positions) {
                
                if (!robot.laden && controller.grid.grid[pos.row][pos.column] != Settings.EMPTY) {
                    
                    if (random() <= robot.clusterDensity) {
                        robot.setCarry(controller.grid.pickUpItem(pos.row, pos.column));
                    }
                    
                } else if (robot.laden && controller.grid.grid[pos.row][pos.column] == Settings.EMPTY) {
                    
                    if (random() <= robot.clusterDensity) {
                        
                        if (controller.grid.dropItem(pos.row, pos.column, robot.getCarry())) {
                            robot.setCarry(Settings.EMPTY);
                        }
                        
                    }
                    
                }
                
            }
            
            //moveToEmptyPos
        }
    }
        
    private float computeDensity(Robot robot) {
        float lamda = (float) (1 / Math.pow(controller.grid.grid.length, 2));
        
        float tmp = 0.0f;
        for (int i = 0; i < controller.grid.grid.length; i++) {
            
            for (int j = 0; j < controller.grid.grid[i].length; j++) {
                
                if (controller.grid.grid[i][j] == Settings.GOLD) {
                    
                    tmp += ( 1 - distance(robot.position, i, j) / gamma_1 );
                    
                }
                
            }
            
        }
        
        lamda += tmp;
        
        return lamda < 0.0f ? 0.0f : lamda;
    }
    
    private float distance(Position ya, int ybX, int ybY) {
        
        return (float) Math.sqrt(Math.pow(ybX - ya.row, 2) + Math.pow(ybY - ya.column, 2));
        
    }
    
    private float computePickPropability(float density) {
        
        return (float) Math.pow(gamma_1 / ( gamma_1 + density ), 2);
        
    }
    
    private float computeDropPropability(float density) {
        
        return density < gamma_2 ? 2 * density : 1;
        
    }
    
    private float random() {
        
        return r.nextFloat();
        
    }
}
