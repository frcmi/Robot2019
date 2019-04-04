package frc.robot.lib.util;
// Stores x and y which represent the distance forward to the board and the
// sideways distance respectively, and the
// sideways angle of the board in radians (counterclockwise)
public class Delta {
    public double x;
    public double y;
    public double theta;
    public long timeStamp;

    public Delta(double x, double y, double theta, long timeStamp) {
        this.x = x;
        this.y = y;
        this.theta = theta;
        this.timeStamp = timeStamp;
    }

    public void print() {
        System.out.println("Delta object:");
        System.out.println("    x=" + x);
        System.out.println("    y=" + y);
        System.out.println("    theta=" + theta);
        System.out.println("    timeStamp=" + timeStamp);
    }
}