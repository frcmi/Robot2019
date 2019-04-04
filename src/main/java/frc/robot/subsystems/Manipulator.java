package frc.robot.subsystems;

import edu.wpi.first.wpilibj.command.Subsystem;

//Abstract class for a drive train. Drive train classes should extend this.
public abstract class Manipulator extends Subsystem {

    public Manipulator() {
    }

    public Manipulator(String name) {
        super(name);
    }

    @Override
    protected void initDefaultCommand() {
    }

    public abstract void stop();
}