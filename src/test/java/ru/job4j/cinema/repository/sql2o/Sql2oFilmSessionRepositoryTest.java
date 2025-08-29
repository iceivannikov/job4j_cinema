package ru.job4j.cinema.repository.sql2o;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sql2o.Sql2o;
import ru.job4j.cinema.configuration.DatasourceConfiguration;
import ru.job4j.cinema.model.FilmSession;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

class Sql2oFilmSessionRepositoryTest {

    private static Sql2oFilmSessionRepository sql2oFilmSessionRepository;
    private static Sql2o sql2o;

    @BeforeAll
    static void initRepositories() throws IOException {
        var properties = new Properties();
        try (var inputStream = Sql2oFilmSessionRepositoryTest.class.getClassLoader()
                .getResourceAsStream("connection.properties")) {
            properties.load(inputStream);
        }
        var url = properties.getProperty("datasource.url");
        var username = properties.getProperty("datasource.username");
        var password = properties.getProperty("datasource.password");

        var configuration = new DatasourceConfiguration();
        var dataSource = configuration.connectionPool(url, username, password);
        sql2o = configuration.databaseClient(dataSource);
        sql2oFilmSessionRepository = new Sql2oFilmSessionRepository(sql2o);
    }

    @BeforeEach
    void setUp() {
        try (var connection = sql2o.open()) {
            connection.createQuery("INSERT INTO files (id, name, path) VALUES (1, 'test.jpg', '/test/path1')").executeUpdate();

            connection.createQuery("INSERT INTO genres (id, name) VALUES (1, 'Action')").executeUpdate();

            for (int i = 1; i <= 5; i++) {
                connection.createQuery("INSERT INTO halls (id, name, row_count, place_count, description) VALUES (:id, :name, 10, 20, 'Test hall')")
                    .addParameter("id", i)
                    .addParameter("name", "Hall " + i)
                    .executeUpdate();
            }

            for (int i = 1; i <= 10; i++) {
                connection.createQuery("INSERT INTO films (id, name, description, \"year\", genre_id, minimal_age, duration_in_minutes, file_id) VALUES (:id, :name, 'Test description', 2023, 1, 16, 120, 1)")
                    .addParameter("id", i)
                    .addParameter("name", "Test Film " + i)
                    .executeUpdate();
            }
        }
    }

    @AfterEach
    void tearDown() {
        try (var connection = sql2o.open()) {
            connection.createQuery("DELETE FROM film_sessions").executeUpdate();
            connection.createQuery("DELETE FROM films").executeUpdate();
            connection.createQuery("DELETE FROM genres").executeUpdate();
            connection.createQuery("DELETE FROM files").executeUpdate();
            connection.createQuery("DELETE FROM halls").executeUpdate();
        }
    }

    @Test
    void whenFindAllThenReturnAllFilmSessions() {
        FilmSession session1 = FilmSession.builder()
                .filmId(1)
                .hallId(1)
                .startTime(LocalDateTime.of(2024, 12, 25, 14, 30))
                .endTime(LocalDateTime.of(2024, 12, 25, 16, 30))
                .price(500)
                .build();

        FilmSession session2 = FilmSession.builder()
                .filmId(2)
                .hallId(2)
                .startTime(LocalDateTime.of(2024, 12, 25, 18, 0))
                .endTime(LocalDateTime.of(2024, 12, 25, 20, 15))
                .price(750)
                .build();

        try (var connection = sql2o.open()) {
            connection.createQuery(
                    "INSERT INTO film_sessions (film_id, halls_id, start_time, end_time, price) "
                            + "VALUES (:filmId, :hallId, :startTime, :endTime, :price)")
                    .addParameter("filmId", session1.getFilmId())
                    .addParameter("hallId", session1.getHallId())
                    .addParameter("startTime", session1.getStartTime())
                    .addParameter("endTime", session1.getEndTime())
                    .addParameter("price", session1.getPrice())
                    .executeUpdate();

            connection.createQuery(
                    "INSERT INTO film_sessions (film_id, halls_id, start_time, end_time, price) "
                            + "VALUES (:filmId, :hallId, :startTime, :endTime, :price)")
                    .addParameter("filmId", session2.getFilmId())
                    .addParameter("hallId", session2.getHallId())
                    .addParameter("startTime", session2.getStartTime())
                    .addParameter("endTime", session2.getEndTime())
                    .addParameter("price", session2.getPrice())
                    .executeUpdate();
        }

        List<FilmSession> sessions = sql2oFilmSessionRepository.findAll();

        assertThat(sessions).hasSize(2);
        assertThat(sessions.get(0).getPrice()).isEqualTo(500);
        assertThat(sessions.get(1).getPrice()).isEqualTo(750);
    }

    @Test
    void whenFindAllWithNoSessionsThenReturnEmptyList() {
        List<FilmSession> sessions = sql2oFilmSessionRepository.findAll();

        assertThat(sessions).isEmpty();
    }

