package frc.robot.commands;

import frc.robot.Robot;
import frc.robot.lib.trajectory.Spline;
import frc.robot.lib.util.RobotMap;

import edu.wpi.first.wpilibj.command.Command;

public class FollowSpline extends CommandBase {

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
            this.theta = Math.PI / 2;
        }

        //Updates the x, y, and theta variables given encoder deltas
        public void updatePosition(float leftencoderdelta, float rightencoderdelta){
            float deltaTheta = (rightencoderdelta-leftencoderdelta)/(2*Math.PI*RobotMap.distBetweenWheels)
            float averageTheta = this.theta + deltaTheta / 2;
            float averageTraversal = (leftencoderdelta + rightencoderdelta)/2;
            this.x += averageTraversal * Math.cos(averageTheta);
            this.y += averageTraversal * Math.sin(averageTheta);
            this.theta += deltaTheta;
        }

    }

    
    public String name; // The name to be displayed as a choice at the SmartDashboard
    public Spine path;
    public Position position;

    public FollowSpline(Spline s) {
        name = "Follow Spline";

        //Requires defines any subsystem dependencies, so more than one command can't
        //use a subsystem at the same time
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
}