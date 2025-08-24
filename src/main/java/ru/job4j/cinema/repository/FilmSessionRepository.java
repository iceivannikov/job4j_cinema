package ru.job4j.cinema.repository;

import ru.job4j.cinema.model.FilmSession;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface FilmSessionRepository {

    List<FilmSession> findAll();
    Optional<FilmSession> findById(Integer id);
    List<FilmSession> findByFilmId(Integer filmId);
    List<FilmSession> findByHallId(Integer hallId);
    List<FilmSession> findByTimeRange(LocalDateTime start, LocalDateTime end);
    List<FilmSession> findActiveSessionsByFilmId(Integer filmId);
    List<FilmSession> findTodaysSessions();
    FilmSession save(FilmSession filmSession);
    boolean update(FilmSession filmSession);
    boolean deleteById(Integer id);
}
