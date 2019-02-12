package org.mercerislandschools.mihs.frc.vision.client;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;

import org.mercerislandschools.mihs.frc.vision.client.model.ErrorInfo;

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
