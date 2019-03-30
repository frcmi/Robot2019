package frc.robot.subsystems;

import frc.robot.lib.util.RobotMap;
import edu.wpi.first.wpilibj.command.Subsystem;


public class Flap extends Subsystem {
    
    public static Flap instance;

    public static Flap getInstance() {
        if (instance==null) instance = new Flap();
        return instance;
    }
    protected void initDefaultCommand() {}

    public static final int ENC_UP_VAL = 0;
    public static final int ENC_DOWN_VAL = 100;
    public static final int MAX_SPEED = 128;
    public static final boolean MOTOR_ENC_INVERTED = false;
    public static final double P = 100.0;

    public boolean direction = false; //True means move up, false means move down

    public void set(boolean direction){
        this.direction = direction;
    }

    public void move(){
        int encVal = 1; //TODO @AsKthul get encoder value here

        //unit vectors for each direction
        int upEncDir = (ENC_UP_VAL - ENC_DOWN_VAL)/Math.abs(ENC_UP_VAL-ENC_DOWN_VAL);
        int encDir = direction ? upEncDir : -1 * upEncDir;
        int motorDir = MOTOR_ENC_INVERTED ? -1*encDir : encDir;

        int targetEncVal = direction ? ENC_UP_VAL : ENC_DOWN_VAL;

        double diff = (double) Math.abs(encVal - targetEncVal);

        RobotMap.hatchFlap.set(P*diff/(Math.abs(ENC_UP_VAL - ENC_DOWN_VAL)) * motorDir);
    }



}