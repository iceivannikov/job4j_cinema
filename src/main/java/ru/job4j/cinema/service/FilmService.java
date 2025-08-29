package ru.job4j.cinema.service;

import ru.job4j.cinema.dto.FilmDto;

import java.util.List;
import java.util.Optional;

public interface FilmService {

    List<FilmDto> findAll();

    Optional<FilmDto> findById(Integer id);
}
