package frc.robot.subsystems;

import frc.robot.commands.MoveTestMotor;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.*;


//A subsystem to control a single motor, to test the code or system
public class TestMotor extends Subsystem {

    // Subsystems are singleton classes, so there should only be one of each class. Instead
    // of calling the constructor directly, the client should use getInstance to prevent duplication
    // of Subsystem objects.
    private static TestMotor instance;
    public static TestMotor getInstance() {
        if (instance==null) instance = new TestMotor();
        return instance;
    }

    private Victor motor;
    
    
    private TestMotor() {
        super();
        motor = RobotMap.testMotor;
    }
    
    //Sets default command for the system
    @Override
    protected void initDefaultCommand() {
        setDefaultCommand(null);
    }

    //Moves the motor at a constant rate
    public void moveForward() {
        motor.set(1.0);
    }
    
    //Makes the motor stop
    public void stop() {
        motor.set(0.0);
    }
}