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
    public double[] endpoint;
    double arcLength;

    private static boolean almostEqual(double x, double y) {
        return Math.abs(x - y) < 1E-6;
    }

    // Makes a spline from the origin with angle 0 radians to a (x,y) with angle
    // theta

    /**
     * VisionSpline - Makes a 2D spline in the XY plane, starting at the origin with
     * angle 0 ( heading along positive X axis) and ending at (x, y) with angle
     * theta (rotated CCW from positive X axis).
     *
     * @param x     Final x coordinate
     * @param y     Final y coordinate
     * @param theta Final angle in radians, CCW from positive x axis
     */
    public VisionSpline(double x, double y, double theta) {
        System.out.println("Reticulating splines...");

        endpoint = new double[] { x, y };

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

        a = (6 * y - 3 * x * dydx) / Math.pow(x, 5);
        b = (7 * x * dydx - 15 * y) / Math.pow(x, 4);
        c = (10 * y - 4 * dydx) / Math.pow(x, 3);
        d = 0;
        e = 0;

        // Approximates the arc length with trapezoid sum estimate of integral
        // Thanks Schjelly
        arcLength = 0;
        double lastds = derivativeAt(0.0);
        double currentds;
        double dx = endpoint[0] / numSamplesForIntegration;
        for (int i = 1; i <= numSamplesForIntegration; i++) {
            double currX = i * dx;
            currentds = Math.sqrt(1 + derivativeAt(currX) * derivativeAt(currX)) * dx;
            arcLength += (currentds + lastds) / 2;
            lastds = currentds;
        }
    }

    // Finds the derivative at a given x value
    private double derivativeAt(double x) {
        return 5.0 * a * Math.pow(x, 4) + 4.0 * b * Math.pow(x, 3) + 3.0 * c * Math.pow(x, 2);
    }

    // Finds the second derivative at a given x calue
    private double secondDerivativeAt(double x) {
        return 20.0 * a * Math.pow(x, 3) + 12.0 * b * Math.pow(x, 2) + 6.0 * c * x;
    }

    public double angleAt(double x) {
        double angle = SnailMath.boundAngle0to2PiRadians(Math.atan(derivativeAt(x)));
        return angle;
    }

    // This should output the change in terms of radians per meter arc length
    // I really don't trust my math here so I am going to check it with someone asap
    public double angleChangeAt(double x) {
        double dthetadx = secondDerivativeAt(x) / (Math.pow(derivativeAt(x), 2) + 1);
        double dxds = 1.0 / (1.0 + Math.pow(derivativeAt(x), 2));
        double dthetads = dthetadx * dxds;
        return dthetads;
    }

    public String toString() {
        return String.format("a=%f; b=%f; c=%f; d=%f; e=%f", a, b, c, d, e);
    }

    public double getY(double x) {
        return a * Math.pow(x, 5) + b * Math.pow(x, 4) + c * Math.pow(x, 3);
    }

    // Gets the x value of the nearest point on the spline from a given (x,y)
    public double getSplinePoint(double x, double y) {
        // XXX: Is this supposed to always return 1.0?
        return 1.0;
    }

}