package frc.robot.lib.trajectory.jetsoninterface;

public final class TargetResult {
    public boolean success;
    public String failureReason;
    public TargetInfo info;

    TargetResult(TargetInfo info) {
        success = true;
        failureReason = null;
        this.info = info;
    }

    TargetResult(String failureReason) {
        success = false;
        this.failureReason = failureReason;
        info = null;
    }
}
