package frc.robot.commands;

import frc.robot.subsystems.DriveTrain;
import frc.robot.subsystems.Pneumatics;
import frc.robot.subsystems.TankDrive;
import frc.robot.subsystems.BallShooter;
import frc.robot.subsystems.Camera;
import frc.robot.subsystems.Light;
import frc.robot.subsystems.BottomPistons;

import edu.wpi.first.wpilibj.command.Command;

// The base command class which has access to all subsystems and the controller OI. All commands 
// should extend this, so they also have this access. Define all subsystems as variables here.
public abstract class CommandBase extends Command {
    public static DriveTrain driveTrain; // Drive train subsystem
    public static Pneumatics pneumatics; // Pneumatics subsystem
    public static BallShooter ballShooter;
    public static Camera camera;
    public static Light light;
    public static BottomPistons bottomPistons;

    static {
        driveTrain = TankDrive.getInstance();
        pneumatics = Pneumatics.getInstance();
        ballShooter = BallShooter.getInstance();
        camera = Camera.getInstance();
        light = Light.getInstance();
        bottomPistons = BottomPistons.getInstance();
    }
}