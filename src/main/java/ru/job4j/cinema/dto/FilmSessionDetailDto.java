package ru.job4j.cinema.dto;

import java.time.LocalDateTime;

public record FilmSessionDetailDto (
        Integer id,
        LocalDateTime startTime,
        LocalDateTime endTime,
        Integer price,
        String filmName,
        String filmDescription,
        Integer filmYear,
        Integer filmMinimalAge,
        Integer filmDurationInMinutes,
        String genreName,
        String posterPath,
        String hallName,
        String hallDescription,
        Integer hallRowCount,
        Integer hallPlaceCount
) {}
