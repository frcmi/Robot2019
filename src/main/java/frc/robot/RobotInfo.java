package frc.robot;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;

//The static info of a robot, i.e. team, starting position, autonomous
public class RobotInfo {
    public String team; //either Either Red or Blue, or null if can't find team
    public String startingPosition; //Either Right or Left, or null if can't get position

    public RobotInfo() {
        // Gets team
        Alliance teamTemp = DriverStation.getInstance().getAlliance();
        team = null;

        switch (teamTemp) {
            case Blue: team = "Blue"; break;
            case Red: team = "Red"; break;
        }

        // Gets starting position
        String gameData = DriverStation.getInstance().getGameSpecificMessage();
        startingPosition = null;

        switch (gameData.charAt(0)) {
            case 'L': startingPosition = "Left"; break;
            case 'R': startingPosition = "Right"; break;
        }
    }
}
