package ru.job4j.cinema.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.job4j.cinema.model.Hall;
import ru.job4j.cinema.repository.HallRepository;
import ru.job4j.cinema.service.HallService;

import java.util.Optional;

@Service
@AllArgsConstructor
public class HallServiceImpl implements HallService {

    private final HallRepository hallRepository;

    @Override
    public Optional<Hall> findById(Integer id) {
        return hallRepository.findById(id);
    }
}
