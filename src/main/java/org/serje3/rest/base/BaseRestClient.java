package org.serje3.rest.base;

import com.google.gson.Gson;
import org.serje3.config.BotConfig;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpClient;

import java.util.concurrent.CompletableFuture;

public class BaseRestClient {

    private final Gson gson = new Gson();
    private final String base_url;

    {
        try {
            base_url = BotConfig.getProperty("backend_url");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private final HttpClient httpClient;

    public BaseRestClient() {
        this.httpClient = HttpClient.newHttpClient();
    }

    protected <T> CompletableFuture<T> get(String url, Class<T> responseClass) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(getFullUrl(url)))
                .GET()
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    System.out.println(response + " body:"+response.body() + " " + responseClass);
                    return fromJson(response.body(), responseClass);
                });
    }

    protected <T> CompletableFuture<T> post(String url, RequestBody requestBody, Class<T> responseClass) {
        HttpRequest request = createHttpRequestBuilder()
                .uri(URI.create(getFullUrl(url)))
                .POST(requestBody.toBodyPublisher())
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> fromJson(response.body(), responseClass));
    }


    protected <T> CompletableFuture<T> put(String url, RequestBody requestBody, Class<T> responseClass) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(getFullUrl(url)))
                .PUT(requestBody.toBodyPublisher())
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> fromJson(response.body(), responseClass));
    }

    protected <T> CompletableFuture<T> delete(String url, RequestBody requestBody, Class<T> responseClass) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(getFullUrl(url)))
                .DELETE()
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> fromJson(response.body(), responseClass));
    }

    private String getFullUrl(String url) {
        if (!base_url.endsWith("/") && !url.startsWith("/")) {
            url = "/" + url;
        } else if (base_url.endsWith("/") && url.startsWith("/")) {
            url = url.substring(1);
        }
        return base_url.concat(url);
    }

    private HttpRequest.Builder createHttpRequestBuilder() {
        return HttpRequest.newBuilder()
                .header("Content-Type", "application/json");
    }


    private <T> T fromJson(String json, Class<T> def) {
        System.out.println("CALLED json:"+json + " " + def);
        T t = gson.fromJson(json, def);
        System.out.println("WTF:" + json + " " + t + " " + def);
        return t;
    }
}
