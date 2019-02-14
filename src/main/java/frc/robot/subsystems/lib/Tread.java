package frc.robot.subsystems.lib;

import edu.wpi.first.wpilibj.*;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.ctre.phoenix.motorcontrol.ControlMode;
import java.util.*;

// Changed class name from Tread<VictorSPX> to Tread (not using CAN)
public class Tread implements SpeedController {
    private List<Victor> motors;
    private boolean isInverted = false;
    private double power;
    public Tread(Victor... motorsArguments){
        this.isInverted = false;
        this.motors = new ArrayList<Victor>();
        for (int i = 0; i < motorsArguments.length; i++){
            this.motors.add(motorsArguments[i]);
        }
        this.power = 0.0;
    }
    //Returns current power of motors
    @Override
    public double get(){
        return power;
    }

    //Returns whether this tread is inverted
    @Override
    public boolean getInverted(){
        return isInverted;
    }
    //Sets the power to the motors
    @Override
    public void set(double power){
        this.power = power;
        for (Victor motor : this.motors) {
            motor.set(power);
        }
    }
    //Sets whether the motors are inverted
    @Override
    public void setInverted(boolean isInverted){
        this.isInverted = isInverted;
    }
    //Stops the tread
    @Override
    public void stopMotor(){
        this.set(0.0);
    }
    //Disables this tread
    @Override
    public void disable(){
        for (Victor motor : this.motors){
            motor.set(0.0);
        }
    }
    @Override
    public void pidWrite(double output){
    }
}