package com.mzap.apiservice.client;

import com.mzap.apiservice.dto.MovieDTO;
import com.mzap.apiservice.dto.PageResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collections;

@Component
public class StorageServiceClient {
    private final WebClient webClient;
    public static final String CORRELATION_ID_HEADER = "X-Correlation-ID";
    public static final Logger logger = LoggerFactory.getLogger(StorageServiceClient.class);

    public StorageServiceClient(WebClient.Builder builder) {
        String serviceName = "storage-service";
        this.webClient = builder.baseUrl("lb://" + serviceName).build();
    }

    @CircuitBreaker(name = "storageService", fallbackMethod = "getMoviesPageFallback")
    @Retry(name = "storageService")
    public PageResponse<MovieDTO> getMoviesPage(String correlationId, int page, int size) {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/movies")
                        .queryParam("page", page)
                        .queryParam("size", size)
                        .build()
                )
                .header(CORRELATION_ID_HEADER, correlationId)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<PageResponse<MovieDTO>>() {})
                .block();
    }

    public PageResponse<MovieDTO> getMoviesPageFallback(String correlationId, int page, int size, Exception exception) {
        logger.warn("Fallback for getMoviesPage triggered with correlationId: {}, page: {}, size: {} and exception: {}",
                correlationId, page, size, exception.getMessage());

        return new PageResponse<>(Collections.emptyList(), page, size, 0, 0, true);
    }

    @CircuitBreaker(name = "storageService", fallbackMethod = "getMovieByIdFallback")
    @Retry(name = "storageService")
    public MovieDTO getMovieById(String correlationId, Long id) {
        return webClient
                .get()
                .uri("/movies/" + id)
                .header(CORRELATION_ID_HEADER, correlationId)
                .retrieve()
                .bodyToMono(MovieDTO.class)
                .block();
    }

    public MovieDTO getMovieByIdFallback(String correlationId, Long id, Exception exception) {
        logger.warn("Fallback for getMovieById triggered with correlationId: {}, id: {} and exception: {}",
                correlationId, id, exception.getMessage());

        return new MovieDTO();
    }

    @CircuitBreaker(name = "storageService", fallbackMethod = "createMovieFallback")
    @Retry(name = "storageService")
    public MovieDTO createMovie(String correlationId, MovieDTO movie) {
        return webClient
                .post()
                .uri("/movies")
                .header(CORRELATION_ID_HEADER, correlationId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(movie)
                .retrieve()
                .bodyToMono(MovieDTO.class)
                .block();
    }
    public MovieDTO createMovieFallback(String correlationId, MovieDTO movie, Exception exception) {
        logger.warn("Fallback for createMovie triggered with correlationId: {}, movie DTO: {} and exception: {}",
                correlationId, movie, exception.getMessage());

        return new MovieDTO();
    }

    @CircuitBreaker(name = "storageService", fallbackMethod = "updateMovieFallback")
    @Retry(name = "storageService")
    public MovieDTO updateMovie(String correlationId, Long id, MovieDTO movie) {
        return webClient
                .put()
                .uri("/movies/{id}", id)
                .header(CORRELATION_ID_HEADER, correlationId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(movie)
                .retrieve()
                .bodyToMono(MovieDTO.class)
                .block();
    }

    public MovieDTO updateMovieFallback(String correlationId, Long id, MovieDTO movie, Exception exception) {
        logger.warn("Fallback for updateMovie triggered with correlationId: {}, movie id: {}, movie DTO: {} and exception: {}",
                correlationId, id, movie, exception.getMessage());

        return new MovieDTO();
    }

    @CircuitBreaker(name = "storageService", fallbackMethod = "deleteMovieFallback")
    @Retry(name = "storageService")
    public void deleteMovie(String correlationId, Long id) {
        webClient
                .delete()
                .uri("/movies/{id}", id)
                .header(CORRELATION_ID_HEADER, correlationId)
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    public void deleteMovieFallback(String correlationId, Long id, Exception exception) {
        logger.warn("Fallback for deleteMovie triggered with correlationId: {}, movie id: {} and exception: {}",
                correlationId, id, exception.getMessage());
    }

    @CircuitBreaker(name = "storageService", fallbackMethod = "callChaosFallback")
    @Retry(name = "storageService")
    public String callChaos(String correlationId, Long delay, Double errorRate) {
        logger.info("API Service: calling storage chaos with delay={} errorRate={}", delay, errorRate);

        return webClient
                .get()
                .uri(uriBuilder -> {
                    var uri = uriBuilder.path("/movies/dev/chaos");
                    if (delay != null && delay > 0) {
                        uri.queryParam("delay", delay);
                    }
                    if (errorRate != null && errorRate > 0) {
                        uri.queryParam("errorRate", errorRate);
                    }
                    return uri.build();
                })
                .header(CORRELATION_ID_HEADER, correlationId)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    private String callChaosFallback(String correlationId, Long delay, Double errorRate, Exception exception) {
        logger.warn("API Service: chaos fallback triggered with correlationId: {}, delay: {} errorRate: {} exception: {}",
                correlationId, delay, errorRate, exception.getMessage());
        return "Chaos fallback from API (circuit breaker / retry kicked in)";
    }

}
