package ru.job4j.cinema.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.job4j.cinema.model.Ticket;
import ru.job4j.cinema.repository.TicketRepository;
import ru.job4j.cinema.service.TicketService;

import java.util.Optional;

@Service
@AllArgsConstructor
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;

    @Override
    public Optional<Ticket> buyTicket(Ticket ticket) {
        if (ticketRepository.isPlaceAvailable(
                ticket.getSessionId(),
                ticket.getRowNumber(),
                ticket.getPlaceNumber())) {
            return Optional.empty();
        }
        return ticketRepository.save(ticket);
    }
}
