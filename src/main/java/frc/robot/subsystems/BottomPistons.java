package frc.robot.subsystems;

import frc.robot.commands.ControlPistons;
import frc.robot.lib.util.RobotMap;

import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;


public class BottomPistons extends Subsystem {

    // Subsystems are singleton classes, so there should only be one of each class. Instead
    // of calling the constructor directly, the client should use getInstance to prevent duplication
    // of Subsystem objects.
    private static BottomPistons instance;

    public static BottomPistons getInstance() {
        if (instance==null) instance = new BottomPistons();
        return instance;
    }

    private DoubleSolenoid frontPiston;
    private DoubleSolenoid backPiston;
    
    
    private BottomPistons() {
        super();
        frontPiston = RobotMap.liftFront;
        backPiston = RobotMap.liftBack;
    }
    
    //Sets default command for the system
    @Override
    protected void initDefaultCommand() {
        setDefaultCommand(new ControlPistons());
    }

    public void setBack(boolean firstVal, boolean secondVal) {
        if (firstVal) {
            backPiston.set(Value.kForward);
        } else if (secondVal) {
            backPiston.set(Value.kReverse);
        }
    }

    public void setFront(boolean firstVal, boolean secondVal) {
        if (firstVal) {
            frontPiston.set(Value.kForward);
        } else if (secondVal) {
            frontPiston.set(Value.kReverse);
        }
    }

    public void stop() {
    }
}