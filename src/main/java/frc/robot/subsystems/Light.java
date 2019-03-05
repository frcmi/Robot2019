package frc.robot.subsystems;

import edu.wpi.first.wpilibj.command.Subsystem;
import frc.robot.subsystems.Pneumatics;
import frc.robot.lib.util.RobotMap;
import edu.wpi.first.wpilibj.*;

public class Light extends Subsystem {

    public Solenoid lightRing;
    private static Light instance;

    public static Light getInstance() {
        if (instance==null) instance = new Light();
        return instance;
    }

    public Light() {
        lightRing = RobotMap.ledRing;
    }

    @Override
    protected void initDefaultCommand() {
    }

    public void set(int state) {
        assert state == 1 || state == 0;
        if (state == 1) {
            lightRing.set(true);
        } else{
            lightRing.set(false);
        }
    }

    public void toggle(){
        lightRing.set(!lightRing.get());
    }
}
