package frc.robot.subsystems;

import frc.robot.commands.MoveTestMotor;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.*;


//A subsystem to control a single motor, to test the code or system
public class TankDrive extends Subsystem {

    // Subsystems are singleton classes, so there should only be one of each class. Instead
    // of calling the constructor directly, the client should use getInstance to prevent duplication
    // of Subsystem objects.
    private static TankDrive instance;
    public static TankDrive getInstance() {
        if (instance==null) instance = new TankDrive();
        return instance;
    }

    private Victor frontLeft;
    private Victor frontRight;
    private Victor backLeft;
    private Victor backRight;
    
    private TankDrive() {
        super();
        frontLeft = RobotMap.frontLeft;
        frontRight = RobotMap.frontRight;
        backLeft = RobotMap.backLeft;
        backRight = RobotMap.backRight;
    }
    
    //Sets default command for the system
    @Override
    protected void initDefaultCommand() {
        setDefaultCommand(null);
    }

    //Moves the motor at a constant rate
    public void moveForward() {
        frontLeft.set(1.0);
        frontRight.set(1.0);
        backLeft.set(1.0);
        backRight.set(1.0);
    }
    
    //Makes the motor stop
    public void stop() {
        frontLeft.set(0.0);
        frontRight.set(0.0);
        backLeft.set(0.0);
        backRight.set(0.0);
    }
}