package ru.job4j.cinema.repository;

import ru.job4j.cinema.model.Ticket;

import java.util.List;
import java.util.Optional;

public interface TicketRepository {

    Optional<Ticket> findById(Integer id);
    List<Ticket> findBySessionId(Integer sessionId);
    List<Ticket> findByUserId(Integer userId);
    boolean isPlaceAvailable(Integer sessionId, Integer rowNumber, Integer placeNumber);
    List<Ticket> findAvailableSeats(Integer sessionId);
    int countSoldTickets(Integer sessionId);
    Optional<Ticket> save(Ticket ticket);
    boolean deleteById(Integer id);
}
