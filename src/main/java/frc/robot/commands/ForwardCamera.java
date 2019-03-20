package frc.robot.commands;

import frc.robot.commands.lib.CameraThread;

//Command to forward cammara info to the SmartDashboard
public class ForwardCamera extends CommandBase {
    private CameraThread thread;
    private boolean isFinished;

    public ForwardCamera() {
        System.out.println("Creating ForwardCamera command");
        requires(camera);
        thread = new CameraThread();
    }

    // Called when the command starts running
    @Override
    public void start() {
        System.out.println("Starting CameraThread");
    }

    // Called periodically while the command is running
    @Override
    protected void execute() {

    }

    // Called just before this Command runs for the first time
    @Override
    protected void initialize() {
        System.out.println("Initializing forwardCamera command");
        thread.start();

    }

    // Make this return true when this Command no longer needs to run execute()
    @Override
    protected boolean isFinished() {
        return isFinished;
    }

    // Called once after isFinished returns true
    @Override
    protected void end() {
        thread.stopRunning();
        isFinished = true;
        System.out.println("Ending forwardCamera command");
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
        end();
    }

    public void stop() {
        end();
    }
}