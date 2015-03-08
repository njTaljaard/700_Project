package Processing;

import Setup.RobotState;
import Setup.Position;
import Unit.Robot;

/**
 * @author Nico
 */
public class Forage {
    
    private final Controller controller;
    private int ittr;
    
    public Forage(Controller controller) {
        this.controller = controller;
        this.ittr = 0;
    }
    
    public void update(Robot robot) {
        ittr++;
        
        switch (robot.state) {
            case RobotState.Bee_DANCE:
                
                break;
            case RobotState.Bee_FORAGE:
                
                break;
            case RobotState.Bee_SCOUT:
                Position moveTo = controller.grid.getNewPosition(robot.position);
                
                break;
            case RobotState.Bee_WAIT:
                
                break;
        }
    }
    
}
