package ru.job4j.cinema.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.job4j.cinema.model.Hall;
import ru.job4j.cinema.repository.HallRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class HallServiceImplTest {

    @Mock
    private HallRepository hallRepository;

    @InjectMocks
    private HallServiceImpl hallService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void whenFindByExistingIdThenReturnHall() {
        Hall hall = Hall.builder()
                .id(1)
                .name("Main Hall")
                .rowCount(10)
                .placeCount(15)
                .description("The largest hall")
                .build();

        when(hallRepository.findById(1)).thenReturn(Optional.of(hall));

        Optional<Hall> result = hallService.findById(1);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1);
        assertThat(result.get().getName()).isEqualTo("Main Hall");
        assertThat(result.get().getRowCount()).isEqualTo(10);
        assertThat(result.get().getPlaceCount()).isEqualTo(15);
        assertThat(result.get().getDescription()).isEqualTo("The largest hall");

        verify(hallRepository).findById(1);
    }

    @Test
    void whenFindByNonExistingIdThenReturnEmpty() {
        when(hallRepository.findById(999)).thenReturn(Optional.empty());

        Optional<Hall> result = hallService.findById(999);

        assertThat(result).isEmpty();
        verify(hallRepository).findById(999);
    }
}