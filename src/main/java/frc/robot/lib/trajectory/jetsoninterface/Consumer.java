package frc.robot.lib.trajectory.jetsoninterface;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;

import frc.robot.lib.trajectory.jetsoninterface.model.ErrorInfo;

public class Consumer {
    public static ErrorInfo consumeWithJsonb(String targetUrl) {
        Client client = ClientBuilder.newClient();
        Response response = client.target(targetUrl).request().get();
        ErrorInfo result = response.readEntity(ErrorInfo.class);
        response.close();
        client.close();

        return result;
    }
}
