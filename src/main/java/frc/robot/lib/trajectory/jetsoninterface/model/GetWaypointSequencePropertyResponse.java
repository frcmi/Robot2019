package frc.robot.lib.trajectory.jetsoninterface.model;

import frc.robot.lib.trajectory.WaypointSequence;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class GetWaypointSequencePropertyResponse extends Response {
    public WaypointSequence data;

    @JsonCreator
    public GetWaypointSequencePropertyResponse(
            @JsonProperty("success") boolean success,
            @JsonProperty("error") ErrorInfo error,
            @JsonProperty("ts_request_mono") double ts_request_mono,
            @JsonProperty("data") WaypointSequence data)
    {
        super(success, error, ts_request_mono);
        this.data = data;
    }
}
