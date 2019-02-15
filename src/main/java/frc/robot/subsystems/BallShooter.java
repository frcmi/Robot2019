package frc.robot.subsystems;

import frc.robot.lib.util.*;

import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.*;

public class BallShooter extends Subsystem {
    
    public static BallShooter instance;

    public Victor shooter;

    public static BallShooter getInstance() {
        if (instance == null) instance = new BallShooter();
        return instance;
    }

    private BallShooter() {
        super();
        shooter = RobotMap.ballShooter;
    }

    //Sets default command for the system
    @Override
    protected void initDefaultCommand() {
        setDefaultCommand(null);
    }

    public void shoot(double in) {
        shooter.set(in);
    }

    public void stop() {
        shooter.set(0.0);
    }


}

