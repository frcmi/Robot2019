package frc.robot.subsystems.lib;

import edu.wpi.first.wpilibj.SpeedController;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.ctre.phoenix.motorcontrol.ControlMode;
import java.util.*;

public class Tread<VictoRSPX> implements SpeedController {
    private List<VictorSPX> motors;
    private boolean isInverted = false;
    private double power;
    public Tread(VictorSPX... motorsArguments){
        this.isInverted = false;
        this.motors = new ArrayList<VictorSPX>();
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
        for (VictorSPX motor : this.motors){
            motor.set(ControlMode.Velocity, power);
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
        for (VictorSPX motor : this.motors){
            motor.set(ControlMode.Disabled, 0);
        }
    }
    @Override
    public void pidWrite(double output){
    }
}