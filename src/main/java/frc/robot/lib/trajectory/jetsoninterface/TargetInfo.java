package frc.robot.lib.trajectory.jetsoninterface;

import org.opencv.core.Mat;

import frc.robot.lib.trajectory.jetsoninterface.model.*;
import frc.robot.lib.trajectory.jetsoninterface.model.TargetInfoResponseData;

public class TargetInfo {
    private static OpencvHelper cvh = OpencvHelper.getInstance();

    public long nanoTime;                   /* Nanoseconds since base time on jetson */
    public Mat cvTvec;
    public Mat cvRvec;
    public Calib calib;
    public double x;
    public double y;
    public double rx;

    public TargetInfo(TargetInfoResponseData data, long nanoTime) {
        this.nanoTime = nanoTime;
        cvTvec = data.cvTvec;
        cvRvec = data.cvRvec;
        calib = data.calib;
        x = data.x;
        y = data.y;
        rx = data.rx;
    }
}
