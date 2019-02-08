package frc.robot.lib.util;

import com.kauailabs.navx.frc.AHRS;
import com.kauailabs.navx.AHRSProtocol;
import com.kauailabs.navx.AHRSProtocol.AHRSPosUpdate;
import com.kauailabs.navx.AHRSProtocol.BoardID;
import com.kauailabs.navx.IMUProtocol.YPRUpdate;

import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.interfaces.Accelerometer;

import java.util.*;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.GyroBase;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PIDSourceType;
import edu.wpi.first.wpilibj.SPI;


//Logs encoder, compass, gyroscope, and accelerometer movements. Handled by the DriveTrain
public class PIDInfo {
   
    private static PIDInfo instance;
    public static PIDInfo getInstance() {
        if (instance==null) instance = new PIDInfo();
        return instance;
    }
    public ArrayList<Double> encoderVals;
    
    public ArrayList<Float> groundAngleVals; // Maybe change to getAngle (from getYaw)

    public ArrayList<Float> compassVals;

    public ArrayList<Float> vertAngleVals;

    public ArrayList<Double> groundAngleDerivs;

    public double dist;
    public byte bitRate;

    public PIDInfo() {
        dist = 4.0; // Calculated distance per encoder pulse
        RobotMap.leftEncoder.setDistancePerPulse(dist);
        RobotMap.leftEncoder.setDistancePerPulse(dist);

        groundAngleVals = new ArrayList<Float>();
        compassVals = new ArrayList<Float>();
        vertAngleVals = new ArrayList<Float>();
        groundAngleDerivs = new ArrayList<Double>();
    }

    //Updates 
    public void update() {
        encoderVals.add(RobotMap.leftEncoder.getDistance());
        encoderVals.add(right.getDistance());

        groundAngleVals.add(RobotMap.navx.getYaw());
        compassVals.add(RobotMap.navx.getCompassHeading());
        vertAngleVals.add(RobotMap.navx.getPitch());
        groundAngleDerivs.add(RobotMap.navx.getRate());
    }

    //TODO: make these functions more sophisticated using the encoders as well as the navx for calibration

    public double getCurrentEncoderLeft() {
        return RobotMap.leftEncoder.getDistance();
    }

    public double getCurrentEncoderRight() {
        return right.getDistance();
    }

    public List<Double> getEncoderHistory() {
        return encoderVals;
    }

    public float getGroundAngle() {
        return RobotMap.navx.getYaw();
    }

    public float getCompass() {
        return RobotMap.navx.getCompassHeading();
    }

    public float getVertAngle() {
        return RobotMap.navx.getPitch();
    }

    public double getGroundAngleDeriv() {
        return RobotMap.navx.getRate();
    }

    public boolean isMoving() {
        return RobotMap.navx.isMoving();
    }

    public boolean isRotating() {
        return RobotMap.navx.isRotating();
    }

    public float speed() {
        return RobotMap.navx.getVelocityX();
    }

    public float turnSpeed() {
        return RobotMap.navx.getVelocityZ();
    }

    public float acceleration() {
        return RobotMap.navx.getWorldLinearAccelX();
    }

    public float turnAcceleration() {
        return RobotMap.navx.getWorldLinearAccelZ();
    }
}