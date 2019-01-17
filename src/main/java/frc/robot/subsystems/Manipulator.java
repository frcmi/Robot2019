package frc.robot.subsystems;

import frc.robot.commands.MoveTestMotor;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.*;


//A subsystem to control a single motor, to test the code or system
public class Manipulator extends Subsystem {

    // Subsystems are singleton classes, so there should only be one of each class. Instead
    // of calling the constructor directly, the client should use getInstance to prevent duplication
    // of Subsystem objects.
    private static Manipulator instance;
    public static Manipulator getInstance() {
        if (instance==null) instance = new Manipulator();
        return instance;
    }
    
    public Manipulator() {
        
    }

    public Manipulator(String name) {
        super(name);
    }
    
    //Sets default command for the system
    @Override
    protected void initDefaultCommand() {
        setDefaultCommand(null);
    }
}