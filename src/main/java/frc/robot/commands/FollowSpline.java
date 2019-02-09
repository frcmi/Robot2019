package frc.robot.commands;

import frc.robot.Robot;
import frc.robot.lib.trajectory.Spline;
import frc.robot.lib.util.RobotMap;
import frc.robot.lib.util.PIDInfo;

import edu.wpi.first.wpilibj.command.Command;
import java.lang.Math.*;

//Follows a given spline, given the first point represents the initial position of the robot
public class FollowSpline extends CommandBase {
    public String name; // The name to be displayed as a choice at the SmartDashboard
    public Spline path;
    public Position position;
    public static final float distFromSplineWeightVal = 1f;
    public static final float deltaBearingWeightVal = 1f;
    public static final float splineConcavityWeightVal = 1f;


    public FollowSpline(Spline s) {
        name = "Follow Spline " + s.name;
        requires(driveTrain);
    }

    // Called when the command starts running
    @Override
    public void start() {
        position = new Position();
    }

    //Called periodically while the command is running
    @Override
    protected void execute() {
        position.updatePosition(PIDInfo.getInstance().getLeftEncoderDelta(), PIDInfo.getInstance().getRightEncoderDelta());
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

    //Keeps track of the position of the front center of the robot
    public class Position{
        float x = 0; //Measured in meters, from the initial position of the robot
        float y = 0; //Measured in meters, from the initial position of the robot
        float theta = 0; //Angle of the robot, measured in radians counterclockwise from the x axis
        
        // time interval is the how many seconds between calls to position
        // distBetweenWheels is the distance between wheels in meters
        public Position(){
            this.x = 0;
            this.y = 0;
            this.theta = (float) Math.PI / 2;
        }

        //Updates the x, y, and theta variables given encoder deltas
        public void updatePosition(float leftencoderdelta, float rightencoderdelta){
            float deltaTheta = (rightencoderdelta-leftencoderdelta)/((float) (2*Math.PI*RobotMap.distBetweenWheels));
            float averageTheta = this.theta + deltaTheta / 2;
            float averageTraversal = (leftencoderdelta + rightencoderdelta)/2;
            this.x += averageTraversal * Math.cos(averageTheta);
            this.y += averageTraversal * Math.sin(averageTheta);
            this.theta += deltaTheta;
        }
    }
}