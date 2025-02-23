package frc.robot.subsystems;

import edu.wpi.first.wpilibj.command.Subsystem;
import frc.robot.commands.ControlLight;
import frc.robot.lib.util.RobotMap;
import edu.wpi.first.wpilibj.*;

public class Light extends Subsystem {

    public Solenoid lightRing;
    private static Light instance;

    public static Light getInstance() {
        if (instance == null) instance = new Light();
        return instance;
    }

    public Light() {
        lightRing = RobotMap.ledRing;
    }

    @Override
    protected void initDefaultCommand() {
        setDefaultCommand(new ControlLight());
    }

    public void set(int state) {
        assert state == 1 || state == 0;
        lightRing.set(true ? state == 1 : false);
    }

    public void toggle() {
        lightRing.set(!lightRing.get());
    }
}
