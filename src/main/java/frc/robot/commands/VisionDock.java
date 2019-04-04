package frc.robot.commands;

import frc.robot.Robot;
import edu.wpi.first.wpilibj.command.Command;
import frc.robot.lib.util.RobotMap;
import frc.robot.lib.trajectory.jetsoninterface.VisionPoller;
import frc.robot.lib.trajectory.jetsoninterface.VisionPoller.Delta;
import frc.robot.commands.AutoDock;
import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Waypoint;
import jaci.pathfinder.Trajectory;
import jaci.pathfinder.modifiers.TankModifier;
import jaci.pathfinder.followers.EncoderFollower;
import edu.wpi.first.wpilibj.command.Scheduler;

//Command that starts AutoDock using vision delta
public class VisionDock extends CommandBase {
    public VisionDock() {
    }

    // Called when the command starts running
    @Override
    public void start() {
    }

    // Called just before this Command runs for the first time
    @Override
    protected void initialize() {
        Delta delta = VisionPoller.getInstance().getRelativePosition();
        if (delta == null){
            System.out.println("VisionDock: could not get target delta");
            return;
        }
        AutoDock autoDocker = new AutoDock(delta);
        Scheduler.getInstance().add(autoDocker);
    }

    // Called periodically while the command is running
    @Override
    protected void execute() {

    }
    // Make this return true when this Command no longer needs to run execute()
    @Override
    protected boolean isFinished() {
        return true;
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