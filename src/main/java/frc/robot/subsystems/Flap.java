package frc.robot.subsystems;

import frc.robot.lib.util.RobotMap;
import edu.wpi.first.wpilibj.command.Subsystem;


public class Flap extends Subsystem {
    
    public static Flap instance;
    protected void initDefaultCommand() {
    }

    public static Flap getInstance() {
        if (instance==null) instance = new Flap();
        return instance;
    }

    public void moveFlap(double in) {
        RobotMap.hatchFlap.set(in);
    }



}