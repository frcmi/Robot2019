package frc.robot.lib.trajectory;

import frc.robot.lib.util.SnailMath;

// Do cubic spline interpolation between points.
public class VisionSpline {

    static final int numSamplesForIntegration = 100000;
    public String name = null;
    double a; // ax^5
    double b; // + bx^4
    double c; // + cx^3
    double d; // + dx^2
    double e; // + ex
    // f is always 0 for the spline formulation we support.
    double[] endpoint;
    double arcLength;


    private static boolean almostEqual(double x, double y) {
        return Math.abs(x - y) < 1E-6;
    }

    //Makes a spline from the x axis to a (x,y) with angle theta
    public VisionSpline(double x, double y, double theta) {
        System.out.println("Reticulating splines...");

        endpoint = new double[]{x, y};

        // We cannot handle vertical slopes in our rotated, translated basis.
        // This would mean the user wants to end up 90 degrees off of the straight
        // line between p0 and p1.
        // We also cannot handle the case that the end angle is facing towards the
        // start angle (total turn > 90 degrees).
        if (almostEqual(Math.abs(theta), Math.PI / 2) || Math.abs(theta) >= Math.PI / 2) {
            throw new IllegalArgumentException("Final theta must be facing away from the robot");
        }
        
        // Turn angles into derivatives (slopes)
        double dydx = Math.tan(theta);

        a = (6 * y - 3 * x * dydx) / (x*x*x*x*x);
        b = (7 * x * dydx - 15 * y) / (x*x*x*x);
        c = (10 * y - 4 * dydx) / (x*x*x);
        d = 0;
        e = 0;

        //Approximates the arc length using trapezoidal sum
        //Thanks Schjelly
        double arcLength = 0;
        double lastds = derivativeAt(0.0);
        double currentds;
        double dx = endpoint[0]/numSamplesForIntegration;
        for (int i = 1; i <= numSamplesForIntegration; ++i) {
            double currX = i*dx;
            currentds = Math.sqrt(1 + derivativeAt(currX)*derivativeAt(currX))*dx;
            arcLength += (currentds+lastds)/2;
            lastds = currentds;
        }
    }

    //Finds the derivative at a given x value
    private double derivativeAt(double x) {
        return 5.0*a*x*x*x*x + 4.0*b*x*x*x + 3.0*c*x*x;
    }

    //Finds the second derivative at a given x calue
    private double secondDerivativeAt(double x) {
        return 20.0*a*x*x*x + 12.0*b*x*x + 6.0*c*x;
    }

    public double angleAt(double x) {
        double angle = SnailMath.boundAngle0to2PiRadians(Math.atan(derivativeAt(x)));
        return angle;
    }

    public double angleChangeAt(double x) {
        return SnailMath.boundAngleNegPiToPiRadians(Math.atan(secondDerivativeAt(x)));
    }

    public String toString() {
        return "a=" + a + "; b=" + b + "; c=" + c + "; d=" + d + "; e=" + e;
    }
}