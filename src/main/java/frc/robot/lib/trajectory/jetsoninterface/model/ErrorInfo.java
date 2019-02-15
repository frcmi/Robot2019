package frc.robot.lib.trajectory.jetsoninterface.model;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class ErrorInfo {
    // public Object data;
    public String class_name;
    public String message;
    public String stack_trace[];
    public boolean success;
    public double ts_request_mono;

    @JsonCreator
    public ErrorInfo(
            @JsonProperty("class_name") String class_name,
            @JsonProperty("message") String message,
            @JsonProperty("stack_trace") String stack_trace[],
            @JsonProperty("success") boolean success,
            @JsonProperty("ts_request_mono") double ts_request_mono)
    {
        this.class_name = class_name;
        this.message = message;
        this.stack_trace = stack_trace;
        this.success = success;
        this.ts_request_mono = ts_request_mono;
    }
}
