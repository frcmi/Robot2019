package org.mercerislandschools.mihs.frc.vision.client;

import java.util.concurrent.TimeUnit;

public class VisionPoller extends Thread {
    public final String defaultServerUrl = "http://integra-ubuntu.local:5800";

    private String serverUrl;
    private VisionClient client;
    private TargetInfo latestTargetInfo = null;
    private String latestFailureReason = "Not yet time-synced";
    private boolean shutdownNow = false;
    private long pollCount = 0;

    public VisionPoller(String serverUrl)
    {
        if (serverUrl == null || serverUrl.length() == 0) {
            serverUrl = defaultServerUrl;
        }
        this.serverUrl = serverUrl;

        client = new VisionClient(serverUrl);

        start();
    }

    public VisionPoller()
    {
        this(null);
    }

    public TargetResult getLatestTargetInfo()
    {
        String reason;
        TargetInfo info;
        TargetResult result;
        synchronized(this) {
            reason = latestFailureReason;
            info = latestTargetInfo;
        }
        if(info == null) {
            result = new TargetResult(reason);
        } else {
            result = new TargetResult(info);
        }
        return result;
    }

    public synchronized TargetResult getNewTargetInfo(TargetInfo currentTargetInfo, long maxWaitMilliseconds)
    {
        TargetResult result;
        long startMilli = System.nanoTime() / 1000000;
        long endMilli = startMilli + maxWaitMilliseconds;
        while (currentTargetInfo == latestTargetInfo && !shutdownNow) {
            if (maxWaitMilliseconds == 0) {
                try {
                    wait();
                } catch (InterruptedException e) {
                }
            } else {
                long currentMilli = System.nanoTime() / 1000000;
                if (currentMilli >= endMilli) {
                    break;
                }
                long waitMilli = endMilli - currentMilli;
                try {
                    wait(waitMilli);
                } catch (InterruptedException e) {
                }
            }
        }
        if(latestTargetInfo == null) {
            result = new TargetResult(latestFailureReason);
        } else if (currentTargetInfo == latestTargetInfo) {
            result = new TargetResult("A new target frame was not captured in the time allotted");
        } else {
            result = new TargetResult(latestTargetInfo);
        }
        return result;
    }

    public void close()
    {
        if (!shutdownNow) {
            System.out.println("VisionPoller: beginning shutdown");
            shutdownNow = true;
            try {
                join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void run()
    {
        System.out.println("VisionPoller: synchronizing time with server");
        while (!shutdownNow) {
            try {
                client.getServerShift();
                break;
            } catch (Exception e) {
                synchronized(this) {
                    latestTargetInfo = null;
                    latestFailureReason = "Unable to sync time with vision server: " + e.getMessage();
                    notifyAll();
                }

                System.out.println("VisionPoller: " + latestFailureReason);
                if (!shutdownNow) {
                    try {
                        TimeUnit.SECONDS.sleep(5);
                    } catch(InterruptedException e2) {
                    }
                }
            }
        }

        System.out.println("VisionPoller: time successfully sync'ed with server");

        while (!shutdownNow) {
            pollCount++;
            try {
                TargetInfo info = client.getTargetInfo();
                synchronized(this) {
                    latestFailureReason = null;
                    latestTargetInfo = info;
                }
            } catch (VisionException e) {
                synchronized(this) {
                    latestTargetInfo = null;
                    latestFailureReason = "Unable to locate target: " + e.getMessage();
                    notifyAll();
                }

                System.out.println("VisionPoller: " + latestFailureReason);
            } catch (Exception e) {
                synchronized(this) {
                    latestTargetInfo = null;
                    latestFailureReason = "Unable to query vision server for target status: " + e.getMessage();
                    notifyAll();
                }

                System.out.println("VisionPoller: " + latestFailureReason);
                if (!shutdownNow) {
                    try {
                        TimeUnit.MILLISECONDS.sleep(50);
                    } catch(InterruptedException e2) {
                    }
                }
            }
        }

        System.out.println("VisionPoller: Polling thread shutting down");

        synchronized(this) {
            latestTargetInfo = null;
            latestFailureReason = "VisionPoller is shutting down";
            notifyAll();
        }
    }

}
