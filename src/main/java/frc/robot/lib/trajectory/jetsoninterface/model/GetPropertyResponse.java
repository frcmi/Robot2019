package frc.robot.lib.trajectory.jetsoninterface.model;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class GetPropertyResponse extends Response {
    public Object data;

    @JsonCreator
    public GetPropertyResponse(
            @JsonProperty("success") boolean success,
            @JsonProperty("error") ErrorInfo error,
            @JsonProperty("ts_request_mono") double ts_request_mono,
            @JsonProperty("data") Object data)
    {
        super(success, error, ts_request_mono);
        this.data = data;
    }
}
