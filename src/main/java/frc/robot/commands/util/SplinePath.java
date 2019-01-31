package frc.robot.commands.util;

import java.util.*;

public class SplinePath {
    
    public ArrayList<SplinePoint> path;
    public String name;

    public SplinePath(String name) {
        path = new ArrayList<SplinePoint>();
        this.name = name;
    }

    public void addPoint(SplinePoint point) {
        path.add(point);
    }

    public SplinePoint getNextPoint() {
        return path.get(path.size());
    }
}