package com.mzap.apiservice.web;

import com.mzap.apiservice.client.StorageServiceClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/movies")
public class MovieApiController {
    private static final Logger logger = LoggerFactory.getLogger(MovieApiController.class);

    private final StorageServiceClient storageServiceClient;

    public MovieApiController(StorageServiceClient storageServiceClient) {
        this.storageServiceClient = storageServiceClient;
    }

    @GetMapping
    public String listMovies(@RequestHeader(value = "X-Correlation-ID", required = false) String correlationId) {
        logger.info("API SERVICE: GET /movies correlationId={}", correlationId);
        return storageServiceClient.getMoviesRaw();
    }
}
