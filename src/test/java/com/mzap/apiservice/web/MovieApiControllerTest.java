package com.mzap.apiservice.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mzap.apiservice.client.StorageServiceClient;
import com.mzap.apiservice.dto.MovieDTO;
import com.mzap.apiservice.dto.PageResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = MovieApiController.class)
@AutoConfigureMockMvc(addFilters = false)
class MovieApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    @MockitoBean
    private StorageServiceClient storageServiceClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("GET /movies should return page of movies")
    void listMovies() throws Exception {
        MovieDTO movie = new MovieDTO(1L, LocalDateTime.now(), "New Movie", "Genre", 2025);
        PageResponse<MovieDTO> page = new PageResponse<>(List.of(movie), 0, 10, 1, 1, true);

        Mockito.when(storageServiceClient.getMoviesPage(any(), anyInt(), anyInt(), any(), any(), any(), any(), any(), any()))
                .thenReturn(page);

        mockMvc.perform(get("/movies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].title", is("New Movie")))
                .andExpect(jsonPath("$.page", is(0)))
                .andExpect(jsonPath("$.size", is(10)));
    }

    @Test
    @DisplayName("GET /movies supports filtering and sorting params and forwards them to storage client")
    void listMovies_withFiltersAndSorting() throws Exception {
        MovieDTO movie = new MovieDTO(10L, LocalDateTime.now(), "New Movie", "Genre", 2025);
        PageResponse<MovieDTO> page = new PageResponse<>(List.of(movie), 0, 5, 1, 1, true);

        Mockito.when(storageServiceClient.getMoviesPage(any(),
                        eq(0), eq(5),
                        eq("Movie"), eq("Genre"),
                        eq(1990), eq(2025),
                        eq("title"), eq("desc"))
                )
                .thenReturn(page);

        mockMvc.perform(get("/movies")
                        .param("page", "0")
                        .param("size", "5")
                        .param("title", "Movie")
                        .param("genre", "Genre")
                        .param("yearFrom", "1990")
                        .param("yearTo", "2025")
                        .param("sortBy", "title")
                        .param("sortDir", "desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].title", is("New Movie")))
                .andExpect(jsonPath("$.size", is(5)));
    }

    @Test
    @DisplayName("GET /movies/{id} should return a movie")
    void getMovie() throws Exception {
        MovieDTO movie = new MovieDTO(2L, LocalDateTime.now(), "New Movie", "Genre", 2025);
        Mockito.when(storageServiceClient.getMovieById(any(), eq(2L))).thenReturn(movie);

        mockMvc.perform(get("/movies/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(2)))
                .andExpect(jsonPath("$.title", is("New Movie")));
    }

    @Test
    @DisplayName("POST /movies should create and return 201")
    void createMovie() throws Exception {
        MovieDTO request = new MovieDTO(null, null, "New Movie", "Genre", 2025);
        MovieDTO created = new MovieDTO(3L, LocalDateTime.now(), request.getTitle(), request.getGenre(), request.getReleaseYear());

        Mockito.when(storageServiceClient.createMovie(any(), any(MovieDTO.class))).thenReturn(created);

        mockMvc.perform(post("/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Content-Type", containsString("application/json")))
                .andExpect(jsonPath("$.id", is(3)))
                .andExpect(jsonPath("$.title", is("New Movie")));
    }

    @Test
    @DisplayName("PUT /movies/{id} should update and return 200")
    void updateMovie_ok() throws Exception {
        MovieDTO request = new MovieDTO(null, null, "New Movie", "Genre", 2025);
        MovieDTO updated = new MovieDTO(4L, LocalDateTime.now(), request.getTitle(), request.getGenre(), request.getReleaseYear());

        Mockito.when(storageServiceClient.updateMovie(any(), eq(4L), any(MovieDTO.class))).thenReturn(updated);

        mockMvc.perform(put("/movies/4")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(4)))
                .andExpect(jsonPath("$.title", is("New Movie")));
    }

    @Test
    @DisplayName("PUT /movies/{id} returns 404 when service throws")
    void updateMovie_notFound() throws Exception {
        MovieDTO request = new MovieDTO(null, null, "New Movie", "Genre", 2025);
        Mockito
                .when(storageServiceClient.updateMovie(any(), eq(404L), any(MovieDTO.class)))
                .thenThrow(new RuntimeException("not found"));

        mockMvc.perform(put("/movies/404")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /movies/{id} should return 204")
    void deleteMovie() throws Exception {
        Mockito.doNothing().when(storageServiceClient).deleteMovie(any(), eq(5L));

        mockMvc.perform(delete("/movies/5"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("GET /movies/dev/chaos should return body")
    void chaos() throws Exception {
        Mockito.when(storageServiceClient.callChaos(any(), eq(100L), eq(0.5))).thenReturn("ok");

        mockMvc.perform(get("/movies/dev/chaos")
                        .param("delay", "100")
                        .param("errorRate", "0.5"))
                .andExpect(status().isOk())
                .andExpect(content().string("ok"));
    }
}
