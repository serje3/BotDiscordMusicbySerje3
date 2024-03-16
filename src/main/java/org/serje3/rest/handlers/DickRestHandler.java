package org.serje3.rest.handlers;

import com.google.gson.Gson;
import org.serje3.config.BotConfig;
import org.serje3.rest.base.AbstractBaseClient;
import org.serje3.rest.requests.DickRequest;
import org.serje3.rest.responses.DickResponse;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class DickRestHandler extends AbstractBaseClient {

    private final String base_url;

    {
        try {
            base_url = BotConfig.getProperty("dick_backend_url");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected String getBaseUrl() {
        return base_url;
    }

    public DickResponse generateDickName(String name) {
        try {
            return this.post("/generate/dickname", new DickRequest(name), DickResponse.class).get();
        } catch (InterruptedException e) {
            return new DickResponse("Хуй хуёвый");
        } catch (ExecutionException e) {
            return new DickResponse("Хуй хуёвее");
        } catch (Exception e) {
            return new DickResponse("А вот хуй тебе");
        }
    }

    public List<DickResponse> generateDickName(List<String> names) {
        List<DickResponse> defaultResponses = names.stream().map(DickResponse::new).toList();
        try {
            return this.postBatchDickResponses("/generate/dickname/batch", names.stream().map(DickRequest::new).toList()).get();
        } catch (InterruptedException e) {
            return defaultResponses;
        } catch (ExecutionException e) {
            return defaultResponses;
        } catch (Exception e) {
            return defaultResponses;
        }
    }


    protected CompletableFuture<List<DickResponse>> postBatchDickResponses(String url, List<DickRequest> body) {
        Gson gson = new Gson();
        String json = gson.toJson(body); // Преобразование объекта в JSON
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(getFullUrl(url)))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    System.out.println(response + " body:" + response.body() + " " + DickResponse.class);
                    return fromJsonList(response.body(), DickResponse.class);
                });
    }
}
