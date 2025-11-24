package com.mzap.apiservice.web;

import com.mzap.apiservice.client.StorageServiceClient;
import com.mzap.apiservice.dto.MovieDTO;
import com.mzap.apiservice.dto.PageResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/movies")
@SecurityRequirement(name = "bearerAuth")
public class MovieApiController {
    private static final Logger logger = LoggerFactory.getLogger(MovieApiController.class);
    public static final String CORRELATION_ID_HEADER = "X-Correlation-ID";

    private final StorageServiceClient storageServiceClient;

    public MovieApiController(StorageServiceClient storageServiceClient) {
        this.storageServiceClient = storageServiceClient;
    }

    @GetMapping
    public PageResponse<MovieDTO> listMovies(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) Integer yearFrom,
            @RequestParam(required = false) Integer yearTo,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortDir
    ) {
        String correlationId = MDC.get(CORRELATION_ID_HEADER);
        logger.info("API SERVICE: GET /movies correlationId={}", correlationId);

        return storageServiceClient.getMoviesPage(correlationId, page, size, title, genre, yearFrom, yearTo, sortBy, sortDir);
    }

    @GetMapping("/{id}")
    public MovieDTO getMovie(@PathVariable Long id) {
        String correlationId = MDC.get(CORRELATION_ID_HEADER);
        logger.info("API SERVICE: GET /movies/{} correlationId={}", id, correlationId);

        return storageServiceClient.getMovieById(correlationId, id);
    }

    @PostMapping
    public ResponseEntity<MovieDTO> createMovie(@Valid @RequestBody MovieDTO movie) {
        String correlationId = MDC.get(CORRELATION_ID_HEADER);
        logger.info("API SERVICE: POST /movies correlationId={}", correlationId);
        MovieDTO created = storageServiceClient.createMovie(correlationId, movie);

        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MovieDTO> updateMovie(
            @PathVariable Long id,
            @Valid @RequestBody MovieDTO movie
    ) {
        String correlationId = MDC.get(CORRELATION_ID_HEADER);
        logger.info("API SERVICE: PUT /movies/{} correlationId={}", id, correlationId);
        try {
            MovieDTO updated = storageServiceClient.updateMovie(correlationId, id, movie);
            return ResponseEntity.ok(updated);
        } catch (Exception _) {
            logger.warn("Movie {} not found for update", id);
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMovie(@PathVariable Long id) {
        String correlationId = MDC.get(CORRELATION_ID_HEADER);
        logger.info("API SERVICE: DELETE /movies/{} correlationId={}", id, correlationId);
        storageServiceClient.deleteMovie(correlationId, id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/dev/chaos")
    public ResponseEntity<String> chaos(
            @RequestParam(name = "delay", defaultValue = "0") long delay,
            @RequestParam(name = "errorRate", defaultValue = "0.0") double errorRate
    ) {
        String correlationId = MDC.get(CORRELATION_ID_HEADER);
        logger.info("API SERVICE: GET /movies/dev/chaos delay={} errorRate={} correlationId={}", delay, errorRate, correlationId);

        String result = storageServiceClient.callChaos(correlationId, delay, errorRate);
        return ResponseEntity.ok(result);
    }
}
