package frc.robot.lib.trajectory.jetsoninterface.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Calib {
    public double dist[][];
    public double mtx[][];
    public double ret;
    public double rvecs[][][];
    public double tvecs[][][];

    @JsonCreator
    public Calib(
            @JsonProperty("dist") double dist[][],
            @JsonProperty("mtx") double mtx[][],
            @JsonProperty("ret") double ret,
            @JsonProperty("rvecs") double rvecs[][][],
            @JsonProperty("tvecs") double tvecs[][][])
    {
        this.dist=dist;
        this.mtx=mtx;
        this.ret=ret;
        this.rvecs=rvecs;
        this.tvecs=tvecs;
    }
}
