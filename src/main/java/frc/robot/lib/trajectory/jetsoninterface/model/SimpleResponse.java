package frc.robot.lib.trajectory.jetsoninterface.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SimpleResponse extends Response {
    public Object data;

    @JsonCreator
    public SimpleResponse(
            @JsonProperty("success") boolean success,
            @JsonProperty("error") ErrorInfo error,
            @JsonProperty("ts_request_mono") double ts_request_mono,
            @JsonProperty("data") Object data)
    {
        super(success, error, ts_request_mono);
        this.data = data;
    }
}
