package frc.robot.subsystems;

import frc.robot.lib.util.RobotMap;
import frc.robot.commands.ControlPneumatics;

import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;

public class Pneumatics extends Subsystem {

    // Subsystems are singleton classes, so there should only be one of each class.
    // Instead
    // of calling the constructor directly, the client should use getInstance to
    // prevent duplication
    // of Subsystem objects.
    private static Pneumatics instance;

    public static Pneumatics getInstance() {
        if (instance == null)
            instance = new Pneumatics();
        return instance;
    }

    private DoubleSolenoid mainSol;

    private Pneumatics() {
        super();
        mainSol = RobotMap.sol;

    }

    // Sets default command for the system
    @Override
    protected void initDefaultCommand() {
        setDefaultCommand(new ControlPneumatics());
    }

    public void setSol(boolean a, boolean b) {
        if (a) {
            mainSol.set(Value.kForward);
        } else if (b) {
            mainSol.set(Value.kReverse);
        }
        
    }

    public void stop() {
    }
}