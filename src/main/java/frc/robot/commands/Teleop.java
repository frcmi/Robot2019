package frc.robot.commands;

import frc.robot.Robot;
import edu.wpi.first.wpilibj.command.Command;
import frc.robot.lib.util.RobotMap;
import frc.robot.subsystems.BallShooter;
import frc.robot.subsystems.Pneumatics;
import frc.robot.subsystems.TankDrive;

//Command to move the test motor
public class Teleop extends CommandBase {

    BallShooter shooter;
    Pneumatics hatchPusher;
    TankDrive drive;

    public Teleop() {
        //Requires defines any subsystem dependencies, so more than one command can't
        //use a subsystem at the same time
        requires(driveTrain);
        requires(pneumatics);
        // requires(BallShooter);
    }	

    // Called when the command starts running
    @Override
    public void start() {

    }

    // Called periodically while the command is running
    @Override
    protected void execute() {
        driveTrain.updatePID();
        drive.moveLeftDrive(RobotMap.getLeftY());
        drive.moveRightDrive(RobotMap.getRightY());
        shooter.shoot(RobotMap.getRightX());
        hatchPusher.setSol(RobotMap.getRightTrigger(), RobotMap.getLeftTrigger());
        
    }

    // Called just before this Command runs for the first time
    @Override



    protected void initialize() {
        System.out.println("Starting teleop");
        shooter = BallShooter.getInstance();
        hatchPusher = Pneumatics.getInstance();
        drive = TankDrive.getInstance();
    }


    // Make this return true when this Command no longer needs to run execute()
    @Override
    protected boolean isFinished() {
        return false;
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