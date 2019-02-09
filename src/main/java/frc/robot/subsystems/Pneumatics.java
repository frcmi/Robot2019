package frc.robot.subsystems;

import frc.robot.lib.util.RobotMap;

import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.*;


//A subsystem to control a single motor, to test the code or system
public class Pneumatics extends Subsystem {

    // Subsystems are singleton classes, so there should only be one of each class. Instead
    // of calling the constructor directly, the client should use getInstance to prevent duplication
    // of Subsystem objects.
    private static Pneumatics instance;
    public static Pneumatics getInstance() {
        if (instance==null) instance = new Pneumatics();
        return instance;
    }

    private Solenoid mainSol;
    
    private Pneumatics() {
        super();
        mainSol = RobotMap.sol;
        
    }
    
    //Sets default command for the system
    @Override
    protected void initDefaultCommand() {
        setDefaultCommand(null);
    }

    public void setSol(boolean value) {
        mainSol.set(value);
    }

    public void stop() {
        mainSol.set(false);
    }
}