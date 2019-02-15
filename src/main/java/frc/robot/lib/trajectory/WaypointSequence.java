package frc.robot.lib.trajectory;

import frc.robot.lib.util.SnailMath;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonValue;
import frc.robot.lib.trajectory.jetsoninterface.model.ErrorInfo;

//A WaypointSequence is a sequence of Waypoints.  #whatdidyouexpect
public class WaypointSequence {
    public static int defaultInitialAllocSize = 8;

    Waypoint[] waypoints_;
    int num_waypoints_ = 0;

    public WaypointSequence(int max_size)
    {
        waypoints_ = new Waypoint[max_size];
    }

    public WaypointSequence()
    {
        this(8);
    }

    public WaypointSequence(Waypoint[] waypoints)
    {
        if (waypoints == null) {
            waypoints_ = new Waypoint[0];
        } else {
            waypoints_ = waypoints;
            num_waypoints_ = waypoints.length;
        }
    }

    @JsonCreator
    public static WaypointSequence fromList(Waypoint[] waypoints)
    {
        return new WaypointSequence(waypoints);
    }

    public int addWaypoint(Waypoint w)
    {
        if (num_waypoints_ >= waypoints_.length) {
            int newLength = 2 * num_waypoints_;
            if (newLength < defaultInitialAllocSize) {
                newLength = defaultInitialAllocSize;
            }
            Waypoint[] newWaypoints = new Waypoint[newLength];
            for (int i = 0; i < num_waypoints_; i++) {
                newWaypoints[i] = waypoints_[i];
            }
            waypoints_ = newWaypoints;
        }
        waypoints_[num_waypoints_] = w;
        int result = num_waypoints_++;
        return result;
    }

    public int addWaypoint(double x, double y, double theta)
    {
        return addWaypoint(new Waypoint(x, y, theta));
    }

    public int getNumWaypoints()
    {
        return num_waypoints_;
    }

    public Waypoint getWaypoint(int index)
    {
        if (index >= 0 && index < getNumWaypoints()) {
            return waypoints_[index];
        } else {
            return null;
        }
    }

    public WaypointSequence invertY()
    {
        WaypointSequence inverted = new WaypointSequence(waypoints_.length);
        inverted.num_waypoints_ = num_waypoints_;
        for (int i = 0; i < num_waypoints_; ++i) {
            inverted.waypoints_[i] = waypoints_[i];
            inverted.waypoints_[i].y *= -1;
            inverted.waypoints_[i].theta = SnailMath
                    .boundAngle0to2PiRadians(2 * Math.PI - inverted.waypoints_[i].theta);
        }

        return inverted;
    }

    @JsonValue
    public Waypoint[] toList()
    {
        Waypoint[] result;
        if (waypoints_.length == num_waypoints_) {
            result = waypoints_;
        } else {
            result = new Waypoint[num_waypoints_];
            for (int i = 0; i < num_waypoints_; ++i) {
                result[i] = waypoints_[i];
            }
        }
        return result;
    }
}