package org.mercerislandschools.mihs.frc.vision.client;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import javax.ws.rs.core.UriBuilder;

import org.mercerislandschools.mihs.frc.vision.client.model.*;

public class App {
    public static void main(String[] args) {
        String url = "http://tegra-ubuntu.local:5800";
        if (args.length > 0) {
            url = args[0];
        }

        VisionClient vc = new VisionClient(url);

        long localTime = vc.getLocalNanosecs();
        TargetInfo info = vc.getTargetInfo();

        System.out.printf("Local Time: %d\n", localTime);
        System.out.printf("Cam Time: %d\n", info.nanoTime);
        System.out.printf("Rvec: [%f, %f, %f]\n", info.rvec[0], info.rvec[1], info.rvec[2]);
        System.out.printf("Tvec: [%f, %f, %f]\n", info.tvec[0], info.tvec[1], info.tvec[2]);
    }
}
