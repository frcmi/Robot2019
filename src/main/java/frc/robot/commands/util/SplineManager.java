package frc.robot.commands.util;

import java.util.*;


//A manager for splines made using the web-based spline designer
//This class should ONLY handle manually created splines, fetching
//them from the TX2

//TODO make this interact with the server @Carver
public class SplineManager {

    public static Map<String, SplinePath> splines;

    static {
        splines = new HashMap<String, SplinePath>();
    }

    //Gets most recent spline
    public static SplinePath get(String name) {
        return splines.get(name);
    }

    //Fetches splines from TX2, updates splines map
    public static void update(SplinePath temp) {
        splines.put(temp.name, temp);
    }
} 