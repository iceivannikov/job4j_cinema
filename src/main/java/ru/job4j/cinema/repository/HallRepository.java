package ru.job4j.cinema.repository;

import ru.job4j.cinema.model.Hall;

import java.util.List;
import java.util.Optional;

public interface HallRepository {
    List<Hall> findAll();
    Optional<Hall> findById(Integer id);
    Hall save(Hall hall);
    boolean update(Hall hall);
    boolean deleteById(Integer id);
}
