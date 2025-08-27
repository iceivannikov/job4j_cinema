package ru.job4j.cinema.dto;

import java.time.LocalDateTime;


public record FilmSessionDto(
        Integer id,
        String filmName,
        String filmDescription,
        String posterPath,
        LocalDateTime startTime,
        LocalDateTime endTime,
        Integer price
) {}
