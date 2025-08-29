package ru.job4j.cinema.repository.sql2o;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sql2o.Sql2o;
import ru.job4j.cinema.configuration.DatasourceConfiguration;
import ru.job4j.cinema.model.Ticket;

import java.io.IOException;
import java.util.Optional;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

class Sql2oTicketRepositoryTest {

    private static Sql2oTicketRepository sql2oTicketRepository;
    private static Sql2o sql2o;

    @BeforeAll
    static void initRepositories() throws IOException {
        var properties = new Properties();
        try (var inputStream = Sql2oTicketRepositoryTest.class.getClassLoader()
                .getResourceAsStream("connection.properties")) {
            properties.load(inputStream);
        }
        var url = properties.getProperty("datasource.url");
        var username = properties.getProperty("datasource.username");
        var password = properties.getProperty("datasource.password");

        var configuration = new DatasourceConfiguration();
        var dataSource = configuration.connectionPool(url, username, password);
        sql2o = configuration.databaseClient(dataSource);
        sql2oTicketRepository = new Sql2oTicketRepository(sql2o);
    }

    @BeforeEach
    void setUp() {
        try (var connection = sql2o.open()) {
            connection.createQuery("INSERT INTO files (id, name, path) VALUES (1, 'test.jpg', '/test/path1')").executeUpdate();
            connection.createQuery("INSERT INTO genres (id, name) VALUES (1, 'Action')").executeUpdate();
            connection.createQuery("INSERT INTO halls (id, name, row_count, place_count, description) VALUES (1, 'Hall 1', 10, 20, 'Test hall')").executeUpdate();
            for (int i = 1; i <= 10; i++) {
                connection.createQuery("INSERT INTO users (id, full_name, email, password) VALUES (:id, :name, :email, 'password')")
                    .addParameter("id", i)
                    .addParameter("name", "Test User " + i)
                    .addParameter("email", "user" + i + "@test.com")
                    .executeUpdate();
            }
            connection.createQuery("INSERT INTO films (id, name, description, \"year\", genre_id, minimal_age, duration_in_minutes, file_id) VALUES (1, 'Test Film', 'Test description', 2023, 1, 16, 120, 1)").executeUpdate();
            for (int i = 1; i <= 10; i++) {
                connection.createQuery("INSERT INTO film_sessions (id, film_id, halls_id, start_time, end_time, price) VALUES (:id, 1, 1, '2023-01-01 10:00:00', '2023-01-01 12:00:00', 500)")
                    .addParameter("id", i)
                    .executeUpdate();
            }
        }
    }

    @AfterEach
    void tearDown() {
        try (var connection = sql2o.open()) {
            connection.createQuery("DELETE FROM tickets").executeUpdate();
            connection.createQuery("DELETE FROM film_sessions").executeUpdate();
            connection.createQuery("DELETE FROM films").executeUpdate();
            connection.createQuery("DELETE FROM genres").executeUpdate();
            connection.createQuery("DELETE FROM files").executeUpdate();
            connection.createQuery("DELETE FROM halls").executeUpdate();
            connection.createQuery("DELETE FROM users").executeUpdate();
        }
    }

    @Test
    void whenSaveTicketThenReturnTicketWithId() {
        Ticket ticket = Ticket.builder()
                .sessionId(1)
                .rowNumber(5)
                .placeNumber(10)
                .userId(1)
                .build();

        Optional<Ticket> savedTicket = sql2oTicketRepository.save(ticket);

        assertThat(savedTicket).isPresent();
        assertThat(savedTicket.get().getId()).isNotNull();
        assertThat(savedTicket.get().getSessionId()).isEqualTo(1);
        assertThat(savedTicket.get().getRowNumber()).isEqualTo(5);
        assertThat(savedTicket.get().getPlaceNumber()).isEqualTo(10);
        assertThat(savedTicket.get().getUserId()).isEqualTo(1);
    }

    @Test
    void whenSaveTicketWithDuplicatePlaceThenReturnEmpty() {
        Ticket ticket1 = Ticket.builder()
                .sessionId(2)
                .rowNumber(3)
                .placeNumber(8)
                .userId(2)
                .build();

        Ticket ticket2 = Ticket.builder()
                .sessionId(2)
                .rowNumber(3)
                .placeNumber(8)
                .userId(3)
                .build();

        sql2oTicketRepository.save(ticket1);
        Optional<Ticket> result = sql2oTicketRepository.save(ticket2);

        assertThat(result).isEmpty();
    }

    @Test
    void whenIsPlaceAvailableForExistingTicketThenReturnFalse() {
        Ticket ticket = Ticket.builder()
                .sessionId(3)
                .rowNumber(7)
                .placeNumber(15)
                .userId(4)
                .build();

        sql2oTicketRepository.save(ticket);

        boolean isAvailable = sql2oTicketRepository.isPlaceAvailable(3, 7, 15);

        assertThat(isAvailable).isTrue();
    }

    @Test
    void whenIsPlaceAvailableForNonExistingTicketThenReturnFalse() {
        boolean isAvailable = sql2oTicketRepository.isPlaceAvailable(999, 1, 1);

        assertThat(isAvailable).isFalse();
    }

    @Test
    void whenSaveMultipleTicketsForDifferentPlacesThenAllAreSaved() {
        Ticket ticket1 = Ticket.builder()
                .sessionId(4)
                .rowNumber(2)
                .placeNumber(5)
                .userId(5)
                .build();

        Ticket ticket2 = Ticket.builder()
                .sessionId(4)
                .rowNumber(2)
                .placeNumber(6)
                .userId(6)
                .build();

        Ticket ticket3 = Ticket.builder()
                .sessionId(4)
                .rowNumber(3)
                .placeNumber(5)
                .userId(7)
                .build();

        Optional<Ticket> savedTicket1 = sql2oTicketRepository.save(ticket1);
        Optional<Ticket> savedTicket2 = sql2oTicketRepository.save(ticket2);
        Optional<Ticket> savedTicket3 = sql2oTicketRepository.save(ticket3);

        assertThat(savedTicket1).isPresent();
        assertThat(savedTicket2).isPresent();
        assertThat(savedTicket3).isPresent();

        assertThat(sql2oTicketRepository.isPlaceAvailable(4, 2, 5)).isTrue();
        assertThat(sql2oTicketRepository.isPlaceAvailable(4, 2, 6)).isTrue();
        assertThat(sql2oTicketRepository.isPlaceAvailable(4, 3, 5)).isTrue();
        assertThat(sql2oTicketRepository.isPlaceAvailable(4, 1, 1)).isFalse();
    }

    @Test
    void whenCheckDifferentSessionsSameSeatingThenReturnCorrectAvailability() {
        Ticket ticket = Ticket.builder()
                .sessionId(5)
                .rowNumber(4)
                .placeNumber(12)
                .userId(8)
                .build();

        sql2oTicketRepository.save(ticket);

        assertThat(sql2oTicketRepository.isPlaceAvailable(5, 4, 12)).isTrue();
        assertThat(sql2oTicketRepository.isPlaceAvailable(6, 4, 12)).isFalse();
    }
}