package org.usfirst.frc.team5937.robot.commands;

import org.usfirst.frc.team5937.robot.Robot;
import edu.wpi.first.wpilibj.command.Command;

//Command to move the test motor
public class MoveTestMotor extends CommandBase {
    
    
    
    public MoveTestMotor() {
        //Requires defines any subsystem dependencies, so more than one command can't
        //use a subsystem at the same time
        requires(testMotor);
    }	

    // Called when the command starts running
    @Override
    public void start() {

    }

    // Called periodically while the command is running
    @Override
    protected void execute() {
        
    }

    // Called just before this Command runs for the first time
    @Override
    protected void initialize() {
        testMotor.moveForward();
    }


    // Make this return true when this Command no longer needs to run execute()
    @Override
    protected boolean isFinished() {
        return false;
    }

    // Called once after isFinished returns true
    @Override
    protected void end() {
        testMotor.stop();
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    @Override
    protected void interrupted() {
        end();
    }
    
    // Called when the command is manually cancelled from the SmartDashboard
    @Override
    public void cancel() {
        super.cancel();
    }

}