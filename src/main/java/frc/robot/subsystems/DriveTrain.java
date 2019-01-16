package frc.robot.subsystems;

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
    }

}
