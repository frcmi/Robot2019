package org.mercerislandschools.mihs.frc.vision.client;

import org.mercerislandschools.mihs.frc.vision.client.model.TargetInfoResponseData;

public class TargetInfo {
    public long nanoTime;
    public double tvec[] = new double[3];
    public double rvec[] = new double[3];

    public TargetInfo(TargetInfoResponseData data, long nanoTime)
    {
        this.nanoTime = nanoTime;
        for (int i = 0; i < 3; i++) {
            tvec[i] = data.tvec[i][0];
            rvec[i] = data.rvec[i][0];
        }
    }
}