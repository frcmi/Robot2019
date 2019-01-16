package frc.robot.commands;

//Abstract class for an autonomous command. Autonomous commands should extend this
public abstract class AutonomousCommand extends CommandBase implements Comparable<AutonomousCommand>{

    public String name;

    public int compareTo(AutonomousCommand cmd){
        return this.hashCode() - cmd.hashCode();
    }
}
