package ru.job4j.cinema.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.job4j.cinema.dto.FilmDto;
import ru.job4j.cinema.dto.FilmSessionDetailDto;
import ru.job4j.cinema.dto.FilmSessionDto;
import ru.job4j.cinema.model.*;
import ru.job4j.cinema.repository.*;
import ru.job4j.cinema.service.*;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class FilmSessionServiceImpl implements FilmSessionService {

    private final FilmSessionRepository filmSessionRepository;

    private final FilmService filmService;
    private final HallService hallService;

    @Override
    public List<FilmSessionDto> findAll() {
        List<FilmSession> filmSessionList = filmSessionRepository.findAll();
        return filmSessionList.stream()
                .map(this::mapToFilmSessionDto)
                .toList();
    }

    @Override
    public FilmSessionDetailDto findById(Integer id) {
        Optional<FilmSession> byId = filmSessionRepository.findById(id);
        return byId.map(this::mapToFilmSessionDetailDto).orElse(null);
    }

    private FilmSessionDto mapToFilmSessionDto(FilmSession filmSession) {
        FilmDto filmDto = getFilm(filmSession);
        return new FilmSessionDto(
                filmSession.getId(),
                filmDto.name(),
                filmDto.description(),
                filmDto.filePath(),
                filmSession.getStartTime(),
                filmSession.getEndTime(),
                filmSession.getPrice()
        );
    }

    private FilmSessionDetailDto mapToFilmSessionDetailDto(FilmSession filmSession) {
        FilmDto filmDto = getFilm(filmSession);
        Optional<Hall> optionalHall = hallService.findById(filmSession.getHallId());
        String hallName = optionalHall.map(Hall::getName).orElse("");
        String hallDescription = optionalHall.map(Hall::getDescription).orElse("");
        Integer hallRowCount = optionalHall.map(Hall::getRowCount).orElse(null);
        Integer hallPlaceCount = optionalHall.map(Hall::getPlaceCount).orElse(null);
        return new FilmSessionDetailDto(
                filmSession.getId(),
                filmSession.getStartTime(),
                filmSession.getEndTime(),
                filmSession.getPrice(),
                filmDto.name(),
                filmDto.description(),
                filmDto.year(),
                filmDto.minimalAge(),
                filmDto.durationInMinutes(),
                filmDto.genre(),
                filmDto.filePath(),
                hallName,
                hallDescription,
                hallRowCount,
                hallPlaceCount
        );
    }

    private FilmDto getFilm(FilmSession filmSession) {
        FilmDto filmDto = null;
        Optional<FilmDto> optionalFilm = filmService.findById(filmSession.getFilmId());
        if (optionalFilm.isPresent()) {
            filmDto = optionalFilm.get();
        }
        return filmDto;
    }
}
