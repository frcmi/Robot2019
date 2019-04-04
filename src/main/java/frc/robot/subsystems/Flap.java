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

    public void setMotor(double magnitude){
        RobotMap.hatchFlap.set(magnitude);
    }

    public void stop(){
        setMotor(0.0);
    }
}