package ru.job4j.cinema.repository.sql2o;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import org.sql2o.Connection;
import org.sql2o.Query;
import org.sql2o.Sql2o;
import ru.job4j.cinema.model.FilmSession;
import ru.job4j.cinema.repository.FilmSessionRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Repository
public class Sql2oFilmSessionRepository implements FilmSessionRepository {

    private final Sql2o sql2o;

    @Override
    public List<FilmSession> findAll() {
        try (Connection connection = sql2o.open()) {
            Query query = connection.createQuery("SELECT * FROM film_sessions");

            return query
                    .setColumnMappings(FilmSession.COLUMN_MAPPING)
                    .executeAndFetch(FilmSession.class);
        }
    }

    @Override
    public Optional<FilmSession> findById(Integer id) {
        try (Connection connection = sql2o.open()) {
            Query query = connection.createQuery("SELECT * FROM film_sessions WHERE id = :id");

            FilmSession filmSession = query
                    .addParameter("id", id)
                    .setColumnMappings(FilmSession.COLUMN_MAPPING)
                    .executeAndFetchFirst(FilmSession.class);

            return Optional.ofNullable(filmSession);
        }
    }

    @Override
    public List<FilmSession> findByFilmId(Integer filmId) {
        try (Connection connection = sql2o.open()) {
            Query query = connection.createQuery("SELECT * FROM film_sessions WHERE film_id = :filmId");

            return query
                    .addParameter("filmId", filmId)
                    .setColumnMappings(FilmSession.COLUMN_MAPPING)
                    .executeAndFetch(FilmSession.class);
        }
    }

    @Override
    public List<FilmSession> findByHallId(Integer hallId) {
        try (Connection connection = sql2o.open()) {
            Query query = connection.createQuery("SELECT * FROM film_sessions WHERE halls_id = :hallId");

            return query
                    .addParameter("hallId", hallId)
                    .setColumnMappings(FilmSession.COLUMN_MAPPING)
                    .executeAndFetch(FilmSession.class);
        }
    }

    @Override
    public List<FilmSession> findByTimeRange(LocalDateTime start, LocalDateTime end) {
        try (Connection connection = sql2o.open()) {
            Query query = connection.createQuery(
                    "SELECT * FROM film_sessions WHERE start_time >= :start AND end_time <= :end");

            return query
                    .addParameter("start", start)
                    .addParameter("end", end)
                    .setColumnMappings(FilmSession.COLUMN_MAPPING)
                    .executeAndFetch(FilmSession.class);
        }
    }

    @Override
    public FilmSession save(FilmSession filmSession) {
        try (Connection connection = sql2o.open()) {
            String sql = """
                    INSERT INTO film_sessions (film_id, halls_id, start_time, end_time, price)
                    VALUES (:filmId, :hallId, :startTime, :endTime, :price)
                    """;

            Query query = connection.createQuery(sql, true);

            Integer generatedId = query
                    .addParameter("filmId", filmSession.getFilmId())
                    .addParameter("hallId", filmSession.getHallId())
                    .addParameter("startTime", filmSession.getStartTime())
                    .addParameter("endTime", filmSession.getEndTime())
                    .addParameter("price", filmSession.getPrice())
                    .executeUpdate()
                    .getKey(Integer.class);

            filmSession.setId(generatedId);
            return filmSession;
        }
    }

    @Override
    public boolean update(FilmSession filmSession) {
        try (Connection connection = sql2o.open()) {
            String sql = """
                    UPDATE film_sessions
                    SET film_id = :filmId,
                        halls_id = :hallId,
                        start_time = :startTime,
                        end_time = :endTime,
                        price = :price
                    WHERE id = :id
                    """;

            Query query = connection.createQuery(sql);

            int affectedRows = query
                    .addParameter("id", filmSession.getId())
                    .addParameter("filmId", filmSession.getFilmId())
                    .addParameter("hallId", filmSession.getHallId())
                    .addParameter("startTime", filmSession.getStartTime())
                    .addParameter("endTime", filmSession.getEndTime())
                    .addParameter("price", filmSession.getPrice())
                    .executeUpdate()
                    .getResult();

            return affectedRows == 1;
        }
    }

    @Override
    public boolean deleteById(Integer id) {
        try (Connection connection = sql2o.open()) {
            Query query = connection.createQuery("DELETE FROM film_sessions WHERE id = :id");

            int affectedRows = query
                    .addParameter("id", id)
                    .executeUpdate()
                    .getResult();

            return affectedRows == 1;
        }
    }

    @Override
    public List<FilmSession> findActiveSessionsByFilmId(Integer filmId) {
        try (Connection connection = sql2o.open()) {
            Query query = connection.createQuery(
                    "SELECT * FROM film_sessions WHERE film_id = :filmId AND start_time >= NOW()");

            return query
                    .addParameter("filmId", filmId)
                    .setColumnMappings(FilmSession.COLUMN_MAPPING)
                    .executeAndFetch(FilmSession.class);
        }
    }

    @Override
    public List<FilmSession> findTodaysSessions() {
        try (Connection connection = sql2o.open()) {
            Query query = connection.createQuery(
                    "SELECT * FROM film_sessions WHERE DATE(start_time) = CURRENT_DATE");

            return query
                    .setColumnMappings(FilmSession.COLUMN_MAPPING)
                    .executeAndFetch(FilmSession.class);
        }
    }
}
