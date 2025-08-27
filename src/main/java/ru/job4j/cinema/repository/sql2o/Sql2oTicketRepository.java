package ru.job4j.cinema.repository.sql2o;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import org.sql2o.Connection;
import org.sql2o.Query;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;
import ru.job4j.cinema.model.Ticket;
import ru.job4j.cinema.repository.TicketRepository;

import java.util.Optional;

@AllArgsConstructor
@Repository
public class Sql2oTicketRepository implements TicketRepository {

    private final Sql2o sql2o;

    @Override
    public Optional<Ticket> save(Ticket ticket) {
        try (Connection connection = sql2o.open()) {
            String sql = """
                    INSERT INTO tickets (session_id, "row_number", place_number, user_id)
                    VALUES (:sessionId, :rowNumber, :placeNumber, :userId)
                    """;

            Query query = connection.createQuery(sql, true);
            Integer generatedId = query
                    .addParameter("sessionId", ticket.getSessionId())
                    .addParameter("rowNumber", ticket.getRowNumber())
                    .addParameter("placeNumber", ticket.getPlaceNumber())
                    .addParameter("userId", ticket.getUserId())
                    .executeUpdate()
                    .getKey(Integer.class);

            Ticket newTicket = Ticket.builder()
                    .id(generatedId)
                    .sessionId(ticket.getSessionId())
                    .rowNumber(ticket.getRowNumber())
                    .placeNumber(ticket.getPlaceNumber())
                    .userId(ticket.getUserId())
                    .build();
            return Optional.of(newTicket);
        } catch (Sql2oException e) {
            return Optional.empty();
        }
    }

    @Override
    public boolean isPlaceAvailable(Integer sessionId, Integer rowNumber, Integer placeNumber) {
        try (Connection connection = sql2o.open()) {
            String sql = """
                    SELECT COUNT(*) FROM tickets
                    WHERE session_id = :sessionId
                    AND "row_number" = :rowNumber
                    AND place_number = :placeNumber
                    """;
            Integer count = connection.createQuery(sql)
                    .addParameter("sessionId", sessionId)
                    .addParameter("rowNumber", rowNumber)
                    .addParameter("placeNumber", placeNumber)
                    .executeScalar(Integer.class);
            return count > 0;
        }
    }
}
