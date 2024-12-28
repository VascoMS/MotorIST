package sirs.motorist.cli;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class HttpClientManager {

    private static final Logger logger = LoggerFactory.getLogger(HttpClientManager.class);

    private static final HttpClient httpClient = HttpClients.createDefault();

    public static HttpResponse executeHttpRequest(String url, String method, String jsonBody) {
        try {
            return switch (method) {
                case "GET" -> sendGetRequest(url);
                case "POST" -> sendPostRequest(url, jsonBody);
                case "PUT" -> sendPutRequest(url, jsonBody);
                case "DELETE" -> sendDeleteRequest(url);
                default -> {
                    logger.error("Invalid method: {}", method);
                    yield null;
                }
            };
        } catch (Exception e) {
            logger.error("Error executing request: {}", e.getMessage());
            return null;
        }
    }

    private static HttpResponse sendGetRequest(String url) throws IOException {
        HttpGet request = new HttpGet(url);
        return httpClient.execute(request);
    }

    private static HttpResponse sendPostRequest(String url, String jsonBody) throws IOException {
        HttpPost request = new HttpPost(url);
        request.setEntity(new StringEntity(jsonBody));
        request.setHeader("Content-Type", "application/json");
        return httpClient.execute(request);
    }

    private static HttpResponse sendPutRequest(String url, String jsonBody) throws IOException {
        HttpPut request = new HttpPut(url);
        request.setEntity(new StringEntity(jsonBody));
        request.setHeader("Content-Type", "application/json");
        return httpClient.execute(request);
    }

    private static HttpResponse sendDeleteRequest(String url) throws IOException {
        HttpDelete request = new HttpDelete(url);
        return httpClient.execute(request);
    }
}