    @Test
    void whenFindByExistingIdThenReturnFilmSession() {
        FilmSession session = FilmSession.builder()
                .filmId(3)
                .hallId(1)
                .startTime(LocalDateTime.of(2024, 12, 26, 20, 0))
                .endTime(LocalDateTime.of(2024, 12, 26, 22, 30))
                .price(850)
                .build();

        try (var connection = sql2o.open()) {
            Integer generatedId = connection.createQuery(
                    "INSERT INTO film_sessions (film_id, halls_id, start_time, end_time, price) "
                            + "VALUES (:filmId, :hallId, :startTime, :endTime, :price)", true)
                    .addParameter("filmId", session.getFilmId())
                    .addParameter("hallId", session.getHallId())
                    .addParameter("startTime", session.getStartTime())
                    .addParameter("endTime", session.getEndTime())
                    .addParameter("price", session.getPrice())
                    .executeUpdate()
                    .getKey(Integer.class);
            session.setId(generatedId);
        }

        Optional<FilmSession> foundSession = sql2oFilmSessionRepository.findById(session.getId());

        assertThat(foundSession).isPresent();
        assertThat(foundSession.get().getId()).isEqualTo(session.getId());
        assertThat(foundSession.get().getFilmId()).isEqualTo(3);
        assertThat(foundSession.get().getHallId()).isEqualTo(1);
        assertThat(foundSession.get().getStartTime()).isEqualTo(LocalDateTime.of(2024, 12, 26, 20, 0));
        assertThat(foundSession.get().getEndTime()).isEqualTo(LocalDateTime.of(2024, 12, 26, 22, 30));
        assertThat(foundSession.get().getPrice()).isEqualTo(850);
    }

    @Test
    void whenFindByNonExistingIdThenReturnEmpty() {
        Optional<FilmSession> foundSession = sql2oFilmSessionRepository.findById(999);

        assertThat(foundSession).isEmpty();
    }

    @Test
    void whenFindByFilmIdThenReturnAllSessionsForFilm() {
        FilmSession session1 = FilmSession.builder()
                .filmId(5)
                .hallId(1)
                .startTime(LocalDateTime.of(2024, 12, 27, 14, 0))
                .endTime(LocalDateTime.of(2024, 12, 27, 16, 0))
                .price(600)
                .build();

        FilmSession session2 = FilmSession.builder()
                .filmId(5)
                .hallId(2)
                .startTime(LocalDateTime.of(2024, 12, 27, 18, 30))
                .endTime(LocalDateTime.of(2024, 12, 27, 20, 30))
                .price(700)
                .build();

        insertFilmSession(session1);
        insertFilmSession(session2);

        List<FilmSession> sessions = sql2oFilmSessionRepository.findByFilmId(5);

        assertThat(sessions).hasSize(2);
        assertThat(sessions.get(0).getPrice()).isEqualTo(600);
        assertThat(sessions.get(1).getPrice()).isEqualTo(700);
    }

    @Test
    void whenFindByFilmIdForDifferentFilmsThenReturnCorrectSessions() {
        FilmSession session1 = FilmSession.builder()
                .filmId(5)
                .hallId(1)
                .startTime(LocalDateTime.of(2024, 12, 27, 14, 0))
                .endTime(LocalDateTime.of(2024, 12, 27, 16, 0))
                .price(600)
                .build();

        FilmSession session2 = FilmSession.builder()
                .filmId(6)
                .hallId(1)
                .startTime(LocalDateTime.of(2024, 12, 27, 21, 0))
                .endTime(LocalDateTime.of(2024, 12, 27, 23, 0))
                .price(800)
                .build();

        insertFilmSession(session1);
        insertFilmSession(session2);

        List<FilmSession> sessionsForFilm5 = sql2oFilmSessionRepository.findByFilmId(5);
        List<FilmSession> sessionsForFilm6 = sql2oFilmSessionRepository.findByFilmId(6);

        assertThat(sessionsForFilm5).hasSize(1);
        assertThat(sessionsForFilm5.get(0).getPrice()).isEqualTo(600);

        assertThat(sessionsForFilm6).hasSize(1);
        assertThat(sessionsForFilm6.get(0).getPrice()).isEqualTo(800);
    }

    @Test
    void whenFindByNonExistingFilmIdThenReturnEmptyList() {
        List<FilmSession> sessions = sql2oFilmSessionRepository.findByFilmId(999);

        assertThat(sessions).isEmpty();
    }

    private void insertFilmSession(FilmSession session) {
        try (var connection = sql2o.open()) {
            connection.createQuery(
                    "INSERT INTO film_sessions (film_id, halls_id, start_time, end_time, price) "
                            + "VALUES (:filmId, :hallId, :startTime, :endTime, :price)")
                    .addParameter("filmId", session.getFilmId())
                    .addParameter("hallId", session.getHallId())
                    .addParameter("startTime", session.getStartTime())
                    .addParameter("endTime", session.getEndTime())
                    .addParameter("price", session.getPrice())
                    .executeUpdate();
        }
    }
}