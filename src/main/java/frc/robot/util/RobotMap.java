package frc.robot.subsystems;

import edu.wpi.first.wpilibj.*;

// All output and input objects (motors, cameras, etc) are defined here
public class RobotMap {
    public static Victor testMotor;
    public static Victor backRight;
    public static Victor frontRight;
    public static Victor backLeft;
    public static Victor frontLeft;
    public static Joystick xbox360;
    public static Solenoid sol;
    static {
        backRight = new Victor(2);
        frontRight = new Victor(3);
        backLeft = new Victor(0);
        frontLeft = new Victor(1);
        xbox360 = new Joystick(0);
        sol = new Solenoid(0);
    }
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