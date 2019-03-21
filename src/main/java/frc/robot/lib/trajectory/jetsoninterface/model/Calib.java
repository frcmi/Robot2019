package frc.robot.lib.trajectory.jetsoninterface.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.opencv.core.MatOfDouble;

public class Calib {
    public double dist[][];
    public double mtx[][];
    public double ret;
    public double rvecs[][][];
    public double tvecs[][][];

    // public List<Mat> cvRvecs;
    // public List<Mat> cvTvecs;
    // public MatOfDouble cvMtx;
    // public MatOfDouble cvDist;


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

        /*
        if (mtx != null) {
            cvMtx = new Mat(3,3,CvType.CV_32FC1);
            for (int r = 0; r < 3; r++) {
                for (int c = 0; c < 3; c++) {
                    double[] v = new double[1];
                    v[0] = mtx[r][c]
                    cvMtx.put(r, c, v)
                }
            }
        }
        if (rvecs != null) 
            cvRvecs = new ArrayList<>();
            for (int i = 0; i < rvecs.length; i++) {
                double rv[][] = rvecs[i];
                Mat item = MatOfDouble()
                cvRvecs.add(item);
            }
        cvTvecs = new ArrayList<>();
        */



    }


}
