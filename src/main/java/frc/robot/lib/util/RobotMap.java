package frc.robot.lib.util;

import edu.wpi.first.wpilibj.*;
import com.kauailabs.navx.frc.AHRS;

// All output and input objects (motors, cameras, encoders, etc) are defined here
public class RobotMap {
    //Define outputs
    public static Victor backRight = new Victor(2);
    public static Victor frontRight = new Victor(3);
    public static Victor backLeft = new Victor(0);
    public static Victor frontLeft = new Victor(1);
    public static Joystick xbox360 = new Joystick(0);
    public static Solenoid sol = new Solenoid(0);

    //Define inputs:
    public Encoder leftEncoder;
    public Encoder rightEncoder;
    public AHRS navX = new AHRS(SPI.Port.kMXP, 100); //last integer is the bit rate (updates per second) of the navx

    //Data about the robot
    public float distBetweenWheels = 1.0; // in meters TODO: measure this
    public float commandUpdateInterval = 0.02; // in seconds


    //Thrustmaster T1600M (x2)
    public Joystick leftThrust = new Joystick(0);
    public Joystick rightThrust = new Joystick(1);

    //Joystick button mapping
    public double leftX() {
        return leftThrust.getRawAxis(0);
    }

    public double leftY() {
        return leftThrust.getRawAxis(1);
    }

    public double rightX() {
        return rightThrust.getRawAxis(0);
    }

    public double rightY() {
        return rightThrust.getRawAxis(1);
    }

    public boolean leftTrigger() {
        return leftThrust.getRawButton(0);
    }

    public boolean rightTrigger() {
        return rightThrust.getRawButton(0);
    }

}