package frc.robot.subsystems;

import frc.robot.lib.util.RobotMap;

import edu.wpi.first.wpilibj.command.Subsystem;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.CvSource;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.cameraserver.*;

public class Camera extends Subsystem {

    // Subsystems are singleton classes, so there should only be one of each class. Instead
    // of calling the constructor directly, the client should use getInstance to prevent duplication
    // of Subsystem objects.
    private static Camera instance;

    public static Camera getInstance() {
        if (instance==null) instance = new Camera();
        return instance;
    }
    
    private int screenWidth;
    private int screenHeight;
    private UsbCamera camera;
    
    private CvSink cvSink;
    private CvSource outputStream;
    
    private Mat source;
    private Mat output;

    private Camera() {
        super();
        screenWidth = RobotMap.userCamResX;
        screenHeight = RobotMap.userCamResY;
        camera = CameraServer.getInstance().startAutomaticCapture();
        camera.setResolution(screenWidth, screenHeight);
        cvSink = CameraServer.getInstance().getVideo();
        outputStream = CameraServer.getInstance().putVideo("Camera Output", screenWidth, screenHeight);
        source = new Mat();
        output = new Mat();
    }
    
    //Sets default command for the system
    @Override
    protected void initDefaultCommand() {
        setDefaultCommand(null);
    }
    
    //Called every tick by a thread when the ForwardCamera command is running
    public void forwardFrame(){
        cvSink.grabFrame(source);
        Imgproc.cvtColor(source, output, Imgproc.COLOR_BGR2BGRA);
        outputStream.putFrame(output);
        // line((screenWidth/2)-20, screenHeight/2, (screenWidth/2)+20, screenHeight/2);  //crosshair horizontal (needs openCV imports)
        // line(screenWidth/2, (screenHeight/2)-20, screenWidth/2, (screenHeight/2)+20);  //crosshair vertical
    }
}