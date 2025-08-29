package ru.job4j.cinema.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;
import ru.job4j.cinema.dto.FilmDto;
import ru.job4j.cinema.dto.FilmSessionDto;
import ru.job4j.cinema.service.FilmService;
import ru.job4j.cinema.service.FilmSessionService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class FilmControllerTest {

    @Mock
    private FilmService filmService;

    @Mock
    private FilmSessionService filmSessionService;

    @Mock
    private Model model;

    @InjectMocks
    private FilmController filmController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void whenGetAllThenReturnFilmsListView() {
        List<FilmDto> films = List.of(createTestFilmDto());

        when(filmService.findAll()).thenReturn(films);

        String result = filmController.getAll(model);

        assertThat(result).isEqualTo("films/list");
        verify(filmService).findAll();
        verify(model).addAttribute("films", films);
    }

    @Test
    void whenGetAllWithEmptyListThenReturnFilmsListView() {
        List<FilmDto> emptyList = List.of();

        when(filmService.findAll()).thenReturn(emptyList);

        String result = filmController.getAll(model);

        assertThat(result).isEqualTo("films/list");
        verify(filmService).findAll();
        verify(model).addAttribute("films", emptyList);
    }

    @Test
    void whenGetFilmSessionsWithValidFilmThenReturnSessionsView() {
        int filmId = 1;
        FilmDto film = createTestFilmDto();
        List<FilmSessionDto> sessions = List.of(createTestFilmSessionDto());

        when(filmService.findById(filmId)).thenReturn(Optional.of(film));
        when(filmSessionService.findByFilmId(filmId)).thenReturn(sessions);

        String result = filmController.getFilmSessions(filmId, model);

        assertThat(result).isEqualTo("films/sessions");
        verify(filmService).findById(filmId);
        verify(filmSessionService).findByFilmId(filmId);
        verify(model).addAttribute("film", film);
        verify(model).addAttribute("sessions", sessions);
    }

    @Test
    void whenGetFilmSessionsWithInvalidFilmThenReturn404() {
        int filmId = 999;

        when(filmService.findById(filmId)).thenReturn(Optional.empty());

        String result = filmController.getFilmSessions(filmId, model);

        assertThat(result).isEqualTo("errors/404");
        verify(filmService).findById(filmId);
        verify(filmSessionService, never()).findByFilmId(any());
        verify(model).addAttribute("message", "Фильм не найден");
    }

    @Test
    void whenGetFilmSessionsWithValidFilmButNoSessionsThenReturnSessionsView() {
        int filmId = 1;
        FilmDto film = createTestFilmDto();
        List<FilmSessionDto> emptySessions = List.of();

        when(filmService.findById(filmId)).thenReturn(Optional.of(film));
        when(filmSessionService.findByFilmId(filmId)).thenReturn(emptySessions);

        String result = filmController.getFilmSessions(filmId, model);

        assertThat(result).isEqualTo("films/sessions");
        verify(model).addAttribute("film", film);
        verify(model).addAttribute("sessions", emptySessions);
    }

    private FilmDto createTestFilmDto() {
        return new FilmDto(
                1,
                "Test Film",
                "Test Description",
                2023,
                18,
                120,
                "Action",
                "/files/1"
        );
    }

    private FilmSessionDto createTestFilmSessionDto() {
        return new FilmSessionDto(
                1,
                "Test Film",
                "Test Description",
                "/files/1",
                "Hall 1",
                LocalDateTime.of(2023, 10, 1, 18, 0),
                LocalDateTime.of(2023, 10, 1, 20, 0),
                500
        );
    }
}