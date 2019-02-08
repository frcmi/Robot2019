package frc.robot.commands;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.lib.util.PIDInfo;

//Command to move the test motor
public class UpdateInfo extends Command {
    public UpdateInfo() {
        //No dependencies, this doesn't use any motors
    }	

    // Called when the command starts running
    @Override
    public void start() {

    }

    // Called periodically while the command is running
    @Override
    protected void execute() {
        PIDInfo.getInstance().update();
    }

    // Called just before this Command runs for the first time
    @Override
    protected void initialize() {

    }


    // Make this return true when this Command no longer needs to run execute()
    @Override
    protected boolean isFinished() {
        return false;
    }

    // Called once after isFinished returns true
    @Override
    protected void end() {

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

    }
}