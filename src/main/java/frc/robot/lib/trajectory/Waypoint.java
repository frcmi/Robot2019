package frc.robot.lib.trajectory;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class Waypoint {
    @JsonCreator
    public Waypoint(
            @JsonProperty("x") double x,
            @JsonProperty("y") double y,
            @JsonProperty("theta") double theta) {
        this.x = x;
        this.y = y;
        this.theta = theta;
    }

    public Waypoint(Waypoint tocopy) {
        this.x = tocopy.x;
        this.y = tocopy.y;
        this.theta = tocopy.theta;
    }

    @JsonProperty("x")
    public double x;

    @JsonProperty("y")
    public double y;

    @JsonProperty("theta")
    public double theta;
}
