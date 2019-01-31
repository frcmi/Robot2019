package frc.robot.util;

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
   
    public Encoder left;
    public Encoder right;
    public ArrayList<Double> encoderVals;

    public AHRS ahrs;
    
    public ArrayList<Float> groundAngleVals; // Maybe change to getAngle (from getYaw)

    public ArrayList<Float> compassVals;

    public ArrayList<Float> vertAngleVals;

    public ArrayList<Double> groundAngleDerivs;

    public double dist;
    public byte bitRate;

    public PIDInfo() {
        dist = 4.0; // Calculated distance per encoder pulse
        left.setDistancePerPulse(dist);
        right.setDistancePerPulse(dist);
        bitRate = 100;  // Updates per second of AHRS

        ahrs = new AHRS(SPI.Port.kMXP, bitRate);

        groundAngleVals = new ArrayList<Float>();
        compassVals = new ArrayList<Float>();
        vertAngleVals = new ArrayList<Float>();
        groundAngleDerivs = new ArrayList<Double>();
    }

    //Updates 
    public void update() {
        encoderVals.add(left.getDistance());
        encoderVals.add(right.getDistance());

        groundAngleVals.add(ahrs.getYaw());
        compassVals.add(ahrs.getCompassHeading());
        vertAngleVals.add(ahrs.getPitch());
        groundAngleDerivs.add(ahrs.getRate());
    }

    public double getCurrentEncoderLeft() {
        return left.getDistance();
    }

    public double getCurrentEncoderRight() {
        return right.getDistance();
    }

    public List<Double> getEncoderHistory() {
        return encoderVals;
    }

    public float getGroundAngle() {
        return ahrs.getYaw();
    }

    public float getCompass() {
        return ahrs.getCompassHeading();
    }

    public float getVertAngle() {
        return ahrs.getPitch();
    }

    public double getGroundAngleDeriv() {
        return ahrs.getRate();
    }

    public boolean isMoving() {
        return ahrs.isMoving();
    }

    public boolean isRotating() {
        return ahrs.isRotating();
    }

    public float speed() {
        return ahrs.getVelocityX();
    }

    public float turnSpeed() {
        return ahrs.getVelocityZ();
    }

    public float acceleration() {
        return ahrs.getWorldLinearAccelX();
    }

    public float turnAcceleration() {
        return ahrs.getWorldLinearAccelZ();
    }



}