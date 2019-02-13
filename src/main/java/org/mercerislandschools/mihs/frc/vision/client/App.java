package org.mercerislandschools.mihs.frc.vision.client;

import java.util.concurrent.TimeUnit;

public class App {
    public static void main(String[] args) {
        String url = "http://tegra-ubuntu.local:5800";
        if (args.length > 0) {
            url = args[0];
        }
        
        long nano1sec = 1000000000L;

        VisionPoller vp = new VisionPoller(url);
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
