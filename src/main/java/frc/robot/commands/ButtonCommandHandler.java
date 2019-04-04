package frc.robot.commands;
import frc.robot.lib.util.Delta;
import frc.robot.commands.AutoDock;
import frc.robot.Robot;
import edu.wpi.first.wpilibj.command.Command;
import frc.robot.lib.util.RobotMap;
import frc.robot.subsystems.BallShooter;
import frc.robot.subsystems.Pneumatics;
import frc.robot.subsystems.TankDrive;
import frc.robot.subsystems.Light;


public class ButtonCommandHandler extends CommandBase {

    public ButtonCommandHandler() {
    }

    boolean lastAutoTestButton = false;
    // Called periodically while the command is running
    @Override
    protected void execute() {

        // Auto test button mapping
        boolean autoTestButton = RobotMap.leftWhiteButton(); //make that here
        if (autoTestButton != lastAutoTestButton && autoTestButton == true){
            new AutoDock(new Delta(2, 1, Math.PI/3, (long) 0)).start();
        }

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
        super.cancel();
    }
}