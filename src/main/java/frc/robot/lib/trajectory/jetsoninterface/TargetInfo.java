package frc.robot.lib.trajectory.jetsoninterface;

import frc.robot.lib.trajectory.jetsoninterface.model.*;
import frc.robot.lib.trajectory.jetsoninterface.model.TargetInfoResponseData;

public class TargetInfo {
    public long nanoTime;
    public double tvec[] = new double[3];
    public double rvec[] = new double[3];
    public Calib calib;
    public double x;
    public double y;
    public double rx;

    public TargetInfo(TargetInfoResponseData data, long nanoTime) {
        this.nanoTime = nanoTime;
        for (int i = 0; i < 3; i++) {
            tvec[i] = data.tvec[i][0];
            rvec[i] = data.rvec[i][0];
        }
        calib = data.calib;
        x = data.x;
        y = data.y;
        rx = data.rx;
    }
}
