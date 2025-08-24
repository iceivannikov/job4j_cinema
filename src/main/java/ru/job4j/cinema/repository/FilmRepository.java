package ru.job4j.cinema.repository;

import ru.job4j.cinema.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmRepository {

    List<Film> findAll();
    Optional<Film> findById(Integer id);
    List<Film> findByGenreId(Integer genreId);
    Film save(Film film);
    boolean update(Film film);
    boolean deleteById(Integer id);
}