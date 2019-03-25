package frc.robot.lib.trajectory.jetsoninterface;

import org.opencv.calib3d.Calib3d;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.WindowConstants;
import javax.swing.WindowConstants.*;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfDouble;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class OpencvHelper {
    public static Path opencvDir;
    public static Imgcodecs imageCodecs = new Imgcodecs();

    static {
        String os = System.getProperty("os.name").toLowerCase(Locale.ENGLISH);
        if (os.contains("windows")) {
            /*
            Path home = Paths.get(".");
            opencvDir = home.resolve("opencv");
            String model = System.getProperty("sun.arch.data.model");
            Path libraryDir;
            if(model.equals("64")) {
                libraryDir = opencvDir.resolve("build/java/x64");
            } else {
                libraryDir = opencvDir.resolve("build/java/x86");
            }
            Path libraryPath = libraryDir.resolve(Core.NATIVE_LIBRARY_NAME);
            
            if (Files.exists(libraryPath)) {
                System.load(libraryPath.toString());
            } else {
                System.out.println("No opencv dll found at " + libraryPath.toString() + "; not loading opencv...");
            }
            */
            try {
                System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
            } catch (Exception e) {
                System.out.println("Unable to load opencv dll " + Core.NATIVE_LIBRARY_NAME + "; not loading opencv...");
            }
        }
    }

    private static OpencvHelper singleton;
    public static OpencvHelper getInstance()
    {
        if (singleton == null) {
            singleton = new OpencvHelper();
        }
        return singleton;
    }

    public String matTypeToString(int depth, int channels) {
        String r;
      
        switch (depth) {
            case CvType.CV_8U:  r = "8U"; break;
            case CvType.CV_8S:  r = "8S"; break;
            case CvType.CV_16U: r = "16U"; break;
            case CvType.CV_16S: r = "16S"; break;
            case CvType.CV_32S: r = "32S"; break;
            case CvType.CV_32F: r = "32F"; break;
            case CvType.CV_64F: r = "64F"; break;
            default:          r = "User"; break;
        }

        if (channels != 1) {
            r += "C" + channels;
        }
      
        return r;
    }

    public String matToTypeString(Mat mat)
    {
        return matTypeToString(mat.depth(), mat.channels());
    }

    public void printMat(Mat mat, String label)
    {
        if (label == null) {
            label = "Mat";
        }
        System.out.print(label + " (nr=" + mat.rows() + ", nc=" + mat.cols() + ", type=" + matToTypeString(mat) + "): ");
        System.out.println(mat.dump());
    }

    public void printMatList(List<Mat> matList, String label)
    {
        System.out.println(label + " List<Mat> length=" + matList.size() + " {");
        for (int i = 0; i < matList.size(); i++) {
            Mat mat = matList.get(i);
            System.out.print("     [" + i + "] Mat(nr=" + mat.rows() + ", nc=" + mat.cols() + ", type=" + matToTypeString(mat) + "): ");
            System.out.print("        " + mat.dump());
            if (i+1 < matList.size()) {
                System.out.print(",");
            }
            System.out.println();
        }
        System.out.println("}");
    }

    public void showImage(Mat img) {
        Imgproc.resize(img, img, new Size(640, 480));
        MatOfByte matOfByte = new MatOfByte();
        Imgcodecs.imencode(".jpg", img, matOfByte);
        byte[] byteArray = matOfByte.toArray();
        BufferedImage bufImage = null;
        try {
            InputStream in = new ByteArrayInputStream(byteArray);
            bufImage = ImageIO.read(in);
            JFrame frame = new JFrame();
            frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            frame.getContentPane().add(new JLabel(new ImageIcon(bufImage)));
            frame.pack();
            frame.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }        
    /**
     * Convert a simple 1D array to a column vector matrix.
     * Resulting numer of rows = v.length
     * Resulting number of colums = 1
     */
    public Mat double1DTo32FColumn(double[] v)
    {
        Mat result = null;
        if (v != null) {
            result = new Mat(v.length, 1, CvType.CV_32FC1);
        }
        return result;
    }

    public Mat double2DToMat(double[][] v, int cvType)
    {
        Mat result = null;
        if (v != null) {
            int nr = v.length;
            int nc = v[0].length;
            result = new Mat(nr, nc, cvType);
            for (int r = 0; r < nr; r++) {
                result.put(r, 0, v[r]);
            }
        }
        return result;
    }

    public Mat double2DToMat64FC1(double[][] v)
    {
        return double2DToMat(v, CvType.CV_64FC1);
    }

    public Mat double2DToMat32FC1(double[][] v)
    {
        return double2DToMat(v, CvType.CV_32FC1);
    }

    public MatOfDouble double2DToMatOfDouble(double[][] v)
    {
        MatOfDouble result = null;
        if (v != null) {
            result = new MatOfDouble(double2DToMat64FC1(v));
        }
        return result;
    }

    public double[][] matToDouble2D(Mat v)
    {
        double[][] result = null;
        if (v != null) {
            int nr = v.rows();
            int nc = v.cols();
            result = new double[nr][nc];
            for (int r = 0; r < nr; r++) {
                v.get(r, 0, result[r]);
            }
        }
        return result;
    }

    public List<Mat> arrayOfDouble2DToListOfMat(double[][][] v, int cvType)
    {
        ArrayList<Mat> result = null;
        if (v != null) {
            result = new ArrayList<Mat>();
            for (int i = 0; i < v.length; i++) {
                result.add(double2DToMat(v[i], cvType));
            }
        }
        return result;
    }

    public List<Mat> arrayOfDouble2DToListOfMat32FC1(double[][][] v)
    {
        return arrayOfDouble2DToListOfMat(v, CvType.CV_32FC1);
    }

    public List<Mat> arrayOfDouble2DToListOfMat64FC1(double[][][] v)
    {
        return arrayOfDouble2DToListOfMat(v, CvType.CV_64FC1);
    }

    public double[][][] listOfMatToArrayOfDouble2D(List<Mat> v)
    {
        double[][][] result = null;
        if (v != null) {
            result = new double[v.size()][][];
            for (int i = 0; i < v.size(); i++) {
                result[i] = matToDouble2D(v.get(i));
            }
        }
        return result;
    }

    public static void main(String[] arg)
    {
        OpencvHelper cvh = getInstance();
        Mat rv = new Mat(1, 5, CvType.CV_32FC1);
        int nr = 1;
        int nc = 5;
        int nx = 1;
        for (int r = 0; r < nr; r++) {
            for (int c = 0; c < nc; c++) {
                float[] data = { (float)(c) + (float)(r) * nc };
                rv.put(r, c, data);
            }
        }
        Mat cv = new Mat(5, 1, CvType.CV_32FC1);
        nr = 5;
        nc = 1;
        for (int r = 0; r < nr; r++) {
            for (int c = 0; c < nc; c++) {
                float[] data = { (float)(c) + (float)(r) * nc };
                cv.put(r, c, data);
            }
        }
        Mat m34 = new Mat(3, 4, CvType.CV_32FC1);
        nr = 3;
        nc = 4;
        for (int r = 0; r < nr; r++) {
            for (int c = 0; c < nc; c++) {
                float[] data = { (float)(c) + (float)(r) * nc };
                m34.put(r, c, data);
            }
        }
        nr = 3;
        nc = 4;
        nx = 2;
        Mat rvx2 = new Mat(nr, nc, CvType.CV_32FC2);
        for (int r = 0; r < nr; r++) {
            for (int c = 0; c < nc; c++) {
                float[] data = new float[nx];
                for (int x = 0; x < nx; x++) {
                    data[x] = (float)x + (float)c * nx + (float)r * nc * nx; 
                }
                rvx2.put(r, c, data);
            }
        }

        float[] rvx2Elem= new float[2];
        int nret = rvx2.get(2, 2, rvx2Elem);


        cvh.printMat(rv, "rv");
        cvh.printMat(cv, "cv");
        cvh.printMat(m34, "m34");
        cvh.printMat(rvx2, "rvx2");

        System.out.println("Finished!");

    }
}