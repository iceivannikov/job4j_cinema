package ru.job4j.cinema.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.job4j.cinema.model.Ticket;
import ru.job4j.cinema.repository.TicketRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class TicketServiceImplTest {

    @Mock
    private TicketRepository ticketRepository;

    @InjectMocks
    private TicketServiceImpl ticketService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void whenBuyTicketAndPlaceIsAvailableThenReturnEmpty() {
        Ticket ticket = Ticket.builder()
                .sessionId(1)
                .rowNumber(5)
                .placeNumber(10)
                .userId(1)
                .build();

        when(ticketRepository.isPlaceAvailable(1, 5, 10)).thenReturn(true);

        Optional<Ticket> result = ticketService.buyTicket(ticket);

        assertThat(result).isEmpty();
        verify(ticketRepository).isPlaceAvailable(1, 5, 10);
        verify(ticketRepository, never()).save(any());
    }

    @Test
    void whenBuyTicketAndPlaceIsNotAvailableThenReturnSavedTicket() {
        Ticket ticket = Ticket.builder()
                .sessionId(1)
                .rowNumber(5)
                .placeNumber(10)
                .userId(1)
                .build();

        Ticket savedTicket = Ticket.builder()
                .id(1)
                .sessionId(1)
                .rowNumber(5)
                .placeNumber(10)
                .userId(1)
                .build();

        when(ticketRepository.isPlaceAvailable(1, 5, 10)).thenReturn(false);
        when(ticketRepository.save(ticket)).thenReturn(Optional.of(savedTicket));

        Optional<Ticket> result = ticketService.buyTicket(ticket);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1);
        assertThat(result.get().getSessionId()).isEqualTo(1);
        assertThat(result.get().getRowNumber()).isEqualTo(5);
        assertThat(result.get().getPlaceNumber()).isEqualTo(10);
        assertThat(result.get().getUserId()).isEqualTo(1);

        verify(ticketRepository).isPlaceAvailable(1, 5, 10);
        verify(ticketRepository).save(ticket);
    }

    @Test
    void whenBuyTicketAndPlaceIsNotAvailableButSaveFailsThenReturnEmpty() {
        Ticket ticket = Ticket.builder()
                .sessionId(1)
                .rowNumber(5)
                .placeNumber(10)
                .userId(1)
                .build();

        when(ticketRepository.isPlaceAvailable(1, 5, 10)).thenReturn(false);
        when(ticketRepository.save(ticket)).thenReturn(Optional.empty());

        Optional<Ticket> result = ticketService.buyTicket(ticket);

        assertThat(result).isEmpty();
        verify(ticketRepository).isPlaceAvailable(1, 5, 10);
        verify(ticketRepository).save(ticket);
    }
}