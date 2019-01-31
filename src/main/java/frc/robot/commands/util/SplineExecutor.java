package frc.robot.commands.util;

public class SplineExecutor {

    public SplinePoint current;
    public SplinePath path;

    public SplineExecutor(SplinePath path, SplinePoint current) {
        this.current = current;
        this.path = path;
    }

    public void followSpline() {
        SplinePoint temp = path.getNextPoint();
        if (current.x < path.getNextPoint().x) {
            if (current.y < temp.y) {
                // rotate(current.angle - temp.angle);
            }
        }
    }
}