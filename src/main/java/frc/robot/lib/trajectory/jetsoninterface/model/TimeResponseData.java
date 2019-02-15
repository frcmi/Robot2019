package frc.robot.lib.trajectory.jetsoninterface.model;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class TimeResponseData {
    public double ts_request_mono;

    @JsonCreator
    public TimeResponseData(
            @JsonProperty("ts_request_mono") double ts_request_mono)
    {
        this.ts_request_mono = ts_request_mono;
    }
}
