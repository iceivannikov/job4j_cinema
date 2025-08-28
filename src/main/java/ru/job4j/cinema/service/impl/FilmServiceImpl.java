package ru.job4j.cinema.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.job4j.cinema.dto.FilmDto;
import ru.job4j.cinema.model.Film;
import ru.job4j.cinema.model.Genre;
import ru.job4j.cinema.repository.FilmRepository;
import ru.job4j.cinema.service.FilmService;
import ru.job4j.cinema.service.GenreService;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class FilmServiceImpl implements FilmService {

    private final FilmRepository filmRepository;
    private final GenreService genreService;

    @Override
    public List<FilmDto> findAll() {
        var films = filmRepository.findAll();
        return films.stream()
                .map(this::mapToFilmDto)
                .toList();
    }

    @Override
    public Optional<FilmDto> findById(Integer id) {
        return filmRepository.findById(id).map(this::mapToFilmDto);
    }

    private FilmDto mapToFilmDto(Film film) {
        String genreName = genreService
                .findById(film.getGenreId())
                .map(Genre::getName)
                .orElse("Неизвестно");
        String fileUrl = "/files/" + film.getFileId();
        return new FilmDto(
                film.getId(),
                film.getName(),
                film.getDescription(),
                film.getYear(),
                film.getMinimalAge(),
                film.getDurationInMinutes(),
                genreName,
                fileUrl
        );
    }
}
