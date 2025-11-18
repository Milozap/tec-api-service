package com.mzap.apiservice.client;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class StorageServiceClient {
    private final WebClient webClient;

    public StorageServiceClient(WebClient.Builder builder) {
        String serviceName = "storage-service";
        this.webClient = builder.baseUrl("lb://" + serviceName).build();
    }

    public String getMoviesRaw() {
        return webClient.get().uri("/movies").retrieve().bodyToMono(String.class).block();
    }
}
