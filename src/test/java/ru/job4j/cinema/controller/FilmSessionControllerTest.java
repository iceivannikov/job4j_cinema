package ru.job4j.cinema.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;
import ru.job4j.cinema.dto.FilmSessionDto;
import ru.job4j.cinema.service.FilmSessionService;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class FilmSessionControllerTest {

    @Mock
    private FilmSessionService filmSessionService;

    @Mock
    private Model model;

    @InjectMocks
    private FilmSessionController filmSessionController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void whenGetAllThenReturnSessionsListView() {
        List<FilmSessionDto> sessions = List.of(createTestFilmSessionDto());

        when(filmSessionService.findAll()).thenReturn(sessions);

        String result = filmSessionController.getAll(model);

        assertThat(result).isEqualTo("sessions/list");
        verify(filmSessionService).findAll();
        verify(model).addAttribute("sessions", sessions);
    }

    @Test
    void whenGetAllWithEmptyListThenReturnSessionsListView() {
        List<FilmSessionDto> emptySessions = List.of();

        when(filmSessionService.findAll()).thenReturn(emptySessions);

        String result = filmSessionController.getAll(model);

        assertThat(result).isEqualTo("sessions/list");
        verify(filmSessionService).findAll();
        verify(model).addAttribute("sessions", emptySessions);
    }

    @Test
    void whenGetAllWithMultipleSessionsThenReturnAllSessions() {
        FilmSessionDto session1 = createTestFilmSessionDto();
        FilmSessionDto session2 = new FilmSessionDto(
                2,
                "Another Film",
                "Another Description",
                "/files/2",
                "Hall 2",
                LocalDateTime.of(2023, 10, 2, 19, 0),
                LocalDateTime.of(2023, 10, 2, 21, 0),
                600
        );
        List<FilmSessionDto> sessions = List.of(session1, session2);

        when(filmSessionService.findAll()).thenReturn(sessions);

        String result = filmSessionController.getAll(model);

        assertThat(result).isEqualTo("sessions/list");
        verify(filmSessionService).findAll();
        verify(model).addAttribute("sessions", sessions);
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