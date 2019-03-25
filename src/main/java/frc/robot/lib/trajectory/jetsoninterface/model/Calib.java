package frc.robot.lib.trajectory.jetsoninterface.model;

import java.util.List;

import frc.robot.lib.trajectory.jetsoninterface.OpencvHelper;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.opencv.core.MatOfDouble;
import org.opencv.core.Mat;

public class Calib {

    private static OpencvHelper cvh = OpencvHelper.getInstance();

    public double dist[][];       /* distortion coefficients as 1x5 double array */
    public double mtx[][];       /* intrinsic camera properties as 3x3 double array */
    public double ret;               /* reprojection error */
    public double rvecs[][][];   /* rvecs as n x 3 x 1 double array, where n is number of calib images */
    public double tvecs[][][];   /* tvecs as n x 3 x 1 double array, where n is number of calib images */

    @JsonIgnore
    public List<Mat> cvRvecs;          /* rvecs as List of 3x1 64FC1 column vectors, size() == number of calib images */

    @JsonIgnore
    public List<Mat> cvTvecs;          /* tvecs as List of 3x1 64FC1 column vectors, size() == number of calib images */

    @JsonIgnore
    public Mat cvMtx;                  /* intrinsic camera properties as 3x3 64FC1 matrix */

    @JsonIgnore
    public MatOfDouble cvDist;                 /* Distortion coeeficients as 1x5 row vector of 64FC1 */

    @JsonCreator
    public Calib(
            @JsonProperty("dist") double dist[][],
            @JsonProperty("mtx") double mtx[][],
            @JsonProperty("ret") double ret,
            @JsonProperty("rvecs") double rvecs[][][],
            @JsonProperty("tvecs") double tvecs[][][])
    {
        this.ret=ret;
        setDist(dist);
        setMtx(mtx);
        setRvecs(rvecs);
        setTvecs(tvecs);
    }

    public Calib()
    {
    }

    public Calib(MatOfDouble cvDist, Mat cvMtx, double ret, List<Mat> cvRvecs, List<Mat> cvTvecs)
    {
        this.ret=ret;
        setCvDist(cvDist);
        setCvMtx(cvMtx);
        setCvRvecs(cvRvecs);
        setCvTvecs(cvTvecs);
    }

    public void setDist(double[][] dist)
    {
        this.dist = dist;
        this.cvDist = cvh.double2DToMatOfDouble(dist);
    }

    public void setMtx(double[][] mtx)
    {
        this.mtx = mtx;
        this.cvMtx = cvh.double2DToMat64FC1(mtx);
    }

    public void setRvecs(double[][][] rvecs)
    {
        this.rvecs = rvecs;
        this.cvRvecs = cvh.arrayOfDouble2DToListOfMat64FC1(rvecs);
    }

    public void setTvecs(double[][][] tvecs)
    {
        this.tvecs = tvecs;
        this.cvTvecs = cvh.arrayOfDouble2DToListOfMat64FC1(tvecs);
    }

    public void setCvDist(MatOfDouble cvDist)
    {
        this.cvDist = cvDist;
        dist = cvh.matToDouble2D(cvDist);
    }

    public void setCvMtx(Mat cvMtx)
    {
        this.cvMtx = cvMtx;
        mtx = cvh.matToDouble2D(cvMtx);
    }

    public void setCvRvecs(List<Mat> cvRvecs)
    {
        this.cvRvecs = cvRvecs;
        rvecs = cvh.listOfMatToArrayOfDouble2D(cvRvecs);
    }

    public void setCvTvecs(List<Mat> cvTvecs)
    {
        this.cvTvecs = cvTvecs;
        tvecs = cvh.listOfMatToArrayOfDouble2D(cvTvecs);
    }

}
