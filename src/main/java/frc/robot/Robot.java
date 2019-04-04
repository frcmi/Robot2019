/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import frc.robot.commands.Teleop;
import frc.robot.commands.ControlLight;
import frc.robot.commands.ControlPistons;
import frc.robot.commands.ControlPneumatics;
import frc.robot.commands.ButtonCommandHandler;
import frc.robot.commands.ControlShooter;
import frc.robot.commands.ForwardCamera;
import frc.robot.lib.util.*;
import frc.robot.lib.trajectory.jetsoninterface.VisionPoller;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import java.util.*;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.properties file in the
 * project.
 */
public class Robot extends TimedRobot {
    public RobotInfo info; // Static information about the robot (team and starting position)

    /**
     * This function is run when the robot is first started up and should be used
     * for any initialization code.
     */
    @Override
    public void robotInit() {
        Scheduler.getInstance().enable();
        System.out.println("Robot initializing");
        info = new RobotInfo();
        //VisionPoller.makeInstance();

        SmartDashboard.putData("Commands", Scheduler.getInstance()); // Makes the SmartDashboard display the status of
                                                                     // running commands

    }

    // Called when autonomous mode starts. Starts the autonomous command
    @Override
    public void autonomousInit() {
        teleopInit();
        Scheduler.getInstance().removeAll();
    }

    // Called periodically during autonomous
    @Override
    public void autonomousPeriodic() {
        Scheduler.getInstance().run();
        // TODO: send status to the SmartDashboard
    }

    // Called when the robot is put into operator control mode
    @Override
    public void teleopInit() {
        Scheduler.getInstance().removeAll();
        System.out.println("Running teleopInit");
        new ButtonCommandHandler().start();
        new ForwardCamera().start();
    }

    // Called periodically during operator control period
    @Override
    public void teleopPeriodic() {
        Scheduler.getInstance().run();
    }

    // Called when the robot is put into test mode
    @Override
    public void testInit() {
        Scheduler.getInstance().removeAll();
    }

    // Called periodically during test mode
    @Override
    public void testPeriodic() {
        Scheduler.getInstance().run();
    }

    // Called when robot is put into disabled mode
    @Override
    public void disabledInit() {
        Scheduler.getInstance().removeAll();
    }

    // Called periodically when the robot is in disabled mode
    @Override
    public void disabledPeriodic() {
        Scheduler.getInstance().run();
    }
}