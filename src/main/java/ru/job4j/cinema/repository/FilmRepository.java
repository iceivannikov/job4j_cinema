package ru.job4j.cinema.repository;

import ru.job4j.cinema.dto.FilmDto;
import ru.job4j.cinema.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmRepository {

    List<Film> findAll();
    Optional<Film> findById(Integer id);
}