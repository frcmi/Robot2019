package frc.robot.commands.util;

//Data structure for spline data point
public class SplinePoint {
    public float x;
    public float y;
    public float rotation;
    public float curvature;

    // Spline Constructor--takes input for x and y coordinates,
    // rotation, curvature, and spline name
    public SplinePoint(float x, float y, float rotation, float curvature) {
        x = this.x;
        y = this.y;
        rotation = this.rotation;
        curvature = this.curvature;
    }
}