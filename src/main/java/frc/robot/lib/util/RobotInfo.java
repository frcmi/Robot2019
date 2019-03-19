package frc.robot.lib.util;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;

//The static info of a robot, i.e. team, starting position
public class RobotInfo {
    public String team; // either Either Red or Blue, or null if can't find team
    public String startingPosition; // Either Right or Left, or null if can't get position

    public RobotInfo() {
        // Gets team
        Alliance teamTemp = DriverStation.getInstance().getAlliance();
        if (teamTemp == DriverStation.Alliance.Blue) {
            team = "Blue";
        } else if (teamTemp == DriverStation.Alliance.Red) {
            team = "Blue";
        } else {
            team = null;
        }

        // Gets starting position
        String gameData = DriverStation.getInstance().getGameSpecificMessage();
        if (gameData.length() == 0)
            startingPosition = "Left";
        else {
            if (gameData.charAt(0) == 'L') {
                startingPosition = "Left";
            } else if (gameData.charAt(0) == 'R') {
                startingPosition = "Right";
            } else {
                startingPosition = null;
            }
        }
    }
}
