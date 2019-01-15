package org.usfirst.frc.team5937.robot.subsystems;

import edu.wpi.first.wpilibj.*;

// All output and input objects (motors, cameras, etc) are defined here
public class RobotMap {
    public static Victor testMotor;
    static {
        testMotor = new Victor(1); // Test motor (set to null if not testing)
    }
}