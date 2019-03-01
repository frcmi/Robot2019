package frc.robot.subsystems;

import frc.robot.lib.util.RobotMap;

import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;


public class Pneumatics extends Subsystem {
    public enum PistonPosition {
        EXTEND,
        RETRACT;
    }

    // Subsystems are singleton classes, so there should only be one of each class. Instead
    // of calling the constructor directly, the client should use getInstance to prevent duplication
    // of Subsystem objects.
    private static Pneumatics instance;

    public static Pneumatics getInstance() {
        if (instance==null) instance = new Pneumatics();
        return instance;
    }

    private DoubleSolenoid mainSol;
    
    private Pneumatics() {
        super();
        mainSol = RobotMap.sol;
        
    }
    
    //Sets default command for the system
    @Override
    protected void initDefaultCommand() {
        setDefaultCommand(null);
    }

    public void setSol(boolean value1, boolean value2) {
        if (value1) {
            mainSol.set(Value.kForward);
        } else if (value2) {
            mainSol.set(Value.kReverse);
        }
    }

    public void setClimberSolenoidBack(PistonPosition position) {
        if(position == PistonPosition.EXTEND) {
            RobotMap.liftBack.set(Value.kForward);
        } else {
            RobotMap.liftBack.set(Value.kReverse);
        }
    }

    public void setClimberSolenoidFront(PistonPosition position) {
        if(position == PistonPosition.EXTEND) {
            RobotMap.liftFront.set(Value.kForward);
        } else {
            RobotMap.liftFront.set(Value.kReverse);
        }
    }

    public void stop() {
    }
}