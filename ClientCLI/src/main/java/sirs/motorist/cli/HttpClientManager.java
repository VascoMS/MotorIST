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

import java.io.IOException;

public class HttpClientManager {

    private static final HttpClient httpClient = HttpClients.createDefault();

    public static String executeHttpRequest(String url, String method, String jsonBody) {
        try {
            switch (method) {
                case "GET":
                    return sendGetRequest(url);
                case "POST":
                    return sendPostRequest(url, jsonBody);
                case "PUT":
                    return sendPutRequest(url, jsonBody);
                case "DELETE":
                    return sendDeleteRequest(url);
                default:
                    return "Invalid HTTP method";
            }
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    private static String sendGetRequest(String url) throws IOException {
        HttpGet request = new HttpGet(url);
        HttpResponse response = httpClient.execute(request);
        return handleResponse(response);
    }

    private static String sendPostRequest(String url, String jsonBody) throws IOException {
        HttpPost request = new HttpPost(url);
        request.setEntity(new StringEntity(jsonBody));
        request.setHeader("Content-Type", "application/json");
        HttpResponse response = httpClient.execute(request);
        return handleResponse(response);
    }

    private static String sendPutRequest(String url, String jsonBody) throws IOException {
        HttpPut request = new HttpPut(url);
        request.setEntity(new StringEntity(jsonBody));
        request.setHeader("Content-Type", "application/json");
        HttpResponse response = httpClient.execute(request);
        return handleResponse(response);
    }

    private static String sendDeleteRequest(String url) throws IOException {
        HttpDelete request = new HttpDelete(url);
        HttpResponse response = httpClient.execute(request);
        return handleResponse(response);
    }

    private static String handleResponse(HttpResponse response) throws IOException {
        int statusCode = response.getStatusLine().getStatusCode();
        String responseBody = EntityUtils.toString(response.getEntity());

        if (statusCode >= 200 && statusCode < 300) {
            return responseBody;
        } else {
            return "Request failed. Status code: " + statusCode + ". Response: " + responseBody;
        }
    }
}
