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

//Command to move the test motor
public class VisionDock extends CommandBase {
    Delta delta;
    AutoDock autoDocker;
    PathGenThread pathGenThread;
    EncoderFollower left;
    EncoderFollower right;
    boolean finished;

    public VisionDock() {
        // Requires defines any subsystem dependencies, so more than one command can't
        // use a subsystem at the same time
        requires(driveTrain);
        finished = false;
    }

    // Called when the command starts running
    @Override
    public void start() {
    }

    // Called just before this Command runs for the first time
    @Override
    protected void initialize() {
        this.delta = VisionPoller.getInstance().getRelativePosition();
        if (delta == null){
            finished = true;
            System.out.println("AutoDock: could not get target delta");
            return;
        }
        autoDocker = new AutoDock(this.delta);
        autoDocker.initialize();
    }

    // Called periodically while the command is running
    @Override
    protected void execute() {
        autoDocker.execute();
        if (autoDocker.isFinished()){
            finished = true;
        }
    }
    // Make this return true when this Command no longer needs to run execute()
    @Override
    protected boolean isFinished() {
        return finished;
    }

    // Called once after isFinished returns true
    @Override
    protected void end() {
        autoDocker.end();
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