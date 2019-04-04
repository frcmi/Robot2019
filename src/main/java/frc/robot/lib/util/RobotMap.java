package frc.robot.lib.util;
import frc.robot.lib.trajectory.jetsoninterface.VisionClient;

import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import frc.robot.commands.ControlPistons;
import frc.robot.subsystems.Pneumatics;
import frc.robot.subsystems.Light;
import frc.robot.subsystems.BottomPistons;

import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.*;
import com.kauailabs.navx.frc.AHRS;

// All output and input objects (motors, cameras, encoders, etc) are defined here
public class RobotMap {
    // *** CONTROLS *** //
    // Left Drive: Left Joystick
    // Right Drive: Right Joystick
    // Front-mounted Pistons: Hold Right Trigger down
    // Shoot ball: Move Right Joystick Hat up
    // Grab ball: Move Right Joystick Hat down
    // Toggle Camera Light: Left Joystick, Right Button
    // Raise Back Piston: Left Joystick, Left Button
    // Lower Back Piston: Left Joystick, Center Button
    // Raise Front Piston: Right Joystick, Right Button
    // Lower Front Piston: Right Joystick, Center Button

    //Pathfinder params
    public static double maxVelocity = 1.7; // in m/s
    public static double maxAcceleration = 2.7; // in m/s^2
    public static double maxJerk = 60.0; // in m/s^3
    public static double position = 1;
    public static double integral = 0; //unused for current motion profiling
    public static double derivative = 0;
    public static double accelerationGain = 0;
    public static double stopDistanceFromHatch = 0.5; //in meters

    //Joysticks
    public static final int JOYSTICK_LEFT = 0;
    public static final int JOYSTICK_RIGHT = 1;

    // Axes
    public static final int AXIS_X = 0;
    public static final int AXIS_Y = 1;

    // Button codes
    public static final int BUTTON_TRIGGER = 1;
    public static final int BUTTON_SIDE_MIDDLE = 2;
    public static final int BUTTON_SIDE_LEFT = 3;
    public static final int BUTTON_SIDE_RIGHT = 4;

    // Channels (PWM)
    public static final int PWM_BACK_RIGHT = 0;
    public static final int PWM_FRONT_RIGHT = 1;
    public static final int PWM_BACK_LEFT = 3;
    public static final int PWM_FRONT_LEFT = 2;
    public static final int PWM_BALL_SHOOTER = 4;
    public static final int PWM_FLAP = 5;

    // Channels (PCM)
    public static final int PCM_SOLENOID_FORWARD = 7;
    public static final int PCM_SOLENOID_BACKWARD = 3;
    public static final int PCM_LIFTBACK_FORWARD = 2;
    public static final int PCM_LIFTBACK_BACKWARD = 1;
    public static final int PCM_LIFTFRONT_FORWARD = 5;
    public static final int PCM_LIFTFRONT_BACKWARD = 4;
    public static final int PCM_LEDRING = 6;

    // Encoders
    public static final int ENCODER_FRONT_LEFT = 0;
    public static final int ENCODER_BACK_LEFT = 1;
    public static final int ENCODER_FRONT_RIGHT = 2;
    public static final int ENCODER_BACK_RIGHT = 3;
 
    // Define outputs
    public static Victor backRight = new Victor(PWM_BACK_RIGHT);
    public static Victor frontRight = new Victor(PWM_FRONT_RIGHT);
    public static Victor backLeft = new Victor(PWM_BACK_LEFT);
    public static Victor frontLeft = new Victor(PWM_FRONT_LEFT);
    public static Victor ballShooter = new Victor(PWM_BALL_SHOOTER);
    public static Victor hatchFlap = new Victor(PWM_FLAP);
    public static DoubleSolenoid sol = new DoubleSolenoid(PCM_SOLENOID_FORWARD, PCM_SOLENOID_BACKWARD);
    public static DoubleSolenoid liftBack = new DoubleSolenoid(PCM_LIFTBACK_FORWARD, PCM_LIFTBACK_BACKWARD);
    public static DoubleSolenoid liftFront = new DoubleSolenoid(PCM_LIFTFRONT_FORWARD, PCM_LIFTFRONT_BACKWARD);

    // Define inputs
    public static Encoder leftEncoder = new Encoder(ENCODER_FRONT_LEFT, ENCODER_BACK_LEFT);
    public static Encoder rightEncoder = new Encoder(ENCODER_FRONT_RIGHT, ENCODER_BACK_RIGHT);

    public static double getLeftEncoder(){
        return leftEncoder.get();
    }
    public static double getRightEncoder(){
        return rightEncoder.get();
    }

    public static int encoderTicksPerRevolution = 1000;
    public static AHRS navx = new AHRS(SPI.Port.kMXP, (byte)100); //last integer is the bit rate (updates per second) of the navx

    public static double getGyroHeading(){
        return navx.getAngle();
    }

    // LED Ring
    public static Solenoid ledRing = new Solenoid(PCM_LEDRING);

    // Data about the robot
    // TODO: measure the distance between wheels
    public static double distBetweenWheels = 0.635; // in meters
    public static double wheelDiameter = 0.087; //in meters
    public static double commandUpdateInterval = 0.02; // in seconds

    // Thrustmaster T1600M (x2)
    public static Joystick leftThrust = new Joystick(JOYSTICK_LEFT);
    public static Joystick rightThrust = new Joystick(JOYSTICK_RIGHT);

    // Data about the camera to go to the user
    public static int userCamResX = 160;
    public static int userCamResY = 90;
    public static int userCamFPS = 20;

    // Jetson information
    public static double camDistance = 10.0; //The distance between the two cameras in inches

    // Joystick button mapping
    public static double getLeftX() {
        return leftThrust.getRawAxis(AXIS_X);
    }

    public static double getLeftY() {
        return leftThrust.getRawAxis(AXIS_Y);
    }

    public static double getRightX() {
        return rightThrust.getRawAxis(AXIS_X);
    }

    public static double getRightY() {
        return rightThrust.getRawAxis(AXIS_Y);
    }

    public static boolean getLeftTrigger() {
        return leftThrust.getRawButton(BUTTON_TRIGGER);
    }

    public static boolean getRightTrigger() {
        return rightThrust.getRawButton(BUTTON_TRIGGER);
    }

    public static boolean getLeftMid() {
        return leftThrust.getRawButton(BUTTON_SIDE_MIDDLE);
    }

    public static boolean getRightMid() {
        return rightThrust.getRawButton(BUTTON_SIDE_MIDDLE);
    }

    public static boolean getLeftLeft() {
        return leftThrust.getRawButton(BUTTON_SIDE_LEFT);
    }

    public static boolean getLeftRight() {
        return leftThrust.getRawButton(BUTTON_SIDE_RIGHT);
    }

    public static boolean getRightLeft() {
        return rightThrust.getRawButton(BUTTON_SIDE_LEFT);
    }

    public static boolean getRightRight() {
        return rightThrust.getRawButton(BUTTON_SIDE_RIGHT);
    }

    public static int getRightHat() {
        return rightThrust.getPOV();
    }

    public static int getLeftHat() {
        return leftThrust.getPOV();
    }

    public static double getLeftSlider() {
        return leftThrust.getRawAxis(3);
    }

    public static double getRightSlider() {
        return rightThrust.getRawAxis(3);
    }

    public static boolean leftWhiteButton() {
        return leftThrust.getRawButton(14);
    }

    public static boolean 


}