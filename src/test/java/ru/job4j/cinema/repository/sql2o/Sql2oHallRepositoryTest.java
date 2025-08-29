package ru.job4j.cinema.repository.sql2o;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.sql2o.Sql2o;
import ru.job4j.cinema.configuration.DatasourceConfiguration;
import ru.job4j.cinema.model.Hall;

import java.io.IOException;
import java.util.Optional;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

class Sql2oHallRepositoryTest {

    private static Sql2oHallRepository sql2oHallRepository;
    private static Sql2o sql2o;

    @BeforeAll
    static void initRepositories() throws IOException {
        var properties = new Properties();
        try (var inputStream = Sql2oHallRepositoryTest.class.getClassLoader()
                .getResourceAsStream("connection.properties")) {
            properties.load(inputStream);
        }
        var url = properties.getProperty("datasource.url");
        var username = properties.getProperty("datasource.username");
        var password = properties.getProperty("datasource.password");

        var configuration = new DatasourceConfiguration();
        var dataSource = configuration.connectionPool(url, username, password);
        sql2o = configuration.databaseClient(dataSource);
        sql2oHallRepository = new Sql2oHallRepository(sql2o);
    }

    @AfterEach
    void tearDown() {
        try (var connection = sql2o.open()) {
            connection.createQuery("DELETE FROM tickets").executeUpdate();
            connection.createQuery("DELETE FROM film_sessions").executeUpdate();
            connection.createQuery("DELETE FROM films").executeUpdate();
            connection.createQuery("DELETE FROM users").executeUpdate();
            connection.createQuery("DELETE FROM halls").executeUpdate();
            connection.createQuery("DELETE FROM genres").executeUpdate();
            connection.createQuery("DELETE FROM files").executeUpdate();
        }
    }

    @Test
    void whenFindByExistingIdThenReturnHall() {
        Hall hall = Hall.builder()
                .name("Morgan Freeman Theater")
                .rowCount(15)
                .placeCount(25)
                .description("Premium theater with leather seats")
                .build();

        try (var connection = sql2o.open()) {
            Integer generatedId = connection.createQuery(
                    "INSERT INTO halls (name, row_count, place_count, description) VALUES (:name, :rowCount, :placeCount, :description)", true)
                    .addParameter("name", hall.getName())
                    .addParameter("rowCount", hall.getRowCount())
                    .addParameter("placeCount", hall.getPlaceCount())
                    .addParameter("description", hall.getDescription())
                    .executeUpdate()
                    .getKey(Integer.class);
            hall.setId(generatedId);
        }

        Optional<Hall> foundHall = sql2oHallRepository.findById(hall.getId());

        assertThat(foundHall).isPresent();
        assertThat(foundHall.get().getId()).isEqualTo(hall.getId());
        assertThat(foundHall.get().getName()).isEqualTo("Morgan Freeman Theater");
        assertThat(foundHall.get().getRowCount()).isEqualTo(15);
        assertThat(foundHall.get().getPlaceCount()).isEqualTo(25);
        assertThat(foundHall.get().getDescription()).isEqualTo("Premium theater with leather seats");
    }

    @Test
    void whenFindByNonExistingIdThenReturnEmpty() {
        Optional<Hall> foundHall = sql2oHallRepository.findById(999);

        assertThat(foundHall).isEmpty();
    }

    @Test
    void whenFindMultipleHallsThenReturnCorrectOne() {
        Hall hall1 = Hall.builder()
                .name("Samuel L Jackson IMAX")
                .rowCount(20)
                .placeCount(30)
                .description("IMAX theater with surround sound")
                .build();
        
        Hall hall2 = Hall.builder()
                .name("Kevin Hart Comedy Hall")
                .rowCount(10)
                .placeCount(15)
                .description("Small intimate comedy venue")
                .build();

        try (var connection = sql2o.open()) {
            Integer id1 = connection.createQuery(
                    "INSERT INTO halls (name, row_count, place_count, description) VALUES (:name, :rowCount, :placeCount, :description)", true)
                    .addParameter("name", hall1.getName())
                    .addParameter("rowCount", hall1.getRowCount())
                    .addParameter("placeCount", hall1.getPlaceCount())
                    .addParameter("description", hall1.getDescription())
                    .executeUpdate()
                    .getKey(Integer.class);
            hall1.setId(id1);

            Integer id2 = connection.createQuery(
                    "INSERT INTO halls (name, row_count, place_count, description) VALUES (:name, :rowCount, :placeCount, :description)", true)
                    .addParameter("name", hall2.getName())
                    .addParameter("rowCount", hall2.getRowCount())
                    .addParameter("placeCount", hall2.getPlaceCount())
                    .addParameter("description", hall2.getDescription())
                    .executeUpdate()
                    .getKey(Integer.class);
            hall2.setId(id2);
        }

        Optional<Hall> foundHall1 = sql2oHallRepository.findById(hall1.getId());
        Optional<Hall> foundHall2 = sql2oHallRepository.findById(hall2.getId());

        assertThat(foundHall1).isPresent();
        assertThat(foundHall1.get().getName()).isEqualTo("Samuel L Jackson IMAX");
        assertThat(foundHall1.get().getRowCount()).isEqualTo(20);
        
        assertThat(foundHall2).isPresent();
        assertThat(foundHall2.get().getName()).isEqualTo("Kevin Hart Comedy Hall");
        assertThat(foundHall2.get().getPlaceCount()).isEqualTo(15);
    }
}