package org.usfirst.frc.team5937.robot.commands;

import org.usfirst.frc.team5937.robot.subsystems.DriveTrain;
import org.usfirst.frc.team5937.robot.subsystems.TestMotor;

import edu.wpi.first.wpilibj.command.Command;

// The base command class which has access to all subsystems and the controller OI. All commands 
// should extend this, so they also have this access.
public abstract class CommandBase extends Command {
    public static DriveTrain driveTrain; //Drive train subsystem
    public static TestMotor testMotor; //TestMotor subsystem
    
    static {
        testMotor = TestMotor.getInstance();
        //TODO driveTrain constructor
    }
}