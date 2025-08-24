package ru.job4j.cinema.repository;

import ru.job4j.cinema.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    Optional<User> findById(Integer id);
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    User save(User user);
    boolean update(User user);
}
