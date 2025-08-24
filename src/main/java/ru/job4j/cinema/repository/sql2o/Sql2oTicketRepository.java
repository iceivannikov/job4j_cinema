package ru.job4j.cinema.repository.sql2o;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import org.sql2o.Connection;
import org.sql2o.Query;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;
import ru.job4j.cinema.model.Ticket;
import ru.job4j.cinema.repository.TicketRepository;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Repository
public class Sql2oTicketRepository implements TicketRepository {

    private final Sql2o sql2o;


    @Override
    public Optional<Ticket> findById(Integer id) {
        try (Connection connection = sql2o.open()) {
            Query query = connection.createQuery("SELECT * FROM tickets WHERE id = :id");

            Ticket ticket = query
                    .addParameter("id", id)
                    .setColumnMappings(Ticket.COLUMN_MAPPING)
                    .executeAndFetchFirst(Ticket.class);

            return Optional.ofNullable(ticket);
        }
    }

    @Override
    public List<Ticket> findBySessionId(Integer sessionId) {
        try (Connection connection = sql2o.open()) {
            Query query = connection.createQuery("SELECT * FROM tickets WHERE session_id = :sessionId");

            return query
                    .addParameter("sessionId", sessionId)
                    .setColumnMappings(Ticket.COLUMN_MAPPING)
                    .executeAndFetch(Ticket.class);
        }
    }

    @Override
    public List<Ticket> findByUserId(Integer userId) {
        try (Connection connection = sql2o.open()) {
            Query query = connection.createQuery("SELECT * FROM tickets WHERE user_id = :userId");

            return query
                    .addParameter("userId", userId)
                    .setColumnMappings(Ticket.COLUMN_MAPPING)
                    .executeAndFetch(Ticket.class);
        }
    }

    @Override
    public boolean isPlaceAvailable(Integer sessionId, Integer rowNumber, Integer placeNumber) {
        try (Connection connection = sql2o.open()) {
            String sql = """
                    SELECT COUNT(*)
                    FROM tickets
                    WHERE session_id = :sessionId
                      AND row_number = :rowNumber
                      AND place_number = :placeNumber
                    """;
            Query query = connection.createQuery(sql);

            Integer count = query
                    .addParameter("sessionId", sessionId)
                    .addParameter("rowNumber", rowNumber)
                    .addParameter("placeNumber", placeNumber)
                    .executeScalar(Integer.class);

            return count == 0;
        }
    }

    @Override
    public Optional<Ticket> save(Ticket ticket) {
        try (Connection connection = sql2o.open()) {
            String sql = """
                    INSERT INTO tickets (session_id, row_number, place_number, user_id)
                    VALUES (:sessionId, :rowNumber, :placeNumber, :userId)
                    """;

            Query query = connection.createQuery(sql, true);

            try {
                Integer generatedId = query
                        .addParameter("sessionId", ticket.getSessionId())
                        .addParameter("rowNumber", ticket.getRowNumber())
                        .addParameter("placeNumber", ticket.getPlaceNumber())
                        .addParameter("userId", ticket.getUserId())
                        .executeUpdate()
                        .getKey(Integer.class);

                ticket.setId(generatedId);
                return Optional.of(ticket);
            } catch (Sql2oException e) {
                if (e.getMessage().contains("unique") || e.getMessage().contains("duplicate")) {
                    return Optional.empty();
                }
                throw e;
            }
        }
    }

    @Override
    public List<Ticket> findAvailableSeats(Integer sessionId) {
        try (Connection connection = sql2o.open()) {
            String sql = """
                    SELECT h.row_count, h.place_count 
                    FROM film_sessions fs 
                    JOIN halls h ON fs.halls_id = h.id 
                    WHERE fs.id = :sessionId
                    """;
            
            Query query = connection.createQuery(sql);
            
            return query
                    .addParameter("sessionId", sessionId)
                    .executeAndFetch(Ticket.class);
        }
    }

    @Override
    public int countSoldTickets(Integer sessionId) {
        try (Connection connection = sql2o.open()) {
            Query query = connection.createQuery("SELECT COUNT(*) FROM tickets WHERE session_id = :sessionId");

            Integer count = query
                    .addParameter("sessionId", sessionId)
                    .executeScalar(Integer.class);

            return count != null ? count : 0;
        }
    }

    @Override
    public boolean deleteById(Integer id) {
        try (Connection connection = sql2o.open()) {
            Query query = connection.createQuery("DELETE FROM tickets WHERE id = :id");

            int affectedRows = query
                    .addParameter("id", id)
                    .executeUpdate()
                    .getResult();

            return affectedRows == 1;
        }
    }
}
