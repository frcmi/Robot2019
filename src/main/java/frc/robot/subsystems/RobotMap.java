package frc.robot.subsystems;

import edu.wpi.first.wpilibj.*;

// All output and input objects (motors, cameras, etc) are defined here
public class RobotMap {
    public static Victor testMotor;
    public static Victor backRight;
    public static Victor frontRight;
    public static Victor backLeft;
    public static Victor frontLeft;
    public static Joystick logiJoy;
    public static Solenoid sol;
    static {
        backRight = new Victor(0);
        frontRight = new Victor(1);
        backLeft = new Victor(2);
        frontLeft = new Victor(3);
    }
}