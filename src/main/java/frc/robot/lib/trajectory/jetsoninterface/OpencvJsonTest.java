package frc.robot.lib.trajectory.jetsoninterface;

import frc.robot.lib.trajectory.jetsoninterface.model.Calib;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.management.RuntimeErrorException;

import static java.lang.Math.PI;
//import java.lang.Math.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import org.opencv.calib3d.Calib3d;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDouble;
import org.opencv.core.Point3;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.MatOfPoint3f;
import org.opencv.core.Size;
import org.opencv.core.TermCriteria;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.Scalar;


public class OpencvJsonTest {
    public class FrameInfo {
        public int imageNum;
        public File imageFile;
        public Mat originalFrame;           /* saved original frame */
        public Mat bwFrame;                 /* saved black and white frame */
        public boolean found;               /* true if chessboard corners were located */
        public MatOfPoint2f imageCorners;   /* 32FC2 column vector of 2D screen coordinates of
                                               chess square corners -- len=boardWidth*boardHeight */
        public Mat drawnFrame;              /* Frame with calibration markers drawn */
        public Mat undistorted;             /* Drawn frame that has been undistorted using calibration values */

        public FrameInfo(int imageNum, File imageFile)
        {
            this.imageNum = imageNum;
            this.imageFile = imageFile;
            this.found = false;
        }

        public String toString()
        {
            String result = "Frame " + imageNum + " (" + imageFile.toString() + ")";
            if (originalFrame != null) {
                result = result + " (" + originalFrame.rows() + "x" + originalFrame.cols() + " " + cvh.matToTypeString(originalFrame) + ")";
                if (found) {
                    result = result + " FOUND";
                }
            }
            return result;
        }

        public void show()
        {
            Mat img = undistorted;
            if (img == null) {
                img = drawnFrame;
                if (img == null) {
                    img = originalFrame;
                }
            }
            if (img != null) {
                cvh.showImage(img);
            }
    }

        public void readOriginalFrame()
        {
            originalFrame = Imgcodecs.imread(imageFile.toString());
        }

        public void findAndDrawPoints()
        {
            bwFrame = new Mat();
            Imgproc.cvtColor(originalFrame, bwFrame, Imgproc.COLOR_BGR2GRAY);
            Size boardSize = new Size(boardWidth, boardHeight);
            imageCorners = new MatOfPoint2f();
            found = Calib3d.findChessboardCorners(bwFrame, boardSize, imageCorners,
                    Calib3d.CALIB_CB_ADAPTIVE_THRESH + Calib3d.CALIB_CB_NORMALIZE_IMAGE + Calib3d.CALIB_CB_FAST_CHECK);
            if (found) {
                System.out.println("Found chessboard corners!");
                // cvh.printMat(imageCorners, "Image corners");
                drawnFrame = new Mat();
                originalFrame.copyTo(drawnFrame);
                TermCriteria term = new TermCriteria(TermCriteria.EPS | TermCriteria.MAX_ITER, 30, 0.1);
                Imgproc.cornerSubPix(bwFrame, imageCorners, new Size(11, 11), new Size(-1, -1), term);
                Calib3d.drawChessboardCorners(drawnFrame, boardSize, imageCorners, found);
            } else {
                System.out.println("Could NOT find chessboard corners!");
            }
        }

        public void undistort(Mat intrinsic, Mat distCoeffs)
        {
            Mat img = drawnFrame;
            if (img == null) {
                img = originalFrame;
            }
            if (img != null) {
                undistorted = new Mat();
                Mat newIntrinsic = intrinsic;
                Imgproc.undistort(img, undistorted, intrinsic, distCoeffs, newIntrinsic);
            }
        }
    }


    /* Must be first to init opencv dll */
    public static  OpencvHelper cvh = OpencvHelper.getInstance();

    public static String calibRelDirPath = "jetson/SnailVision/calibrate";
    public static int boardWidth = 9; /* Number of detected chess board intersections horizontally */
    public static int boardHeight = 7; /* Number of detected chess board intersections vertically */
    public static MatOfPoint3f obj;   /* constructed 3D float coordinates of chessboard intersections in
                                         chessboard coordinated system, in inches.  Never changes */

