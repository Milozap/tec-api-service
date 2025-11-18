package com.mzap.apiservice.client;

import com.mzap.apiservice.dto.MovieDTO;
import com.mzap.apiservice.dto.PageResponse;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class StorageServiceClient {
    private final WebClient webClient;
    public static final String CORRELATION_ID_HEADER = "X-Correlation-ID";

    public StorageServiceClient(WebClient.Builder builder) {
        String serviceName = "storage-service";
        this.webClient = builder.baseUrl("lb://" + serviceName).build();
    }

    public PageResponse<MovieDTO> getAllMovies(String correlationId, int page, int size) {
        return webClient
                .get()
                .uri("/movies?page={}&size={}", page, size)
                .header(CORRELATION_ID_HEADER, correlationId)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<PageResponse<MovieDTO>>() {})
                .block();
    }

    public MovieDTO getMovieById(String correlationId, Long id) {
        return webClient
                .get()
                .uri("/movies/" + id)
                .header(CORRELATION_ID_HEADER, correlationId)
                .retrieve()
                .bodyToMono(MovieDTO.class)
                .block();
    }

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

    public void deleteMovie(String correlationId, Long id) {
        webClient
                .delete()
                .uri("/movies/{id}", id)
                .header(CORRELATION_ID_HEADER, correlationId)
                .retrieve()
                .toBodilessEntity()
                .block();
    }
}
