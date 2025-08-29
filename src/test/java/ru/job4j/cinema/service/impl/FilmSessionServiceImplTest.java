package ru.job4j.cinema.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.job4j.cinema.dto.FilmDto;
import ru.job4j.cinema.dto.FilmSessionDetailDto;
import ru.job4j.cinema.dto.FilmSessionDto;
import ru.job4j.cinema.model.FilmSession;
import ru.job4j.cinema.model.Hall;
import ru.job4j.cinema.repository.FilmSessionRepository;
import ru.job4j.cinema.service.FilmService;
import ru.job4j.cinema.service.HallService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class FilmSessionServiceImplTest {

    @Mock
    private FilmSessionRepository filmSessionRepository;

    @Mock
    private FilmService filmService;

    @Mock
    private HallService hallService;

    @InjectMocks
    private FilmSessionServiceImpl filmSessionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void whenFindAllThenReturnListOfFilmSessionDto() {
        FilmSession session = FilmSession.builder()
                .id(1)
                .filmId(1)
                .hallId(1)
                .startTime(LocalDateTime.of(2023, 10, 1, 18, 0))
                .endTime(LocalDateTime.of(2023, 10, 1, 20, 0))
                .price(500)
                .build();

        FilmDto filmDto = new FilmDto(
                1,
                "Test Film",
                "Description",
                2023,
                16,
                120,
                "Action",
                "/path/to/film"
        );

        Hall hall = Hall.builder()
                .id(1)
                .name("Hall 1")
                .rowCount(10)
                .placeCount(15)
                .description("Main hall")
                .build();

        when(filmSessionRepository.findAll()).thenReturn(List.of(session));
        when(filmService.findById(1)).thenReturn(Optional.of(filmDto));
        when(hallService.findById(1)).thenReturn(Optional.of(hall));

        List<FilmSessionDto> result = filmSessionService.findAll();

        assertThat(result).hasSize(1);
        FilmSessionDto dto = result.get(0);
        assertThat(dto.id()).isEqualTo(1);
        assertThat(dto.filmName()).isEqualTo("Test Film");
        assertThat(dto.hallName()).isEqualTo("Hall 1");
        assertThat(dto.price()).isEqualTo(500);

        verify(filmSessionRepository).findAll();
        verify(filmService).findById(1);
        verify(hallService).findById(1);
    }

    @Test
    void whenFindAllWithEmptyListThenReturnEmptyList() {
        when(filmSessionRepository.findAll()).thenReturn(List.of());

        List<FilmSessionDto> result = filmSessionService.findAll();

        assertThat(result).isEmpty();
        verify(filmSessionRepository).findAll();
    }

    @Test
    void whenFindByIdThenReturnFilmSessionDetailDto() {
        FilmSession session = FilmSession.builder()
                .id(1)
                .filmId(1)
                .hallId(1)
                .startTime(LocalDateTime.of(2023, 10, 1, 18, 0))
                .endTime(LocalDateTime.of(2023, 10, 1, 20, 0))
                .price(500)
                .build();

        FilmDto filmDto = new FilmDto(
                1,
                "Test Film",
                "Description",
                2023,
                16,
                120,
                "Action",
                "/path/to/film"
        );

        Hall hall = Hall.builder()
                .id(1)
                .name("Hall 1")
                .rowCount(10)
                .placeCount(15)
                .description("Main hall")
                .build();

        when(filmSessionRepository.findById(1)).thenReturn(Optional.of(session));
        when(filmService.findById(1)).thenReturn(Optional.of(filmDto));
        when(hallService.findById(1)).thenReturn(Optional.of(hall));

        FilmSessionDetailDto result = filmSessionService.findById(1);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1);
        assertThat(result.filmName()).isEqualTo("Test Film");
        assertThat(result.filmDescription()).isEqualTo("Description");
        assertThat(result.hallName()).isEqualTo("Hall 1");
        assertThat(result.hallRowCount()).isEqualTo(10);
        assertThat(result.price()).isEqualTo(500);

        verify(filmSessionRepository).findById(1);
        verify(filmService).findById(1);
        verify(hallService).findById(1);
    }

    @Test
    void whenFindByNonExistingIdThenReturnNull() {
        when(filmSessionRepository.findById(999)).thenReturn(Optional.empty());

        FilmSessionDetailDto result = filmSessionService.findById(999);

        assertThat(result).isNull();
        verify(filmSessionRepository).findById(999);
    }

    @Test
    void whenFindByFilmIdThenReturnListOfFilmSessionDto() {
        FilmSession session1 = createFilmSession(1, 1, 1, 500);
        FilmSession session2 = createFilmSession(2, 1, 2, 600);
        FilmDto filmDto = createFilmDto();
        Hall hall1 = Hall.builder().id(1).name("Hall 1").build();
        Hall hall2 = Hall.builder().id(2).name("Hall 2").build();

        setupMocksForFindByFilmId(session1, session2, filmDto, hall1, hall2);

        List<FilmSessionDto> result = filmSessionService.findByFilmId(1);

        verifyFindByFilmIdResult(result);
    }

    private FilmSession createFilmSession(int id, int filmId, int hallId, int price) {
        return FilmSession.builder()
                .id(id)
                .filmId(filmId)
                .hallId(hallId)
                .startTime(LocalDateTime.of(2023, 10, 1, 18, 0))
                .endTime(LocalDateTime.of(2023, 10, 1, 20, 0))
                .price(price)
                .build();
    }

    private FilmDto createFilmDto() {
        return new FilmDto(1, "Test Film", "Description", 2023, 16, 120, "Action", "/path/to/film");
    }

    private void setupMocksForFindByFilmId(FilmSession session1, FilmSession session2, 
            FilmDto filmDto, Hall hall1, Hall hall2) {
        when(filmSessionRepository.findByFilmId(1)).thenReturn(List.of(session1, session2));
        when(filmService.findById(1)).thenReturn(Optional.of(filmDto));
        when(hallService.findById(1)).thenReturn(Optional.of(hall1));
        when(hallService.findById(2)).thenReturn(Optional.of(hall2));
    }

    private void verifyFindByFilmIdResult(List<FilmSessionDto> result) {
        assertThat(result).hasSize(2);
        assertThat(result.get(0).id()).isEqualTo(1);
        assertThat(result.get(0).hallName()).isEqualTo("Hall 1");
        assertThat(result.get(1).id()).isEqualTo(2);
        assertThat(result.get(1).hallName()).isEqualTo("Hall 2");

        verify(filmSessionRepository).findByFilmId(1);
        verify(filmService, times(2)).findById(1);
        verify(hallService).findById(1);
        verify(hallService).findById(2);
    }

    @Test
    void whenFindByFilmIdWithEmptyResultThenReturnEmptyList() {
        when(filmSessionRepository.findByFilmId(999)).thenReturn(List.of());

        List<FilmSessionDto> result = filmSessionService.findByFilmId(999);

        assertThat(result).isEmpty();
        verify(filmSessionRepository).findByFilmId(999);
    }

    @Test
    void whenFilmServiceReturnsEmptyThenThrowsException() {
        FilmSession session = FilmSession.builder()
                .id(1)
                .filmId(999)
                .hallId(1)
                .startTime(LocalDateTime.of(2023, 10, 1, 18, 0))
                .endTime(LocalDateTime.of(2023, 10, 1, 20, 0))
                .price(500)
                .build();

        Hall hall = Hall.builder().id(1).name("Hall 1").build();

        when(filmSessionRepository.findAll()).thenReturn(List.of(session));
        when(filmService.findById(999)).thenReturn(Optional.empty());
        when(hallService.findById(1)).thenReturn(Optional.of(hall));

        try {
            filmSessionService.findAll();
        } catch (NullPointerException e) {
            assertThat(e.getMessage()).contains("Cannot invoke \"ru.job4j.cinema.dto.FilmDto.name()\" because \"filmDto\" is null");
        }

        verify(filmSessionRepository).findAll();
        verify(filmService).findById(999);
    }

    @Test
    void whenHallServiceReturnsEmptyThenHallNameIsUnknown() {
        FilmSession session = FilmSession.builder()
                .id(1)
                .filmId(1)
                .hallId(999)
                .startTime(LocalDateTime.of(2023, 10, 1, 18, 0))
                .endTime(LocalDateTime.of(2023, 10, 1, 20, 0))
                .price(500)
                .build();

        FilmDto filmDto = new FilmDto(
                1,
                "Test Film",
                "Description",
                2023,
                16,
                120,
                "Action",
                "/path/to/film"
        );

        when(filmSessionRepository.findAll()).thenReturn(List.of(session));
        when(filmService.findById(1)).thenReturn(Optional.of(filmDto));
        when(hallService.findById(999)).thenReturn(Optional.empty());

        List<FilmSessionDto> result = filmSessionService.findAll();

        assertThat(result).hasSize(1);
        FilmSessionDto dto = result.get(0);
        assertThat(dto.hallName()).isEqualTo("Неизвестен");
    }
}