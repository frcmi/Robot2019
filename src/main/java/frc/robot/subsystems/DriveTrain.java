package frc.robot.subsystems;

import frc.robot.lib.util.PIDInfo;
import frc.robot.commands.Teleop;

import edu.wpi.first.wpilibj.command.Subsystem;

//Abstract class for a drive train. Drive train classes should extend this.
public abstract class DriveTrain extends Subsystem {

    public DriveTrain() {
    }

    public DriveTrain(String name) {
        super(name);
    }

    @Override
    protected void initDefaultCommand() {
        setDefaultCommand(new Teleop());
    }

    public void updatePID() {
        PIDInfo.getInstance().update();
    }

    public abstract void stop();

    public abstract void moveLeftDrive(double magnitude);

    public abstract void moveRightDrive(double magnitude);
}
