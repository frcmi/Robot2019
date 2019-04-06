package frc.robot.subsystems;

import frc.robot.lib.util.RobotMap;
import frc.robot.lib.trajectory.jetsoninterface.model.*;
import frc.robot.commands.ForwardCamera;
import frc.robot.lib.trajectory.jetsoninterface.OpencvHelper;
import frc.robot.lib.trajectory.jetsoninterface.TargetInfo;
import frc.robot.lib.trajectory.jetsoninterface.VisionException;
import frc.robot.lib.trajectory.jetsoninterface.VisionPoller;

import edu.wpi.first.wpilibj.command.Subsystem;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.CvSource;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.cameraserver.*;
import java.util.*;
import org.opencv.core.*;
import org.opencv.calib3d.Calib3d;
import org.apache.commons.io.FileUtils;

public class Camera extends Subsystem {

    // Subsystems are singleton classes, so there should only be one of each class.
    // Instead
    // of calling the constructor directly, the client should use getInstance to
    // prevent duplication
    // of Subsystem objects.

    private static OpencvHelper cvh = OpencvHelper.getInstance();    
    private static Camera instance;

    public static Camera getInstance() {
        if (instance == null)
            instance = new Camera();
        return instance;
    }

    private int screenWidth;
    private int screenHeight;

    private int calibWidth = 780;
    private int calibHeight = 400;

    private int fps;
    private UsbCamera camera;

    private CvSink cvSink;
    private CvSource outputStream;

    private Mat source;
    private Mat output;

    private Camera() {
        super();
        fps = RobotMap.userCamFPS;
        screenWidth = RobotMap.userCamResX;
        screenHeight = RobotMap.userCamResY;
        camera = CameraServer.getInstance().startAutomaticCapture();
        camera.setResolution(screenWidth, screenHeight);
        camera.setFPS(fps);
        cvSink = CameraServer.getInstance().getVideo();
        cvSink.setEnabled(true);
        outputStream = CameraServer.getInstance().putVideo("Camera Output", screenWidth, screenHeight);
        source = new Mat();
        output = new Mat();
    }

    // Sets default command for the system
    @Override
    protected void initDefaultCommand() {
        setDefaultCommand(null);
    }
    
    // Called every tick by a thread when the ForwardCamera command is running
    public void forwardFrame() {
        long frameTime = cvSink.grabFrame(source);
        if (frameTime == 0) {
            System.out.println("cvSink.grabFrame() failed:");
            System.out.println(cvSink.getError());
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
            }
        } else {
            Imgproc.cvtColor(source, output, Imgproc.COLOR_BGR2BGRA);
            Imgproc.line(source, new Point(10, 10), new Point(30, 30), new Scalar(255, 0, 0));
            drawOnFrame(source);
        }

        //Testing code to get delta
        if(VisionPoller.getInstance() != null && VisionPoller.getInstance().getRelativePosition()!=null){
            VisionPoller.getInstance().getRelativePosition().print();
        }
    }

    Mat srcPointer;
    Mat rvec;
    Mat tvec;
    Mat mtx;
    MatOfDouble dist;

    public void drawOnFrame(Mat src) {
        srcPointer = src;

        //Makes it exit cleanly if it cannot communicate with VisionPoller
        if (VisionPoller.getInstance() == null){
            return;
        }
        TargetInfo info = VisionPoller.getInstance().getLatestTargetInfoHandleErrors();
        if (info == null){
            return;
        }

        cvh.printMat(info.cvRvec, "rvec");
        cvh.printMat(info.cvTvec, "tvec");
        cvh.printMat(info.calib.cvMtx, "mtx");
        cvh.printMat(info.calib.cvDist, "dist");
        rvec = info.cvRvec;
        tvec = info.cvTvec;
        mtx = info.calib.cvMtx;
        dist = info.calib.cvDist;
        drawAxis();
    }

    // Draw the axis for debugging
    public void drawAxis() {
        double camDist = RobotMap.camDistance;
        double reticleDist = 10.0;
        double error = camDist - reticleDist;

        Point3[] originPts = { new Point3(0, error, 0), new Point3(3, error, 0), new Point3(0, error + 3, 0),
                new Point3(0, error, 3) };
        MatOfPoint2f pixels = null;

        drawLine3D(originPts[0], originPts[1], new Scalar(255, 0, 0));
        drawLine3D(originPts[0], originPts[2], new Scalar(0, 255, 0));
        drawLine3D(originPts[0], originPts[3], new Scalar(0, 0, 255));

    }

    // Draws a line from two given 3d coordinates
    public void drawLine3D(Point3 point1, Point3 point2, Scalar color) {
        MatOfPoint2f pixels = null;
        Calib3d.projectPoints(new MatOfPoint3f(point1, point2), rvec, tvec, mtx, dist, pixels);
        Point p1 = pixels.toArray()[0];
        Point p2 = pixels.toArray()[1];
        double[] p1Array = {p1.x * screenWidth / calibWidth, p1.y * screenHeight / calibHeight};
        double[] p2Array = {p2.x * screenWidth / calibWidth, p2.y * screenHeight / calibHeight};
        p1.set(p1Array);
        p2.set(p2Array);
        
        Imgproc.line(srcPointer, p1, p2, color);
    }
}