package frc.robot.lib.util;
import frc.robot.lib.trajectory.jetsoninterface.VisionClient;

import edu.wpi.first.wpilibj.Victor;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.*;
import com.kauailabs.navx.frc.AHRS;

// All output and input objects (motors, cameras, encoders, etc) are defined here
public class RobotMap {
    //Define outputs
    public static Victor backRight = new Victor(0);;
    public static Victor frontRight = new Victor(1);
    public static Victor backLeft = new Victor(3);
    public static Victor frontLeft = new Victor(2);
    public static Victor ballShooter = new Victor(4);
    public static DoubleSolenoid sol = new DoubleSolenoid(7, 3);

    //Define inputs:
    public static Encoder leftEncoder = new Encoder(0,1);
    public static Encoder rightEncoder = new Encoder(2,3);
    public static AHRS navx = new AHRS(SPI.Port.kMXP, (byte) 100); //last integer is the bit rate (updates per second) of the navx

    //Data about the robot
    public static double distBetweenWheels = 1.0; // in meters TODO: measure this
    public static double commandUpdateInterval = 0.02; // in seconds

    //Thrustmaster T1600M (x2)
    public static Joystick leftThrust = new Joystick(0);
    public static Joystick rightThrust = new Joystick(1);

    //Data about the camera to go to the user
    public static int userCamResX = 1920;
    public static int userCamResY = 1080;
    public static VisionClient visionClient = new VisionClient(null); //you can put an address in the constructor to use something other than the default
    public static double camDistance = 10.0; //The distance between the two cameras in inches

    //Joystick button mapping
    public static double getLeftX() {
        return leftThrust.getRawAxis(0);
    }

    public static double getLeftY() {
        return leftThrust.getRawAxis(1);
    }

    public static double getRightX() {
        return rightThrust.getRawAxis(0);
    }

    public static double getRightY() {
        return rightThrust.getRawAxis(1);
    }

    public static boolean getLeftTrigger() {
        return leftThrust.getRawButton(1);
    }

    public static boolean getRightTrigger() {
        return rightThrust.getRawButton(1);
    }
}