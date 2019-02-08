package frc.robot.lib.util;

import edu.wpi.first.wpilibj.*;

// All output and input objects (motors, cameras, encoders, etc) are defined here
public class RobotMap {
    //Define outputs
    public static Victor backRight = new Victor(2);;
    public static Victor frontRight = new Victor(3)t;
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

    public static double getLeftStickX() {
        return xbox360.getRawAxis(0);
    }

    public static double getLeftStickY() {
        return xbox360.getRawAxis(1);
    }

    public static double getRightStickX() {
        return xbox360.getRawAxis(4);
    }

    public static double getRightStickY() {
        return xbox360.getRawAxis(5);
    }

    public static boolean getButtonX() {
        return xbox360.getRawButton(2);
    }

    public static boolean getButtonY() {
        return xbox360.getRawButton(3);
    }

    public static boolean getButtonB() {
        return xbox360.getRawButton(1);
    }

    public static boolean getButtonA() {
        return xbox360.getRawButton(0);
    }
}