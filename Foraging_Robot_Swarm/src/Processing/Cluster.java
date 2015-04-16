package Processing;

import Setup.Position;
import Setup.Settings;
import Unit.Robot;
import java.util.ArrayList;
import java.util.Random;

/**
 * @author Nico
 */
public class Cluster {
    private final Controller controller;
    private boolean clustered;
    private float gamma_1, gamma_2, alpha;
    private Random r;
    
    public Cluster(Controller controller) {
        this.controller = controller;
        this.clustered = false;
        this.gamma_1 = 0.4f;
        this.gamma_2 = 0.4f;
        this.alpha = (float) Math.sqrt(controller.settings.GridSize);
        this.r = new Random();
    }
    
    public void update(Robot robot) {

        robot.position = controller.grid.movement.getNewPosition(robot);
          
        ArrayList<Position> surround = getSurrounding(robot.position);
        
        robot.clusterDensity = computeDensity(robot, surround);

        //ArrayList<Position> positions = controller.grid.getSurroundPositions(robot.position);

        //positions.stream().forEach((pos) -> {
            
            if (!robot.laden && controller.grid.grid[robot.position.row][robot.position.column] != Settings.EMPTY) {
                
                boolean gold = controller.grid.grid[robot.position.row][robot.position.column] == Settings.GOLD;
                
                if (random() <= computePickPropability(robot.clusterDensity)) {
                    robot.setCarry(
                            controller.grid.pickUpItem(robot.position.row, robot.position.column, true, gold), 
                            true);
                }
            } else if (robot.laden) {

                if (robot.ladenCount > 10) {
                    
                    if (controller.grid.dropItem(robot.position.row, robot.position.column, robot.getCarry())) {
                        robot.setCarry(Settings.EMPTY, true);
                    }
                    
                } else if (random() <= computeDropPropability(robot.clusterDensity)) {

                    if (controller.grid.dropItem(robot.position.row, robot.position.column, robot.getCarry())) {
                        robot.setCarry(Settings.EMPTY, true);
                    }
                }
            }
        //});
    }
    
    
    
    private ArrayList<Position> getSurrounding(Position origin) {
        ArrayList<Position> tmp = new ArrayList<>();
        
        for (int i = -5; i <= 5; i++) {
            
            for (int j = -5; j <= 5; j++) {
            
                int tmpRow = i + origin.row;
                int tmpCol = j + origin.column;
                
                if (!(i == 0 && j == 0) && wrap(tmpRow, tmpCol) && 
                        (controller.grid.grid[tmpRow][tmpCol] == Settings.EMPTY ||
                        controller.grid.grid[tmpRow][tmpCol] == Settings.ANT_GOLD || 
                        controller.grid.grid[tmpRow][tmpCol] == Settings.ANT_ROCK)) {
                    
                    tmp.add(new Position(i+origin.row, j+origin.column));
                }               
            }            
        }
        
        return tmp;
    }
        
    private float computeDensity(Robot robot, ArrayList<Position> surrounding) {
        float lamda = (float) (1 / Math.pow(controller.grid.grid.length, 2));
        
        float tmp = 0.0f;
        /*for (int i = 0; i < controller.grid.grid.length; i++) {
            
            for (int j = 0; j < controller.grid.grid[i].length; j++) {*/
        for (Position pos : surrounding) {
                
                if (robot.getCarry() == Settings.ANT_GOLD) {
                    
                    if (controller.grid.grid[pos.row][pos.column] == Settings.GOLD || 
                            controller.grid.grid[pos.row][pos.column] == Settings.ANT_GOLD ||
                            controller.grid.grid[pos.row][pos.column] == Settings.BEE_GOLD) {

                        tmp += ( 1 - distance(robot.position, pos.row, pos.column) / alpha );

                    }
                    
                } else if (robot.getCarry() == Settings.ANT_ROCK) {                
                    
                    if (controller.grid.grid[pos.row][pos.column] == Settings.ROCK || 
                        controller.grid.grid[pos.row][pos.column] == Settings.ANT_ROCK ||
                        controller.grid.grid[pos.row][pos.column] == Settings.BEE_ROCK) {
                        
                        tmp += ( 1 - distance(robot.position, pos.row, pos.column) / alpha ) / 2;
                        
                    }
                    
                } else {
                    
                    if (controller.grid.grid[pos.row][pos.column] == Settings.GOLD || 
                            controller.grid.grid[pos.row][pos.column] == Settings.ANT_GOLD ||
                            controller.grid.grid[pos.row][pos.column] == Settings.BEE_GOLD) {

                        tmp += ( 1 - distance(robot.position, pos.row, pos.column) / alpha );

                    } else if (controller.grid.grid[pos.row][pos.column] == Settings.ROCK || 
                        controller.grid.grid[pos.row][pos.column] == Settings.ANT_ROCK ||
                        controller.grid.grid[pos.row][pos.column] == Settings.BEE_ROCK) {
                        
                        tmp += ( 1 - distance(robot.position, pos.row, pos.column) / alpha ) / 2;
                        
                    }
                    
                }
                
            //}
            
        }
        
        lamda *= tmp;
        
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
    
    private boolean wrap(int p1, int p2) {
        return p1 < controller.grid.settings.GridSize && p2 < controller.grid.settings.GridSize && p1 >= 0 && p2 >= 0;
    }
    
    private float random() {
        
        return r.nextFloat();
        
    }
}
