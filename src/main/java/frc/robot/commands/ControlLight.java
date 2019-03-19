package frc.robot.commands;

import frc.robot.Robot;
import edu.wpi.first.wpilibj.command.Command;
import frc.robot.lib.util.RobotMap;
import frc.robot.subsystems.BallShooter;
import frc.robot.subsystems.Pneumatics;
import frc.robot.subsystems.TankDrive;
import frc.robot.subsystems.Light;

//Command to move the test motor
public class ControlLight extends CommandBase {

    public ControlLight() {
        // Requires defines any subsystem dependencies, so more than one command can't
        // use a subsystem at the same time
        requires(light);
    }

    // Called when the command starts running
    @Override
    public void start() {

    }

    // Called periodically while the command is running
    private boolean lastButtonState = false;

    @Override
    protected void execute() {
        if (RobotMap.getLeftRight() && !lastButtonState) {
            light.toggle();
        }

        lastButtonState = RobotMap.getLeftRight();
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
        light.set(0);
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