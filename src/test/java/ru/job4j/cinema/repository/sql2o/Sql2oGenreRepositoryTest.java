package ru.job4j.cinema.repository.sql2o;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.sql2o.Sql2o;
import ru.job4j.cinema.configuration.DatasourceConfiguration;
import ru.job4j.cinema.model.Genre;

import java.io.IOException;
import java.util.Optional;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

class Sql2oGenreRepositoryTest {

    private static Sql2oGenreRepository sql2oGenreRepository;
    private static Sql2o sql2o;

    @BeforeAll
    static void initRepositories() throws IOException {
        var properties = new Properties();
        try (var inputStream = Sql2oGenreRepositoryTest.class.getClassLoader()
                .getResourceAsStream("connection.properties")) {
            properties.load(inputStream);
        }
        var url = properties.getProperty("datasource.url");
        var username = properties.getProperty("datasource.username");
        var password = properties.getProperty("datasource.password");

        var configuration = new DatasourceConfiguration();
        var dataSource = configuration.connectionPool(url, username, password);
        sql2o = configuration.databaseClient(dataSource);
        sql2oGenreRepository = new Sql2oGenreRepository(sql2o);
    }

    @AfterEach
    void tearDown() {
        try (var connection = sql2o.open()) {
            connection.createQuery("DELETE FROM genres").executeUpdate();
        }
    }

    @Test
    void whenFindByExistingIdThenReturnGenre() {
        Genre genre = Genre.builder()
                .name("Will Smith Action")
                .build();

        try (var connection = sql2o.open()) {
            Integer generatedId = connection.createQuery(
                    "INSERT INTO genres (name) VALUES (:name)", true)
                    .addParameter("name", genre.getName())
                    .executeUpdate()
                    .getKey(Integer.class);
            genre.setId(generatedId);
        }

        Optional<Genre> foundGenre = sql2oGenreRepository.findById(genre.getId());

        assertThat(foundGenre).isPresent();
        assertThat(foundGenre.get().getId()).isEqualTo(genre.getId());
        assertThat(foundGenre.get().getName()).isEqualTo("Will Smith Action");
    }

    @Test
    void whenFindByNonExistingIdThenReturnEmpty() {
        Optional<Genre> foundGenre = sql2oGenreRepository.findById(999);

        assertThat(foundGenre).isEmpty();
    }

    @Test
    void whenFindMultipleGenresThenReturnCorrectOne() {
        Genre genre1 = Genre.builder()
                .name("Robert Downey Jr Comedy")
                .build();
        
        Genre genre2 = Genre.builder()
                .name("Denzel Washington Drama")
                .build();

        try (var connection = sql2o.open()) {
            Integer id1 = connection.createQuery(
                    "INSERT INTO genres (name) VALUES (:name)", true)
                    .addParameter("name", genre1.getName())
                    .executeUpdate()
                    .getKey(Integer.class);
            genre1.setId(id1);

            Integer id2 = connection.createQuery(
                    "INSERT INTO genres (name) VALUES (:name)", true)
                    .addParameter("name", genre2.getName())
                    .executeUpdate()
                    .getKey(Integer.class);
            genre2.setId(id2);
        }

        Optional<Genre> foundGenre1 = sql2oGenreRepository.findById(genre1.getId());
        Optional<Genre> foundGenre2 = sql2oGenreRepository.findById(genre2.getId());

        assertThat(foundGenre1).isPresent();
        assertThat(foundGenre1.get().getName()).isEqualTo("Robert Downey Jr Comedy");
        
        assertThat(foundGenre2).isPresent();
        assertThat(foundGenre2.get().getName()).isEqualTo("Denzel Washington Drama");
    }
}