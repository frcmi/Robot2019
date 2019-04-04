package frc.robot.commands;

import frc.robot.Robot;
import frc.robot.lib.util.RobotMap;
import edu.wpi.first.wpilibj.command.Command;

//Command to move the test motor
public class ControlShooter extends CommandBase {

    public ControlShooter() {
        // Requires defines any subsystem dependencies, so more than one command can't
        // use a subsystem at the same time
        requires(ballShooter);
    }

    // Called periodically while the command is running
    @Override
    protected void execute() {
        ballShooter.setMotor(RobotMap.getRightHat() == 0, RobotMap.getRightHat() == 180);
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
        ballShooter.stop();
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
        ballShooter.stop();
        super.cancel();
    }
}