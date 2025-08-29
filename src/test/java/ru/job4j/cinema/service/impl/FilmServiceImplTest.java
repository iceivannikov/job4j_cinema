package ru.job4j.cinema.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.job4j.cinema.dto.FilmDto;
import ru.job4j.cinema.model.Film;
import ru.job4j.cinema.model.Genre;
import ru.job4j.cinema.repository.FilmRepository;
import ru.job4j.cinema.service.GenreService;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class FilmServiceImplTest {

    @Mock
    private FilmRepository filmRepository;

    @Mock
    private GenreService genreService;

    @InjectMocks
    private FilmServiceImpl filmService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void whenFindAllThenReturnListOfFilmDto() {
        Film film = createTestFilm();
        Genre genre = createTestGenre();

        when(filmRepository.findAll()).thenReturn(List.of(film));
        when(genreService.findById(1)).thenReturn(Optional.of(genre));

        List<FilmDto> result = filmService.findAll();

        assertThat(result).hasSize(1);
        FilmDto dto = result.get(0);
        assertFilmDto(dto, "Action", "/files/1");

        verify(filmRepository).findAll();
        verify(genreService).findById(1);
    }

    @Test
    void whenFindAllWithEmptyListThenReturnEmptyList() {
        when(filmRepository.findAll()).thenReturn(List.of());

        List<FilmDto> result = filmService.findAll();

        assertThat(result).isEmpty();
        verify(filmRepository).findAll();
    }

    @Test
    void whenFindAllWithMultipleFilmsThenReturnAllFilmDtos() {
        Film film1 = createTestFilm();
        Film film2 = Film.builder()
                .id(2)
                .name("Test Film 2")
                .description("Description 2")
                .year(2024)
                .minimalAge(12)
                .durationInMinutes(90)
                .genreId(2)
                .fileId(2)
                .build();

        Genre genre1 = createTestGenre();
        Genre genre2 = Genre.builder().id(2).name("Comedy").build();

        when(filmRepository.findAll()).thenReturn(List.of(film1, film2));
        when(genreService.findById(1)).thenReturn(Optional.of(genre1));
        when(genreService.findById(2)).thenReturn(Optional.of(genre2));

        List<FilmDto> result = filmService.findAll();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).name()).isEqualTo("Test Film");
        assertThat(result.get(0).genre()).isEqualTo("Action");
        assertThat(result.get(1).name()).isEqualTo("Test Film 2");
        assertThat(result.get(1).genre()).isEqualTo("Comedy");
    }

    @Test
    void whenFindByExistingIdThenReturnFilmDto() {
        Film film = createTestFilm();
        Genre genre = createTestGenre();

        when(filmRepository.findById(1)).thenReturn(Optional.of(film));
        when(genreService.findById(1)).thenReturn(Optional.of(genre));

        Optional<FilmDto> result = filmService.findById(1);

        assertThat(result).isPresent();
        FilmDto dto = result.get();
        assertFilmDto(dto, "Action", "/files/1");

        verify(filmRepository).findById(1);
        verify(genreService).findById(1);
    }

    @Test
    void whenFindByNonExistingIdThenReturnEmpty() {
        when(filmRepository.findById(999)).thenReturn(Optional.empty());

        Optional<FilmDto> result = filmService.findById(999);

        assertThat(result).isEmpty();
        verify(filmRepository).findById(999);
    }

    @Test
    void whenGenreServiceReturnsEmptyThenGenreIsUnknown() {
        Film film = createTestFilm();

        when(filmRepository.findById(1)).thenReturn(Optional.of(film));
        when(genreService.findById(1)).thenReturn(Optional.empty());

        Optional<FilmDto> result = filmService.findById(1);

        assertThat(result).isPresent();
        FilmDto dto = result.get();
        assertFilmDto(dto, "Неизвестно", "/files/1");

        verify(filmRepository).findById(1);
        verify(genreService).findById(1);
    }

    @Test
    void whenMapToFilmDtoThenCorrectFileUrlIsGenerated() {
        Film film = Film.builder()
                .id(1)
                .name("Test Film")
                .description("Description")
                .year(2023)
                .minimalAge(18)
                .durationInMinutes(120)
                .genreId(1)
                .fileId(42)
                .build();

        Genre genre = createTestGenre();

        when(filmRepository.findById(1)).thenReturn(Optional.of(film));
        when(genreService.findById(1)).thenReturn(Optional.of(genre));

        Optional<FilmDto> result = filmService.findById(1);

        assertThat(result).isPresent();
        assertThat(result.get().filePath()).isEqualTo("/files/42");
    }

    private Film createTestFilm() {
        return Film.builder()
                .id(1)
                .name("Test Film")
                .description("Test Description")
                .year(2023)
                .minimalAge(18)
                .durationInMinutes(120)
                .genreId(1)
                .fileId(1)
                .build();
    }

    private Genre createTestGenre() {
        return Genre.builder()
                .id(1)
                .name("Action")
                .build();
    }

    private void assertFilmDto(FilmDto dto, String expectedGenre, String expectedFilePath) {
        assertThat(dto.id()).isEqualTo(1);
        assertThat(dto.name()).isEqualTo("Test Film");
        assertThat(dto.description()).isEqualTo("Test Description");
        assertThat(dto.year()).isEqualTo(2023);
        assertThat(dto.minimalAge()).isEqualTo(18);
        assertThat(dto.durationInMinutes()).isEqualTo(120);
        assertThat(dto.genre()).isEqualTo(expectedGenre);
        assertThat(dto.filePath()).isEqualTo(expectedFilePath);
    }
}