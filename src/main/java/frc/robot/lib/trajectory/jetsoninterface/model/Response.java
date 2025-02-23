package frc.robot.lib.trajectory.jetsoninterface.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Response {
    public ErrorInfo error;
    public boolean success;
    public double ts_request_mono;

    @JsonCreator
    public Response(
            @JsonProperty("success") boolean success,
            @JsonProperty("error") ErrorInfo error,
            @JsonProperty("ts_request_mono") double ts_request_mono)
    {
        this.error = error;
        this.success = success;
        this.ts_request_mono = ts_request_mono;
    }
}