    static {
        /* construct the (fixed) 3d coordinate list of the chess board vertices, in inches */
        obj = new MatOfPoint3f();
        for (int r = 0; r < boardHeight; r++) {
            for (int c = 0; c < boardWidth; c++) {
                // x = row inches
                // y = col inches
                // z = 0
                obj.push_back(new MatOfPoint3f(new Point3((float)r, (float)c, 0.0f)));
            }
        }
        cvh.printMat(obj, "Chessboard3DPoints");
    }


    public OpencvJsonTest()
    {
    }

    public void run()
    {
        Path wd = Paths.get(".").toAbsolutePath().normalize();
        Path calibDir = wd.resolve(calibRelDirPath);
        Path imagesDir = calibDir.resolve("images");
        Path calibFile = calibDir.resolve("fisheyecalibration.txt");

        ObjectMapper mapper = new ObjectMapper();
        Calib calib;
        try {
            calib = mapper.readValue(new File(calibFile.toString()), Calib.class);
        } catch (IOException e) {
            throw new RuntimeException("Unable to open calib file " + calibFile.toString(), e);
        }

        File[] imageFiles = new File(imagesDir.toString()).listFiles(new FilenameFilter() {
            public boolean accept(File dir, String filename) {
                return filename.endsWith(".jpg");
            }
        });

        int numSuccesses = 0;
        List<FrameInfo> frames = new ArrayList<>();
        List<Mat> imagePoints = new ArrayList<>();
        List<Mat> objectPoints = new ArrayList<>();
        Size imgSize = null;
        for (int i = 0; i < imageFiles.length; i++) {
            FrameInfo frame = new FrameInfo(i, imageFiles[i]);
            frames.add(frame);
            try {
               frame.readOriginalFrame();
            } catch(Exception e) {
                System.out.println(frame.toString());
                throw e;
            }
            System.out.println(frame.toString());
            frame.findAndDrawPoints();
            if (frame.found) {
                numSuccesses++;
                imagePoints.add(frame.imageCorners);
                objectPoints.add(obj);
                Size frameSize = frame.bwFrame.size();
                if (imgSize == null) {
                    imgSize = frameSize;
                } else {
                    if (frameSize.height != imgSize.height || frameSize.width != imgSize.width) {
                        throw new RuntimeException("All calibration images must have the same dimesions");
                    }
                }
            }
            // frame.show();
        }
        System.out.println("Number of successes=" + numSuccesses);
        if (numSuccesses > 5) {
            List<Mat> rvecs = new ArrayList<>();  /* List of 3x1 64FC1 column vectors, len = # images*/
            List<Mat> tvecs = new ArrayList<>();  /* List of 3x1 64FC1 column vectors, len = # images*/

            /* Camera intrinsic properties (called mtx in our JSON schema) */
            Mat intrinsic = new Mat(3, 3, CvType.CV_32FC1);  /* seems to be converted to 64FC1 by calibration */
            /* set intrinsic to identity matrix to start with */
            intrinsic.setTo(new Scalar(0));
            intrinsic.put(0, 0, 1);
            intrinsic.put(1, 1, 1);
            cvh.printMat(intrinsic, "preCalibrateIntrinsic");
            MatOfDouble distCoeffs = new MatOfDouble();  /* 1x5 row vector 64FC1 */
            double reprojectionError = Calib3d.calibrateCamera(
                objectPoints, imagePoints, imgSize, intrinsic, distCoeffs, rvecs, tvecs);
            System.out.println("Calibration successful! reprojection error=" + reprojectionError);
            cvh.printMat(intrinsic, "intrinsic");
            cvh.printMat(distCoeffs, "dist");
            cvh.printMatList(rvecs, "rvecs");
            cvh.printMatList(tvecs, "tvecs");

            for (int i = 0; i < frames.size(); i++) {
                FrameInfo frame = frames.get(i);
                frame.undistort(intrinsic, distCoeffs);
                frame.show();
            }

            Calib newCalib = new Calib(distCoeffs, intrinsic, reprojectionError, rvecs, tvecs);
            ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());
            try {
                writer.writeValue(System.out, newCalib);
            } catch (IOException e) {
                throw new RuntimeException("Unable to serialize newCalib", e);
            }

        }
        System.out.println("Finished!");
    }

    public static void main(String[] args) {
        new OpencvJsonTest().run();
    }

}