package frc.robot.commands;

import frc.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;


//Example of how to program an autonomous command
public class Autonomous1 extends AutonomousCommand {

    public String name; // The name to be displayed as a choice at the SmartDashboard

    public Autonomous1() {
        name = "Autonomous1";

        //Requires defines any subsystem dependencies, so more than one command can't
        //use a subsystem at the same time
        requires(driveTrain);
    }

    // Called when the command starts running
    @Override
    public void start() {
        super.start();
    }

    //Called periodically while the command is running
    @Override
    protected void execute() {
        driveTrain.updatePID();
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

    // Called when after isFinished returns true
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
        super.cancel();
    }
}