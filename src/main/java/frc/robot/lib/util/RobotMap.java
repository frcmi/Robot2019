package frc.robot.lib.util;
import frc.robot.lib.trajectory.jetsoninterface.VisionClient;

import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import frc.robot.commands.ClimbPistonsBack;
import frc.robot.commands.ClimbPistonsFront;
import frc.robot.subsystems.Pneumatics;
import frc.robot.subsystems.Light;

import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.*;
import com.kauailabs.navx.frc.AHRS;

// All output and input objects (motors, cameras, encoders, etc) are defined here
public class RobotMap {
    //Joysticks
    public static final int JOYSTICK_LEFT = 0;
    public static final int JOYSTICK_RIGHT = 1;

    // Axes
    public static final int AXIS_X = 0;
    public static final int AXIS_Y = 1;

    // Button codes
    public static final int BUTTON_TRIGGER = 1;
    public static final int BUTTON_MIDDLE = 2;
    public static final int BUTTON_SIDE_LEFT = 3;
    public static final int BUTTON_SIDE_RIGHT = 4;

    // Channels (PWM)
    public static final int PWM_BACK_RIGHT = 0;
    public static final int PWM_FRONT_RIGHT = 1;
    public static final int PWM_BACK_LEFT = 3;
    public static final int PWM_FRONT_LEFT = 2;
    public static final int PWM_BALL_SHOOTER = 4;

    // Channels (PCM)
    public static final int PCM_SOLENOID_FORWARD = 7;
    public static final int PCM_SOLENOID_BACKWARD = 3;
    public static final int PCM_LIFTBACK_FORWARD = 2;
    public static final int PCM_LIFTBACK_BACKWARD = 1;
    public static final int PCM_LIFTFRONT_FORWARD = 4;
    public static final int PCM_LIFTFRONT_BACKWARD = 5;
    public static final int PCM_LEDRING = 6;

    // Encoders
    public static final int ENCODER_LEFT_A = 0;
    public static final int ENCODER_LEFT_B = 1;
    public static final int ENCODER_RIGHT_A = 2;
    public static final int ENCODER_RIGHT_B = 3;
 
    // Define outputs
    public static Victor backRight = new Victor(PWM_BACK_RIGHT);
    public static Victor frontRight = new Victor(PWM_FRONT_RIGHT);
    public static Victor backLeft = new Victor(PWM_BACK_LEFT);
    public static Victor frontLeft = new Victor(PWM_FRONT_LEFT);
    public static Victor ballShooter = new Victor(PWM_BALL_SHOOTER);
    public static DoubleSolenoid sol = new DoubleSolenoid(PCM_SOLENOID_FORWARD, PCM_SOLENOID_BACKWARD);
    public static DoubleSolenoid liftBack = new DoubleSolenoid(PCM_LIFTBACK_FORWARD, PCM_LIFTBACK_BACKWARD);
    public static DoubleSolenoid liftFront = new DoubleSolenoid(PCM_LIFTFRONT_FORWARD, PCM_LIFTFRONT_BACKWARD);

    // Define inputs
    public static Encoder leftEncoder = new Encoder(ENCODER_LEFT_A, ENCODER_LEFT_B);
    public static Encoder rightEncoder = new Encoder(ENCODER_RIGHT_A, ENCODER_RIGHT_B);
    public static AHRS navx = new AHRS(SPI.Port.kMXP, (byte)100); //last integer is the bit rate (updates per second) of the navx

    // LED Ring
    public static Solenoid ledRing = new Solenoid(PCM_LEDRING);

    // Data about the robot
    public static double distBetweenWheels = 1.0; // in meters TODO: measure this
    public static double commandUpdateInterval = 0.02; // in seconds

    // Thrustmaster T1600M (x2)
    public static Joystick leftThrust = new Joystick(JOYSTICK_LEFT);
    public static Joystick rightThrust = new Joystick(JOYSTICK_RIGHT);

    // Data about the camera to go to the user
    public static int userCamResX = 640;
    public static int userCamResY = 480;

    // Jetson information
    // public static String jetsonAddress = "http://tegra-ubuntu.local:5800";
    public static String jetsonAddress = "http://10.59.37.54:5800";

    // public static VisionClient visionClient = new VisionClient(null); //you can put an address in the constructor to use something other than the default

    public static double camDistance = 10.0; //The distance between the two cameras in inches

    // Climber buttons back
    public static JoystickButton climbPistonBackExtend = new JoystickButton(leftThrust, BUTTON_SIDE_LEFT);
    public static JoystickButton climbPistonBackRetract = new JoystickButton(leftThrust, BUTTON_MIDDLE);

    // Climber buttons front
    public static JoystickButton climbPistonFrontExtend = new JoystickButton(rightThrust, BUTTON_SIDE_RIGHT);
    public static JoystickButton climbPistonFrontRetract = new JoystickButton(rightThrust, BUTTON_MIDDLE);

    public static void attachCommandsToButtons() {
        // Rear pistons
        climbPistonBackExtend.whenPressed(new ClimbPistonsBack(Pneumatics.PistonPosition.EXTEND));
        climbPistonBackRetract.whenPressed(new ClimbPistonsBack(Pneumatics.PistonPosition.RETRACT));
        
        // Front pistions
        climbPistonFrontExtend.whenPressed(new ClimbPistonsFront(Pneumatics.PistonPosition.EXTEND));
        climbPistonFrontRetract.whenPressed(new ClimbPistonsFront(Pneumatics.PistonPosition.RETRACT));
    }

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

    public static boolean getMidButton() {
        return rightThrust.getRawButton(BUTTON_MIDDLE);
    }
}