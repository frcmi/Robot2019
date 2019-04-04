package frc.robot.commands;

import frc.robot.Robot;
import edu.wpi.first.wpilibj.command.Command;
import frc.robot.lib.util.RobotMap;
import frc.robot.subsystems.BallShooter;
import frc.robot.subsystems.Pneumatics;
import frc.robot.subsystems.TankDrive;
import frc.robot.subsystems.Light;

//Command to move the test motor
public class MoveFlap extends CommandBase {
    private int direction = 0;

    //Direction -1 means move down, 0 means keep still, 1 means move up
    public MoveFlap(int direction) {
        // Requires defines any subsystem dependencies, so more than one command can't
        // use a subsystem at the same time
        requires(flap);
        this.direction = direction;
    }

    // Called periodically while the command is running
    @Override
    protected void execute() {
        
    }

    // Called just before this Command runs for the first time
    @Override

    protected void initialize() {
    }

    // Make this return true when this Command no longer needs to run execute()
    @Override
    protected boolean isFinished() {
        return true;
    }

    // Called once after isFinished returns true
    @Override
    protected void end() {
        flap.stop();
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