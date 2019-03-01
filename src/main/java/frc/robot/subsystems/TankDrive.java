package frc.robot.subsystems;

import frc.robot.subsystems.lib.Tread;
import frc.robot.lib.util.*;

import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.*;


//A subsystem to control a single motor, to test the code or system
public class TankDrive extends DriveTrain {

    // Subsystems are singleton classes, so there should only be one of each class. Instead
    // of calling the constructor directly, the client should use getInstance to prevent duplication
    // of Subsystem objects.
    private static TankDrive instance;
    public static double leftScalar = -1; //Change this to offset variation in tread (make it so robot always moves straight)
    public static double rightScalar = 1; // Change this to offset variation in tread
    public static TankDrive getInstance() {
        if (instance==null) instance = new TankDrive();
        return instance;
    }

    private Tread leftTread, rightTread;

    private TankDrive() {
        super();
        leftTread = new Tread(RobotMap.frontLeft, RobotMap.backLeft);
        rightTread = new Tread(RobotMap.frontRight, RobotMap.backRight);
        leftTread.setInverted(true);
    }
    
    //Sets default command for the system
    @Override
    protected void initDefaultCommand() {
        setDefaultCommand(null);
    }

    //Moves the right tread with power magnitude
    public void moveRightDrive(double magnitude){
        rightTread.set(magnitude * rightScalar);
    }

    //Moves the left tread with power magnitude
    public void moveLeftDrive(double magnitude){
        leftTread.set(magnitude * leftScalar);
    }
    
    //Makes the motor stop
    public void stop() {
        leftTread.stopMotor();
        rightTread.stopMotor();
    }
}