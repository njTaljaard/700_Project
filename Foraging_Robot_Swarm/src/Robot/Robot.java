package Robot;

import Processing.Controller;
import Setup.Settings;

/**
 * @author Nico
 */
public class Robot {
    
    private AntBot antBot;
    private BeeBot beeBot;
    private Controller controller;
    
    private int robotType;
    
    public Robot(Controller controller, int robotType) {
        this.controller = controller;
        this.robotType = robotType;
        
        this.antBot = new AntBot(controller);
        this.beeBot = new BeeBot(controller);
    }
    
    public void update() {
        if (robotType == Settings.ANT) {
            antBot.update();
        } else {
            beeBot.update();
        }
    }
    
    public boolean getLaden() {
        if (robotType == Settings.ANT) {
            return antBot.getLaden();
        } else {
            return beeBot.getLaden();
        }
    }
    
    public Position getPosition() {
        if (robotType == Settings.ANT) {
            return antBot.getPosition();
        } else {
            return beeBot.getPosition();
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
