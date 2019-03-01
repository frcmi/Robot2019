package frc.robot.lib.util;

import java.util.*;

/**
 * This class holds a bunch of static methods and variables needed for
 * mathematics
 */
public class SnailMath {
    // constants

    public static final double inchesToMeters = 39.3701;
    static final double sq2p1 = 2.414213562373095048802e0;
    static final double sq2m1 = .414213562373095048802e0;
    static final double p4 = .161536412982230228262e2;
    static final double p3 = .26842548195503973794141e3;
    static final double p2 = .11530293515404850115428136e4;
    static final double p1 = .178040631643319697105464587e4;
    static final double p0 = .89678597403663861959987488e3;
    static final double q4 = .5895697050844462222791e2;
    static final double q3 = .536265374031215315104235e3;
    static final double q2 = .16667838148816337184521798e4;
    static final double q1 = .207933497444540981287275926e4;
    static final double q0 = .89678597403663861962481162e3;
    static final double PIO2 = 1.5707963267948966135E0;
    static final double nan = (0.0 / 0.0);
    // reduce

    private static double mxatan(double arg) {
        double argsq, value;

        argsq = arg * arg;
        value = ((((p4 * argsq + p3) * argsq + p2) * argsq + p1) * argsq + p0);
        value = value / (((((argsq + q4) * argsq + q3) * argsq + q2) * argsq + q1) * argsq + q0);
        return value * arg;
    }

    // reduce
    private static double msatan(double arg) {
        if (arg < sq2m1) {
            return mxatan(arg);
        }
        if (arg > sq2p1) {
            return PIO2 - mxatan(1 / arg);
        }
        return PIO2 / 2 + mxatan((arg - 1) / (arg + 1));
    }

    // implementation of atan
    public static double atan(double arg) {
        if (arg > 0) {
            return msatan(arg);
        }
        return -msatan(-arg);
    }

    // implementation of atan2
    public static double atan2(double arg1, double arg2) {
        if (arg1 + arg2 == arg1) {
            if (arg1 >= 0) {
                return PIO2;
            }
            return -PIO2;
        }
        arg1 = atan(arg1 / arg2);
        if (arg2 < 0) {
            if (arg1 <= 0) {
                return arg1 + Math.PI;
            }
            return arg1 - Math.PI;
        }
        return arg1;

    }

    // implementation of asin
    public static double asin(double arg) {
        double temp;
        int sign;

        sign = 0;
        if (arg < 0) {
            arg = -arg;
            sign++;
        }
        if (arg > 1) {
            return nan;
        }
        temp = Math.sqrt(1 - arg * arg);
        if (arg > 0.7) {
            temp = PIO2 - atan(temp / arg);
        } else {
            temp = atan(arg / temp);
        }
        if (sign > 0) {
            temp = -temp;
        }
        return temp;
    }

    // implementation of acos
    public static double acos(double arg) {
        if (arg > 1 || arg < -1) {
            return nan;
        }
        return PIO2 - asin(arg);
    }

    /**
     * Get the difference in angle between two angles.
     *
     * @param from The first angle
     * @param to   The second angle
     * @return The change in angle from the first argument necessary to line up with
     *         the second. Always between -Pi and Pi
     */
    public static double getDifferenceInAngleRadians(double from, double to) {
        return boundAngleNegPiToPiRadians(to - from);
    }

    /**
     * Get the difference in angle between two angles.
     *
     * @param from The first angle
     * @param to   The second angle
     * @return The change in angle from the first argument necessary to line up with
     *         the second. Always between -180 and 180
     */
    public static double getDifferenceInAngleDegrees(double from, double to) {
        return boundAngleNeg180to180Degrees(to - from);
    }

    public static double boundAngle0to360Degrees(double angle) {
        // Naive algorithm
        while (angle >= 360.0) {
            angle -= 360.0;
        }
        while (angle < 0.0) {
            angle += 360.0;
        }
        return angle;
    }

    public static double boundAngleNeg180to180Degrees(double angle) {
        // Naive algorithm
        while (angle >= 180.0) {
            angle -= 360.0;
        }
        while (angle < -180.0) {
            angle += 360.0;
        }
        return angle;
    }

    public static double boundAngle0to2PiRadians(double angle) {
        // Naive algorithm
        while (angle >= 2.0 * Math.PI) {
            angle -= 2.0 * Math.PI;
        }
        while (angle < 0.0) {
            angle += 2.0 * Math.PI;
        }
        return angle;
    }

    public static double boundAngleNegPiToPiRadians(double angle) {
        // Naive algorithm
        while (angle >= Math.PI) {
            angle -= 2.0 * Math.PI;
        }
        while (angle < -Math.PI) {
            angle += 2.0 * Math.PI;
        }
        return angle;
    }


    
    public double[] path;
    public static double sampleWidth = 12.0; // Width between samples taken on curve
    public static double fieldLengthConst = 648.0; //Field length in inches
    public static double a;
    public static double b;
    public static double c;
    public static double x;
    public static double y;
    public double tempX = fieldLengthConst + 1.0;
    public double tempY = fieldLengthConst + 1.0;
    public ArrayList<double[]> ranges = new ArrayList<double[]>();
    public ArrayList<Double> mins = new ArrayList<Double>();
    public double[] minYs = new double[mins.size()];
    public double finalX;


    public double closestPolyX(double pointX, double pointY, double[] coefficients) {

        a = coefficients[0];
        b = coefficients[1];
        c = coefficients[2];
        x = pointX;
        y = pointY;
        
        for (double i = 0.0; i < fieldLengthConst; i += sampleWidth) {
            if (tempX < fieldLengthConst && tempY < fieldLengthConst) {
                if (tempY <= 0.0 && polynomial(i) >= 0.0) {
                    double[] temp = new double[2];
                    temp[0] = tempX;
                    temp[1] = i;
                    ranges.add(temp);
                }
            }
            tempX = i;
            tempY = polynomial(i);
        }

        for(double[] range : ranges) {
            mins.add(search(range[0], ((range[0] + range[1]) / 2), range[1]));
        }

        for(int k = 0; k < mins.size(); k++) {
            minYs[k] = antiderivative(mins.get(k));
        }

        Arrays.sort(minYs);
        
        for(int j = 0; j < mins.size(); j++) {
            if (antiderivative(mins.get(j)) == minYs[0]) {
                finalX = mins.get(j);
            }
        }
        return finalX;

    }
    
    // Equation of derivative of distance from point to curve
    private static double polynomial(double input) {
        return ((5.0 * input * a * a) +
                (9.0 * input * a * b) +
                (8.0 * a * c + 4.0 * b * b) * (input) +
                (7.0 * input * b * c) +
                (3.0 * input * c * c) +
                (-5.0 * y * a * input) +
                (-4.0 * y * b * input) +
                (-3.0 * y * c * input) +
                (-1.0 * input) + x);
    }

    // Equation of distance from point to curve
    private static double antiderivative(double input) {
        return Math.sqrt(
            y - (Math.pow((a * Math.pow(input, 5.0) + b * Math.pow(input, 4.0) + c * Math.pow(input, 3.0)), 2.0) + Math.pow((3 - input), 2.0)));
    }

    private double search(double low, double mid, double high) {
        if (nearZero(polynomial(mid))) {
            return mid;
        } else if (low == mid) {
            return -1.0;
        } else if (search(low, ((low + mid) / 2), mid) == -1.0) {
            return search(mid, ((high + mid) / 2), high);
        } else {
            return search(low, ((low + mid) / 2), mid);
        }
    }

    private boolean nearZero(double in) {
        return (in < 1.0 && in > -1.0);
    }
}