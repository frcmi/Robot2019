package frc.robot.lib.trajectory.jetsoninterface;

import frc.robot.lib.util.SnailMath;
import frc.robot.lib.util.RobotMap;

import java.util.concurrent.TimeUnit;
import java.util.Arrays;

public class VisionPoller extends Thread {
    public static String defaultServerUrl = VisionClient.defaultServerUrl;

    private String serverUrl;
    private VisionClient client;
    private TargetInfo latestTargetInfo = null;
    private String latestFailureReason = "Not yet time-synced";
    private boolean shutdownNow = false;
    private long pollCount = 0;

    String[] allowedErrors = { "Unable to find 2 target contours", "Could not find 2 targets",
            "Hull does not have 6 vertices", "Unable to determine target pose using solvepnp" };

    private static VisionPoller instance;

    public static VisionPoller getInstance() {
        if (instance == null)
            instance = new VisionPoller(defaultServerUrl);
        return instance;
    }

    public static String normalizeUrl(String serverUrl) {
        if (serverUrl == null || serverUrl.length() == 0) {
            serverUrl = defaultServerUrl;
        }
        return serverUrl;
    }

    private VisionPoller(String serverUrl) {
        super("VisionPoller@" + normalizeUrl(serverUrl));
        this.serverUrl = normalizeUrl(serverUrl);

        client = new VisionClient(serverUrl);

        start();
    }

    public VisionPoller() {
        this(null);
    }

    public VisionClient getClient() {
        return client;
    }

    public TargetResult getLatestTargetInfo() {
        String reason;
        TargetInfo info;
        TargetResult result;
        synchronized (this) {
            reason = latestFailureReason;
            info = latestTargetInfo;
        }
        if (info == null) {
            result = new TargetResult(reason);
        } else {
            result = new TargetResult(info);
        }
        return result;
    }

    // Returns current TargetInfo, handles errors
    public TargetInfo getLatestTargetInfoHandleErrors() {
        TargetResult latest = getLatestTargetInfo();
        if (latest.success == true) {
            return latest.info;
        } else {
            return null;
        }
    }

    public synchronized TargetResult getNewTargetInfo(TargetInfo currentTargetInfo, long maxWaitMilliseconds) {
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
        if (latestTargetInfo == null) {
            result = new TargetResult(latestFailureReason);
        } else if (currentTargetInfo == latestTargetInfo) {
            result = new TargetResult("A new target frame was not captured in the time allotted");
        } else {
            result = new TargetResult(latestTargetInfo);
        }
        return result;
    }

    public void close() {
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

    public void run() {
        System.out.println("VisionPoller: synchronizing time with server");
        while (!shutdownNow) {
            try {
                client.getServerShift();
                break;
            } catch (Exception e) {
                synchronized (this) {
                    e.printStackTrace();
                    latestTargetInfo = null;
                    latestFailureReason = "Unable to sync time with vision server: " + e.getMessage();
                    notifyAll();
                }

                printError(latestFailureReason);
                if (!shutdownNow) {
                    try {
                        TimeUnit.SECONDS.sleep(5);
                    } catch (InterruptedException e2) {
                    }
                }
            }
        }

        System.out.println("VisionPoller: time successfully sync'ed with server");

        while (!shutdownNow) {
            pollCount++;
            try {
                TargetInfo info = client.getTargetInfo();
                synchronized (this) {
                    latestFailureReason = null;
                    latestTargetInfo = info;
                }
            } catch (VisionException e) {
                synchronized (this) {
                    latestTargetInfo = null;
                    latestFailureReason = "Unable to locate target: " + e.getMessage();
                    notifyAll();
                }

                printError(latestFailureReason);
            } catch (Exception e) {
                synchronized (this) {
                    latestTargetInfo = null;
                    latestFailureReason = "Unable to query vision server for target status: " + e.getMessage();
                    notifyAll();
                }

                printError(latestFailureReason);
                if (!shutdownNow) {
                    try {
                        TimeUnit.MILLISECONDS.sleep(50);
                    } catch (InterruptedException e2) {
                    }
                }
            }
        }

        System.out.println("VisionPoller: Polling thread shutting down");

        synchronized (this) {
            latestTargetInfo = null;
            latestFailureReason = "VisionPoller is shutting down";
            notifyAll();
        }
    }

    private void printError(String errorMessage) {
        boolean allowed = false;
        for (int i = 0; i < allowedErrors.length; i++) {
            if (errorMessage.contains(allowedErrors[i])) {
                allowed = true;
            }
        }
        if (!allowed) {
            System.out.println("VisionPoller: " + errorMessage);
        }
    }

    // Returns the relative postion from robot to the vision target
    public Delta getRelativePosition() {
        TargetInfo info = getLatestTargetInfoHandleErrors();
        if (info == null) {
            return null;
        } else {
            return new Delta(info.y / SnailMath.inchesToMeters, info.x / SnailMath.inchesToMeters,
                    -info.rx * Math.PI / 180, info.nanoTime);
        }
    }

    // Stores x and y which represent the distance forward to the board and the
    // sideways distance respectively, and the
    // sideways angle of the board in radians
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
}
