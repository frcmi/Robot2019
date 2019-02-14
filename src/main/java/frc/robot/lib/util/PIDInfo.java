package frc.robot.lib.util;

import com.kauailabs.navx.frc.AHRS;
import com.kauailabs.navx.AHRSProtocol;
import com.kauailabs.navx.AHRSProtocol.AHRSPosUpdate;
import com.kauailabs.navx.AHRSProtocol.BoardID;
import com.kauailabs.navx.IMUProtocol.YPRUpdate;

import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.interfaces.Accelerometer;

import java.util.*;
import java.lang.Float;
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
    public LinkedList<java.lang.Float> leftEncoderVals;
    public LinkedList<java.lang.Float> rightEncoderVals;
    
    public LinkedList<java.lang.Float> groundAngleVals; // Maybe change to getAngle (from getYaw)

    public LinkedList<java.lang.Float> compassVals;

    public LinkedList<java.lang.Float> vertAngleVals;

    public LinkedList<java.lang.Float> groundAngleDerivs;

    public double dist;
    public byte bitRate;

    private static final int NUM_STORED_VALUES = 500; //how much history is stored by this class

    public PIDInfo() {
        dist = 4.0; // Calculated distance per encoder pulse
        RobotMap.leftEncoder.setDistancePerPulse(dist);
        RobotMap.leftEncoder.setDistancePerPulse(dist);

        //all of these are stored so index zero is the most recent
        leftEncoderVals = new LinkedList<java.lang.Float>();
        rightEncoderVals = new LinkedList<java.lang.Float>();

        groundAngleVals = new LinkedList<java.lang.Float>();
        compassVals = new LinkedList<java.lang.Float>();
        vertAngleVals = new LinkedList<java.lang.Float>();
        groundAngleDerivs = new LinkedList<java.lang.Float>();
    }

    //Updates the state of everything. Should be called by
    public void update() {
        leftEncoderVals.addFirst(Float.valueOf((float) RobotMap.leftEncoder.getDistance()));
        // trimList(leftEncoderVals);
        rightEncoderVals.addFirst(Float.valueOf((float) RobotMap.rightEncoder.getDistance()));
        // trimList(rightEncoderVals);

        groundAngleVals.addFirst(Float.valueOf(RobotMap.navx.getYaw()));
        // trimList(groundAngleVals);
        compassVals.addFirst(Float.valueOf(RobotMap.navx.getCompassHeading()));
        // trimList(compassVals);
        vertAngleVals.addFirst(Float.valueOf(RobotMap.navx.getPitch()));
        // trimList(vertAngleVals);
        groundAngleDerivs.addFirst(Float.valueOf((float) RobotMap.navx.getRate()));
        // trimList(groundAngleDerivs);
    }
/*  NOTE: java doesn't like it when you use sublists of linkedlists, threw exception at runtime
    public void trimList(LinkedList<Float> list){
        if (list.size() > NUM_STORED_VALUES){
            list = (LinkedList<java.lang.Float>) list.subList(0, NUM_STORED_VALUES-1);
        }
    }
    */

    //TODO: make these functions more sophisticated using the encoders as well as the navx for calibration

    public float getLeftEncoderDelta(){
        return leftEncoderVals.get(0) - leftEncoderVals.get(1);
    }

    public float getRightEncoderDelta(){
        return rightEncoderVals.get(0) - rightEncoderVals.get(1);
    }

    public double getCurrentEncoderLeft() {
        return RobotMap.leftEncoder.getDistance();
    }

    public double getCurrentEncoderRight() {
        return RobotMap.rightEncoder.getDistance();
    }

    public float getGroundAngle() {
        return groundAngleVals.get(0);
    }

    public float getCompass() {
        return compassVals.get(0);
    }

    public float getVertAngle() {
        return vertAngleVals.get(0);
    }

    public float getGroundAngleDeriv() {
        return groundAngleDerivs.get(0);
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