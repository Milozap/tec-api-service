package com.mzap.apiservice.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class MovieDTO {

    private Long id;

    private LocalDateTime createdAt;

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Genre is required")
    private String genre;

    @NotNull
    @Min(value = 1800, message = "Release year cannot be lower than 1800")
    @Max(value = 2100, message = "Release year cannot be higher than 2100")
    private Integer releaseYear;

    public MovieDTO() {
    }

    public MovieDTO(Long id, LocalDateTime createdAt, String title, String genre, Integer releaseYear) {
        this.id = id;
        this.createdAt = createdAt;
        this.title = title;
        this.genre = genre;
        this.releaseYear = releaseYear;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public Integer getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(Integer releaseYear) {
        this.releaseYear = releaseYear;
    }
}
