package org.mercerislandschools.mihs.frc.vision.client;

import frc.robot.lib.trajectory.WaypointSequence;

import java.util.concurrent.TimeUnit;

import static java.lang.Math.PI;
//import java.lang.Math.*;

public class App {
    public static void main(String[] args) {
        String url = "http://tegra-ubuntu.local:5800";
        if (args.length > 0) {
            url = args[0];
        }
        
        long nano1sec = 1000000000L;

        VisionPoller vp = new VisionPoller(url);

        try {
            TimeUnit.MILLISECONDS.sleep(2000);
        } catch(InterruptedException e) {
        }

        VisionClient vc = vp.getClient();


        WaypointSequence ws1 = new WaypointSequence();
        ws1.addWaypoint(5.0, 6.0, PI/4.0);
        ws1.addWaypoint(10.0, 8.0, 0.0);
        ws1.addWaypoint(15.0, 6.0, -PI/4.0);
        ws1.addWaypoint(20.0, 0.0, 0.0);

        System.out.println("setting testWaypointSequence");

        vc.setWaypointSequenceProperty("testWaypointSequence", ws1);

        System.out.println("getting testWaypointSequence");
        WaypointSequence ws2 = vc.getWaypointSequenceProperty("testWaypointSequence");

        System.out.println("setting testWaypointSequence2");
        vc.setWaypointSequenceProperty("testWaypointSequence2", ws2);


        long startTime = System.nanoTime();
        long endTime = startTime + nano1sec * 10;

        System.out.printf("starttime=%d, endtime=%d\n", startTime, endTime);

        while (System.nanoTime() < endTime) {
            TargetResult tr = vp.getLatestTargetInfo();
            if (tr.success) {
                TargetInfo info = tr.info;
                long localTime = System.nanoTime();
                System.out.printf("POLL JETSON: Success!\n  Local Time: %d\n", localTime);
                System.out.printf("  Cam Time: %d\n", info.nanoTime);
                System.out.printf("  Rvec: [%f, %f, %f]\n", info.rvec[0], info.rvec[1], info.rvec[2]);
                System.out.printf("  Tvec: [%f, %f, %f]\n", info.tvec[0], info.tvec[1], info.tvec[2]);
            } else {
                System.out.printf("POLL JETSON: Failure: %s\n", tr.failureReason);
            }
            try {
                TimeUnit.MILLISECONDS.sleep(50);
            } catch(InterruptedException e) {
            }

        }

        System.out.printf("Closing Vision poller at %d\n", System.nanoTime() );

        vp.close();

        System.out.println("Finished!");
    }
}
