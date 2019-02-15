package frc.robot.lib.trajectory.jetsoninterface.model;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class TargetInfoResponse extends Response {
    public TargetInfoResponseData data;

    @JsonCreator
    public TargetInfoResponse(
            @JsonProperty("success") boolean success,
            @JsonProperty("error") ErrorInfo error,
            @JsonProperty("ts_request_mono") double ts_request_mono,
            @JsonProperty("data") TargetInfoResponseData data)
    {
        super(success, error, ts_request_mono);
        this.data = data;
    }
}
