package Robot;

import Processing.Controller;
import Setup.RobotState;
import Setup.Settings;

/**
 * @author Nico
 */
public class Robot {
    
    private AntBot antBot;
    private BeeBot beeBot;
    private Position position;
    private Controller controller;
    
    private int robotType;
    
    public Robot(Controller controller, int robotType) {
        this.controller = controller;
        this.robotType = robotType;
        
        this.antBot = new AntBot(controller);
        this.beeBot = new BeeBot(controller);
        
        this.position = new Position(controller.settings.GridSize);
        this.position.pickupDensity = 0.0;
    }
    
    public void update(int iteration) {
        
        Position newPos = null;
        
        if (robotType == RobotState.ANT) {
            newPos = antBot.update(position, iteration);
        } else {
            newPos = beeBot.update(this, position);
        }
        
        if (newPos != null) {
            
            position.row = newPos.row;
            position.column = newPos.column;
            position.currentDensity = newPos.currentDensity;
            
            if (newPos.pickupDensity > position.pickupDensity)
                position.pickupDensity = newPos.pickupDensity;
        }
    }
    
    public boolean getLaden() {
        if (robotType == RobotState.ANT) {
            return antBot.getLaden();
        } else {
            return beeBot.getLaden();
        }
    }
    
    public Position getPosition() {
        return position;
    }
    
    public Robot getBot() {
        return this;
    }
    
    public Position getBareing() {
        return beeBot.getBareing();
    }
    
    public int getCarry() {
        if (robotType == Settings.ANT) {
            return antBot.getCarry();
        } else {
            return beeBot.getCarry();
        }
    }
    
    public int getState() {
        if (robotType == Settings.ANT) {
            return antBot.getState();
        } else {
            return beeBot.getState();
        }
    }
}
