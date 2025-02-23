package frc.robot.subsystems;

import frc.robot.lib.util.*;
import frc.robot.commands.ControlShooter;

import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.*;

public class BallShooter extends Subsystem {

    public static BallShooter instance;

    public Victor shooter;

    public static BallShooter getInstance() {
        if (instance == null)
            instance = new BallShooter();
        return instance;
    }

    private BallShooter() {
        super();
        shooter = RobotMap.ballShooter;
    }

    // Sets default command for the system
    @Override
    protected void initDefaultCommand() {
        setDefaultCommand(new ControlShooter());
    }

    public void setMotor(boolean valOne, boolean valTwo) {
        if (valOne) {
            shooter.set(0.5);
        } else if (valTwo) {
            shooter.set(-0.5);
        } else {
            shooter.set(0.0);
        }
    }

    public void stop() {
        shooter.set(0.0);
    }

}
