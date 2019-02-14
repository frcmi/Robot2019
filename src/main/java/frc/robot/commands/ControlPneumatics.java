package frc.robot.commands;

import frc.robot.Robot;
import frc.robot.lib.util.RobotMap;
import edu.wpi.first.wpilibj.command.Command;

//Command to move the test motor
public class ControlPneumatics extends CommandBase {
    public ControlPneumatics() {
        //Requires defines any subsystem dependencies, so more than one command can't
        //use a subsystem at the same time
        requires(pneumatics);
    }	

    // Called when the command starts running
    @Override
    public void start() {

    }

    // Called periodically while the command is running
    @Override
    protected void execute() {
        pneumatics.setSol(RobotMap.getRightTrigger(), RobotMap.getLeftTrigger());
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
        pneumatics.stop();
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
        pneumatics.stop();
        super.cancel();
    }
}