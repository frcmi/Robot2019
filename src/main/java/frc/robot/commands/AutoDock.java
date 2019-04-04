package frc.robot.commands;

import frc.robot.Robot;
import edu.wpi.first.wpilibj.command.Command;
import frc.robot.lib.util.RobotMap;
import frc.robot.lib.trajectory.jetsoninterface.VisionPoller;
import frc.robot.lib.util.Delta;
import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Waypoint;
import jaci.pathfinder.Trajectory;
import jaci.pathfinder.modifiers.TankModifier;
import jaci.pathfinder.followers.EncoderFollower;

//Command to move the test motor
public class AutoDock extends CommandBase {
    Delta delta;
    PathGenThread pathGenThread;
    EncoderFollower left;
    EncoderFollower right;
    public boolean finished;

    public AutoDock(Delta delta) {
        // Requires defines any subsystem dependencies, so more than one command can't
        // use a subsystem at the same time
        requires(driveTrain);
        this.delta = delta;
        finished = false;
    }

    // Called just before this Command runs for the first time
    @Override
    protected void initialize() {
        driveTrain.moveLeftDrive(0.0);
        driveTrain.moveRightDrive(0.0);

        //Do path generation in a seperate thread as it might take more than 20ms
        pathGenThread = new PathGenThread(this);
        pathGenThread.start();
    }

    // Called periodically while the command is running
    @Override
    protected void execute() {
        //Exit if finished or path is still generating
        if (finished || pathGenThread.isAlive()){
            return;
        }
        /*
        // ---- getting a new delta ----
        Delta newDelta = VisionPoller.getInstance().getRelativePosition();
        if(newDelta != null && newDelta != delta){
            delta = newDelta;
        }
        long timeDiff = System.nanoTime() - delta.timeStamp;
        // ---- this is not currently used ----
        */

        double l = left.calculate((int) RobotMap.getLeftEncoder());
        double r = right.calculate((int) RobotMap.getRightEncoder());

        double gyroHeading = RobotMap.getGyroHeading();
        double desiredHeading = Pathfinder.r2d(left.getHeading());
        double angleDifference = Pathfinder.boundHalfDegrees(desiredHeading - gyroHeading);
        double turn = 0.8 * (-1.0/80.0) * angleDifference;

        driveTrain.moveLeftDrive(l+turn);
        driveTrain.moveRightDrive(r-turn);

        if(left.isFinished() && right.isFinished()){
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
        driveTrain.stop();
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

class PathGenThread extends Thread {
    AutoDock dockingCommand;
    public PathGenThread(AutoDock dockingCommand){
        this.dockingCommand = dockingCommand;
    }

    @Override
    public void run(){
        // Code to generate a trajectory for a AutoDock command object
        System.out.println("Pathfinding to delta fetched " + (System.nanoTime() - dockingCommand.delta.timeStamp) + " mS ago:");
        dockingCommand.delta.print();

        double d = RobotMap.stopDistanceFromHatch;
        double theta = dockingCommand.delta.theta;
        double x = dockingCommand.delta.x - d * Math.cos(theta);
        double y = dockingCommand.delta.y - d * Math.sin(theta);

        long runStartTime = System.nanoTime();
        Waypoint[] points = new Waypoint[] {
            new Waypoint(0.0, 0.0, 0.0),
            new Waypoint(x, y, theta)
        };
        double timeStep = RobotMap.commandUpdateInterval; //in seconds
        double maxVelocity = RobotMap.maxVelocity; // in m/s
        double maxAcceleration = RobotMap.maxAcceleration; // in m/s^2
        double maxJerk = RobotMap.maxJerk; // in m/s^3
        Trajectory.Config config = new Trajectory.Config(Trajectory.FitMethod.HERMITE_CUBIC, Trajectory.Config.SAMPLES_FAST, 
                    timeStep, maxVelocity, maxAcceleration, maxJerk);
        
        Trajectory trajectory = Pathfinder.generate(points, config);
        double wheelbaseWidth = RobotMap.distBetweenWheels; //should be in meters

        TankModifier modifier = new TankModifier(trajectory);
        modifier.modify(wheelbaseWidth);
        dockingCommand.left = new EncoderFollower(modifier.getLeftTrajectory());
        dockingCommand.right = new EncoderFollower(modifier.getRightTrajectory());
        int leftEncoderPosition = (int) RobotMap.getLeftEncoder();
        int rightEncoderPosition = (int) RobotMap.getRightEncoder();
        dockingCommand.left.configureEncoder(leftEncoderPosition, RobotMap.encoderTicksPerRevolution, RobotMap.wheelDiameter);
        dockingCommand.right.configureEncoder(rightEncoderPosition, RobotMap.encoderTicksPerRevolution, RobotMap.wheelDiameter);

        dockingCommand.left.configurePIDVA(RobotMap.position, RobotMap.integral, RobotMap.derivative, 1.0 / maxVelocity, RobotMap.accelerationGain);
        dockingCommand.right.configurePIDVA(RobotMap.position, RobotMap.integral, RobotMap.derivative, 1.0 / maxVelocity, RobotMap.accelerationGain);

        RobotMap.navx.zeroYaw();
        System.out.println("Finished path generation after " + (int) (System.nanoTime() - runStartTime) + " ms");
    }
}