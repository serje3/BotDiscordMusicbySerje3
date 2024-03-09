package org.serje3.rest.base;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import lombok.RequiredArgsConstructor;
import org.serje3.config.BotConfig;
import org.serje3.rest.domain.NodeRef;
import org.serje3.utils.gson.LocalDateTimeTypeAdapter;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public abstract class AbstractBaseClient {
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
            .create();
    protected final HttpClient httpClient;

    public AbstractBaseClient() {
        this.httpClient = HttpClient.newHttpClient();
    }

    protected <T> CompletableFuture<T> get(String url, Class<T> responseClass) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(getFullUrl(url)))
                .GET()
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    System.out.println(response + " body:" + response.body() + " " + responseClass);
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

    protected abstract String getBaseUrl();

    protected String getFullUrl(String url) {
        if (!getBaseUrl().endsWith("/") && !url.startsWith("/")) {
            url = "/" + url;
        } else if (getBaseUrl().endsWith("/") && url.startsWith("/")) {
            url = url.substring(1);
        }
        return getBaseUrl().concat(url);
    }

    protected HttpRequest.Builder createHttpRequestBuilder() {
        return HttpRequest.newBuilder()
                .header("Content-Type", "application/json");
    }


    protected  <T> T fromJson(String json, Class<T> def) {
        System.out.println("CALLED json:" + json + " " + def);
        T t = gson.fromJson(json, def);
        System.out.println("WTF:" + json + " " + t + " " + def);
        return t;
    }

    protected <T> List<T> fromJsonList(String json, Class<T> clazz) {
        Type listType = TypeToken.getParameterized(List.class, clazz).getType();
        return gson.fromJson(json, listType);
    }
}
