package frc.robot.subsystems;

import frc.robot.lib.util.RobotMap;
import frc.robot.commands.ControlFlap;
import edu.wpi.first.wpilibj.command.Subsystem;


public class Flap extends Subsystem {
    
    public static Flap instance;

    public static Flap getInstance() {
        if (instance==null) instance = new Flap();
        return instance;
    }

    private int position;
    private int lastHall;

    public Flap(){
        position = 0;
        lastHall = 0;
    }

    protected void initDefaultCommand() {
        setDefaultCommand(new ControlFlap());
    }

    public void setMotor(double magnitude){
        RobotMap.hatchFlap.set(magnitude);
        System.out.println(getPosition());
    }

    public double getPosition(){
        double motorMagnitude = RobotMap.hatchFlap.get();
        int currentHall = RobotMap.getHall();

        int direction;
        if (Math.abs(motorMagnitude) < 0.001){
            direction = 0;
        } else{
            direction = (int) Math.round(motorMagnitude / Math.abs(motorMagnitude));
        }

        int displacementMagnitude = currentHall - lastHall;
        lastHall = currentHall;

        position += displacementMagnitude * direction;

        return position;
    }

    public void stop(){
        setMotor(0.0);
    }
}