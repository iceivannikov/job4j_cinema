package ru.job4j.cinema.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.job4j.cinema.model.Genre;
import ru.job4j.cinema.repository.GenreRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class GenreServiceImplTest {

    @Mock
    private GenreRepository genreRepository;

    @InjectMocks
    private GenreServiceImpl genreService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void whenFindByExistingIdThenReturnGenre() {
        Genre genre = Genre.builder()
                .id(1)
                .name("Action")
                .build();

        when(genreRepository.findById(1)).thenReturn(Optional.of(genre));

        Optional<Genre> result = genreService.findById(1);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1);
        assertThat(result.get().getName()).isEqualTo("Action");

        verify(genreRepository).findById(1);
    }

    @Test
    void whenFindByNonExistingIdThenReturnEmpty() {
        when(genreRepository.findById(999)).thenReturn(Optional.empty());

        Optional<Genre> result = genreService.findById(999);

        assertThat(result).isEmpty();
        verify(genreRepository).findById(999);
    }
}