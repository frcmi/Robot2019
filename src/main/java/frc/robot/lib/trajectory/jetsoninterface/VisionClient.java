package frc.robot.lib.trajectory.jetsoninterface;

import frc.robot.lib.trajectory.WaypointSequence;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import javax.ws.rs.core.UriBuilder;
// import java.util.concurrent.locks.Lock;
// import java.util.concurrent.locks.ReentrantLock;


import org.jboss.resteasy.client.jaxrs.engines.ApacheHttpClient4Engine;
import frc.robot.lib.trajectory.jetsoninterface.model.*;

public class VisionClient {
    public static final String defaultServerUrl = "http://tegra-ubuntu.local:5800";
    public static int numSyncSamples = 20;
    public static double nanosPerSecond = 1000000000.0;

    private Object reqMutex = new Object();
    private String serverUrl;
    private ResteasyClient ezClient;
    private ResteasyWebTarget ezTarget;
    private ServicesInterface proxy;
    /* The number of nanoseconds that the server's monotonic clock is ahead of our System.nanoTime() */
    private long serverShiftNano = 0;
    private boolean haveServerShift = false;
    private long averageSyncLatencyNano = 0;

    public VisionClient(String serverUrl) {
        if (serverUrl == null || serverUrl.length() == 0) {
            serverUrl = defaultServerUrl;
        }
        this.serverUrl = serverUrl;

        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(cm).build();
        cm.setMaxTotal(200); // Increase max total connection to 200
        cm.setDefaultMaxPerRoute(20); // Increase default max connection per route to 20
        ApacheHttpClient4Engine engine = new ApacheHttpClient4Engine(httpClient);

        ezClient = new ResteasyClientBuilder().httpEngine(engine).build();
        ezTarget = ezClient.target(UriBuilder.fromPath(serverUrl));
        proxy = ezTarget.proxy(ServicesInterface.class);
    }

    public double getServerUnadjustedMonotonicSecs()
    {
        TimeResponse resp = proxy.time();
        if (!resp.success) {
            throw new VisionException(resp.error);
        }
        return resp.data.ts_request_mono;
    }

    public long getLocalNanosecs() {
        return System.nanoTime();
    }

    public double getLocalSecs() {
        return System.nanoTime() / nanosPerSecond;
    }

    public long serverToLocalTime(double monoSecs)
    {
        if (!haveServerShift) {
            throw new RuntimeException("Request for server local time prior to synchronization");
        }
        return (long)(monoSecs * nanosPerSecond) - serverShiftNano;
    }

    long serverToLocalTimeSync(double monoSecs)
    {
        long serverShift = getServerShift();
        return (long)(monoSecs * nanosPerSecond) - serverShift;
    }

    public long getServerShift()
    {
        if (serverShiftNano == 0 || !haveServerShift) {
            synchronized(reqMutex) {
                if(!haveServerShift) {
                    long totalLatency = 0;
                    long totalShiftNano = 0;
                    for (int i = 0; i < numSyncSamples; i++) {
                        /* BEGIN TIME CRITICAL */
                        long localNanoPre = System.nanoTime();
                        double serverMono = getServerUnadjustedMonotonicSecs();
                        long localNanoPost = System.nanoTime();
                        /* END TIME CRITICAL */
                        long serverNano = (long)(serverMono * nanosPerSecond);
                        long latencyNano = localNanoPost - localNanoPre;
                        totalLatency += latencyNano;
                        /* Estimate local time to be halfway between request and response */
                        double localNano = localNanoPre + latencyNano / 2;
                        double shiftNano = serverNano - localNano;
                        totalShiftNano += shiftNano;
                    }
                    averageSyncLatencyNano = totalLatency / numSyncSamples;
                    serverShiftNano = totalShiftNano / numSyncSamples;
                    haveServerShift = true;
                }
            }
        }
        return serverShiftNano;
    }

    public long getServerNanosecs() {
        getServerShift();
        TimeResponse resp = proxy.time();
        if (!resp.success) {
            throw new VisionException(resp.error);
        }
        return serverToLocalTime(resp.data.ts_request_mono);
    }

    public TargetInfo getTargetInfo() {
        getServerShift();
        TargetInfoResponse resp = proxy.targetInfo();
        if (!resp.success) {
            throw new VisionException(resp.error);
        }
        return new TargetInfo(resp.data, serverToLocalTime(resp.data.ts_post_mono));
    }

    public void setProperty(String name, Object value) {
        SimpleResponse resp = proxy.setProperty(name, value);
        if (!resp.success) {
            throw new VisionException(resp.error);
        }
    }

    public Object getProperty(String name) {
        GetPropertyResponse resp = proxy.getProperty(name);
        if (!resp.success) {
            throw new VisionException(resp.error);
        }
        return resp.data;
    }

    public void setStringProperty(String name, String value) {
        SimpleResponse resp = proxy.setProperty(name, (Object)value);
        if (!resp.success) {
            throw new VisionException(resp.error);
        }
    }

    public String getStringProperty(String name) {
        GetPropertyResponse resp = proxy.getProperty(name);
        if (!resp.success) {
            throw new VisionException(resp.error);
        }
        return (String)resp.data;
    }

    public void setWaypointSequenceProperty(String name, WaypointSequence value) {
        SimpleResponse resp = proxy.setWaypointSequenceProperty(name, value);
        if (!resp.success) {
            throw new VisionException(resp.error);
        }
    }

    public WaypointSequence getWaypointSequenceProperty(String name) {
        GetWaypointSequencePropertyResponse resp = proxy.getWaypointSequenceProperty(name);
        if (!resp.success) {
            throw new VisionException(resp.error);
        }
        return resp.data;
    }
}
