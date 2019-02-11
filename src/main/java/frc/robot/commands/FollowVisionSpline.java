package frc.robot.commands;

import frc.robot.Robot;
import frc.robot.lib.trajectory.VisionSpline;
import frc.robot.lib.util.RobotMap;
import frc.robot.lib.util.PIDInfo;

import edu.wpi.first.wpilibj.command.Command;
import java.lang.Math.*;

//Follows a given spline, given the first point represents the initial position of the robot
public class FollowVisionSpline extends CommandBase {
    public String name; // The name to be displayed as a choice at the SmartDashboard
    public VisionSpline path;
    public Position position;
    public static final double angleDiffWeightVal = 1.0;
    public static final double distanceFromSplineWeightVal = 1.0;
    public static final double startSpeedDecreaseDistance = 2.0; //number of meters away from portal the robot starts slowing down


    public FollowVisionSpline(VisionSpline s) {
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

    // Returns the desired turning rate, in radians per meter

    // Three factors are considered in generating wanted turn rate: first, the
    // distance of the robot from the spline. Second, the difference between the angle
    // of the spline and the angle of the robot. Third, the angle change of the spline.
    public double getWantedTurnRate(){
        double splineX = path.getSplinePoint(position.x, position.y);
        double splineY = path.getY(splineX);
        double distanceFromSpline = Math.sqrt(Math.pow(position.x - splineX, 2) + Math.pow(position.y - splineY, 2));
        double angleDiff = path.angleAt(splineX) - position.theta;
        double angleChange = path.angleChangeAt(splineX);

        double returnVal = 0;

        returnVal += angleChange; //the base value is the turning rate of the spline
        returnVal += distanceFromSpline * distanceFromSplineWeightVal;
        returnVal += angleDiff * angleDiffWeightVal;

        return returnVal;
    }

    //Gets the desired motor power as a fraction of max motor power
    public double getWantedSpeed(){
        double distanceFromEnd = Math.sqrt(Math.pow(path.endpoint[0] - position.x,2) + Math.pow(path.endpoint[1] - position.y,2));

        double returnVal = distanceFromEnd * distanceFromEnd / (startSpeedDecreaseDistance*startSpeedDecreaseDistance);

        if (returnVal > 1.0){
            returnVal = 1.0;
        } else if (returnVal < 0.0){
            returnVal = 0.0;
        }
        return returnVal;
    }

    public class PIDController{
        private double P, I, D; //the weightings of each output
        private double integral, lastError;

        public PIDController(double P, double I, double D){
            this.P = P;
            this.I = I;
            this.D = D;
            this.lastError = 0;
            this.integral = 0;
        }

        //All turn rates are in radians per meter
        public double[] getOutput(double wantedTurnRate, double lastTurnRate){
            double error = lastTurnRate - wantedTurnRate;
            double derivative = error - lastError;
            lastError = error;
            integral += error;

            //Unfinished
            


            return new double[]{1.0, 1.0};
        }
    }

    //Keeps track of the position of the front center of the robot
    public class Position{
        public double x = 0; //Measured in meters, from the initial position of the robot
        public double y = 0; //Measured in meters, from the initial position of the robot
        public double theta = 0; //Angle of the robot, measured in radians counterclockwise from the x axis
        
        // time interval is the how many seconds between calls to position
        // distBetweenWheels is the distance between wheels in meters
        public Position(){
            this.x = 0;
            this.y = 0;
            this.theta = 0f;
        }

        //Updates the x, y, and theta variables given encoder deltas
        public void updatePosition(float leftencoderdelta, float rightencoderdelta){
            double deltaTheta = (rightencoderdelta-leftencoderdelta)/(2*Math.PI*RobotMap.distBetweenWheels);
            double averageTheta = (this.theta + deltaTheta) / 2;
            double averageTraversal = (leftencoderdelta + rightencoderdelta)/2;
            this.x += averageTraversal * Math.cos(averageTheta);
            this.y += averageTraversal * Math.sin(averageTheta);
            this.theta += deltaTheta;
        }
    }
}