package frc.robot.lib.trajectory.jetsoninterface.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.opencv.core.Mat;

import frc.robot.lib.trajectory.jetsoninterface.OpencvHelper;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

//@JsonIgnoreProperties(ignoreUnknown = true)
public class TargetInfoResponseData {
    private static OpencvHelper cvh = OpencvHelper.getInstance();

    public double x;
    public double y;
    public double tvec[][];        /* 3x1 Column vector containing a single 3D point */
    public double ts_request_mono;
    public double ts_pre_mono;
    public double ts_pre;
    public double ts_post_mono;
    public double ts_post;
    public double t;
    public double rx;
    public double ry;
    public double rvec[][];       /* 3x1 column vector containing a single 3d point */
    public double originpt[][][];
    public double jacobian[][];
    public long frame_num_pre;
    public long frame_num_post;
    public double dst[][];
    public Calib calib;

    @JsonIgnore
    public Mat cvTvec;

    @JsonIgnore
    public Mat cvRvec;

    @JsonCreator
    public TargetInfoResponseData(
            @JsonProperty("x") double x,
            @JsonProperty("y") double y,
            @JsonProperty("tvec") double tvec[][],
            @JsonProperty("ts_request_mono") double ts_request_mono,
            @JsonProperty("ts_pre_mono") double ts_pre_mono,
            @JsonProperty("ts_pre") double ts_pre,
            @JsonProperty("ts_post_mono") double ts_post_mono,
            @JsonProperty("ts_post") double ts_post,
            @JsonProperty("t") double t,
            @JsonProperty("rx") double rx,
            @JsonProperty("ry") double ry,
            @JsonProperty("rvec") double rvec[][],
            @JsonProperty("originpt") double originpt[][][],
            @JsonProperty("jacobian") double jacobian[][],
            @JsonProperty("frame_num_pre") long frame_num_pre,
            @JsonProperty("frame_num_post") long frame_num_post,
            @JsonProperty("dst") double dst[][],
            @JsonProperty("calib") Calib calib
        )
    {
        this.x = x;
        this.y = y;
        this.tvec = tvec;
        this.ts_request_mono = ts_request_mono;
        this.ts_pre_mono = ts_pre_mono;
        this.ts_pre = ts_pre;
        this.ts_post_mono = ts_post_mono;
        this.ts_post = ts_post;
        this.t = t;
        this.rx = rx;
        this.ry = ry;
        this.rvec = rvec;
        this.originpt = originpt;
        this.jacobian = jacobian;
        this.frame_num_pre = frame_num_pre;
        this.frame_num_post = frame_num_post;
        this.dst = dst;
        this.calib = calib;
        this.cvTvec = cvh.double2DToMat64FC1(tvec);
        this.cvRvec = cvh.double2DToMat64FC1(rvec);
    }
}
