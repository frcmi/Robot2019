package frc.robot.commands;

import frc.robot.lib.util.RobotMap;
import edu.wpi.first.wpilibj.command.Scheduler;

public class ControlFlap extends CommandBase {
    private int lastInput = -1;
    private boolean goDown = false;
    private boolean goUp = false;
    
    public ControlFlap() {
        requires(flap);
        //Requires defines any subsystem dependencies, so more than one command can't
        //use a subsystem at the same time
    }

    // Called periodically while the command is running
    @Override
    protected void execute() {
        if (!RobotMap.bottomSwitch.get()){
            goDown = false;
        }
        if (!RobotMap.topSwitch.get()) {
            goUp = false;
        }
        if (goDown) {
            flap.setMotor(-0.5);
        } else if (goUp) {
            flap.setMotor(0.5);
        } else {
            if (RobotMap.getLeftHat() == 0) {
                flap.setMotor(0.5);
            } else if(RobotMap.getLeftHat() == 180 && RobotMap.bottomSwitch.get()) {
                flap.setMotor(-0.5);
                goDown = true;
            } else if (RobotMap.leftWhiteButton() && RobotMap.rightLeftWhite() && RobotMap.rightRightWhite()) {
                flap.setMotor(0.5);
                
                goUp = true;
            } else {
                flap.setMotor(0.0);
            }
        }

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