package ru.job4j.cinema.service;

import ru.job4j.cinema.model.User;

import java.util.Optional;

public interface UserService {

    Optional<User> register(User user);

    Optional<User> findByEmailAndPassword(String email, String password);

    Optional<User> findById(Integer id);
}
