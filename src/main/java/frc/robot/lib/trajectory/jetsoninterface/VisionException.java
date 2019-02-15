package frc.robot.lib.trajectory.jetsoninterface;

import frc.robot.lib.trajectory.jetsoninterface.model.ErrorInfo;

public class VisionException extends RuntimeException {
    public ErrorInfo errorInfo;

    public VisionException(ErrorInfo errorInfo) {
        super(errorInfo.message);
        this.errorInfo = errorInfo;
    }
}
