package frc.robot.commands;

import frc.robot.subsystems.DriveTrain;
import frc.robot.subsystems.TestMotor;
import frc.robot.subsystems.TankDrive;

import edu.wpi.first.wpilibj.command.Command;

// The base command class which has access to all subsystems and the controller OI. All commands 
// should extend this, so they also have this access.
public abstract class CommandBase extends Command {
    public static DriveTrain driveTrain; //Drive train subsystem
    public static TestMotor testMotor; //TestMotor subsystem
    public static TankDrive tankDrive;

    static {
        testMotor = TestMotor.getInstance();
        //TODO driveTrain constructor
    }
}