package ru.job4j.cinema.dto;

public record FilmDto(
        Integer id,
        String name,
        String description,
        Integer year,
        Integer minimalAge,
        Integer durationInMinutes,
        String genre,
        String filePath
) { }
