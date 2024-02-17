package org.serje3.rest.base;

import org.serje3.config.BotConfig;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpClient;
import java.net.http.HttpResponse.BodyHandlers;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class BaseRestClient {
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

    protected <T> CompletableFuture<ResponseBody<T>> get(String url, HttpResponse.BodyHandler<T> responseBodyHandler) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(getFullUrl(url)))
                .GET()
                .build();

        return httpClient.sendAsync(request, responseBodyHandler)
                .thenApply(response -> new ResponseBody<>(response.body()));
    }

    protected <T> CompletableFuture<ResponseBody<T>> post(String url, RequestBody requestBody, HttpResponse.BodyHandler<T> responseBodyHandler) {
        HttpRequest request = createHttpRequestBuilder()
                .uri(URI.create(getFullUrl(url)))
                .POST(requestBody.toBodyPublisher())
                .build();

        return httpClient.sendAsync(request, responseBodyHandler)
                .thenApply(response -> new ResponseBody<>(response.body()));
    }


    protected <T> CompletableFuture<ResponseBody<T>> put(String url, RequestBody requestBody, HttpResponse.BodyHandler<T> responseBodyHandler) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(getFullUrl(url)))
                .PUT(requestBody.toBodyPublisher())
                .build();

        return httpClient.sendAsync(request, responseBodyHandler)
                .thenApply(response -> new ResponseBody<>(response.body()));
    }

    protected <T> CompletableFuture<ResponseBody<T>> delete(String url, RequestBody requestBody, HttpResponse.BodyHandler<T> responseBodyHandler) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(getFullUrl(url)))
                .DELETE()
                .build();

        return httpClient.sendAsync(request, responseBodyHandler)
                .thenApply(response -> new ResponseBody<>(response.body()));
    }

    private String getFullUrl(String url) {
        if (!base_url.endsWith("/") && !url.startsWith("/")){
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

}
