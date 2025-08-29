package ru.job4j.cinema.repository.sql2o;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.sql2o.Sql2o;
import ru.job4j.cinema.configuration.DatasourceConfiguration;
import ru.job4j.cinema.model.Genre;

import java.util.Optional;
import java.util.Properties;

import static java.util.Optional.empty;
import static org.assertj.core.api.Assertions.assertThat;

class Sql2oGenreRepositoryTest {

    private static Sql2oGenreRepository sql2oGenreRepository;
    private static Sql2o sql2o;

    @BeforeAll
    public static void initRepositories() throws Exception {
        var properties = new Properties();
        try (var inputStream = Sql2oGenreRepositoryTest.class.getClassLoader().getResourceAsStream("connection.properties")) {
            properties.load(inputStream);
        }
        var url = properties.getProperty("datasource.url");
        var username = properties.getProperty("datasource.username");
        var password = properties.getProperty("datasource.password");

        var configuration = new DatasourceConfiguration();
        var datasource = configuration.connectionPool(url, username, password);
        sql2o = configuration.databaseClient(datasource);

        sql2oGenreRepository = new Sql2oGenreRepository(sql2o);
    }

    @AfterEach
    public void clearGenres() {
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
    void whenFindByExistingIdThenReturnGenre() {
        Integer genreId;
        try (var connection = sql2o.open()) {
            genreId = connection.createQuery("INSERT INTO genres (name) VALUES (:name)", true)
                    .addParameter("name", "Action")
                    .executeUpdate()
                    .getKey(Integer.class);
        }

        Optional<Genre> foundGenre = sql2oGenreRepository.findById(genreId);

        assertThat(foundGenre).isPresent();
        assertThat(foundGenre.get().getId()).isEqualTo(genreId);
        assertThat(foundGenre.get().getName()).isEqualTo("Action");
    }

    @Test
    void whenFindByNonExistingIdThenReturnEmpty() {
        Optional<Genre> foundGenre = sql2oGenreRepository.findById(999);

        assertThat(foundGenre).isEqualTo(empty());
    }

    @Test
    void whenFindMultipleGenresThenReturnCorrectOne() {
        Integer genreId1, genreId2;
        try (var connection = sql2o.open()) {
            genreId1 = connection.createQuery("INSERT INTO genres (name) VALUES (:name)", true)
                    .addParameter("name", "Comedy")
                    .executeUpdate()
                    .getKey(Integer.class);

            genreId2 = connection.createQuery("INSERT INTO genres (name) VALUES (:name)", true)
                    .addParameter("name", "Drama")
                    .executeUpdate()
                    .getKey(Integer.class);
        }

        Optional<Genre> foundGenre1 = sql2oGenreRepository.findById(genreId1);
        Optional<Genre> foundGenre2 = sql2oGenreRepository.findById(genreId2);

        assertThat(foundGenre1).isPresent();
        assertThat(foundGenre1.get().getName()).isEqualTo("Comedy");
        
        assertThat(foundGenre2).isPresent();
        assertThat(foundGenre2.get().getName()).isEqualTo("Drama");
    }
}