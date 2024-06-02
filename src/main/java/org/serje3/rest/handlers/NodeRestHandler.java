package org.serje3.rest.handlers;

import dev.arbjerg.lavalink.client.NodeOptions;
import dev.arbjerg.lavalink.client.loadbalancing.RegionGroup;
import org.serje3.rest.base.BaseRestClient;
import org.serje3.rest.domain.NodeRef;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class NodeRestHandler extends BaseRestClient {
    public List<NodeOptions> getNodes() throws ExecutionException, InterruptedException {

        return this.getList("/lavalink/nodes", NodeRef.class).get().stream()
                .map(node -> new NodeOptions.Builder()
                        .setName("node_" +node.getId())
                        .setServerUri(node.getUrl())
                        .setPassword(node.getPassword())
                        .build())
                .toList();
    }

    protected CompletableFuture<List<NodeRef>> getList(String url, Class<NodeRef> responseClass) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(getFullUrl(url)))
                .GET()
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    System.out.println(response + " body:" + response.body() + " " + responseClass);
                    return fromJsonList(response.body(), responseClass);
                });
    }
}
