package ru.job4j.cinema.repository.sql2o;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.sql2o.Query;
import org.sql2o.Sql2o;
import ru.job4j.cinema.configuration.DatasourceConfiguration;
import ru.job4j.cinema.model.Film;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

class Sql2oFilmRepositoryTest {

    private static Sql2oFilmRepository sql2oFilmRepository;
    private static Sql2o sql2o;

    @BeforeAll
    static void initRepositories() throws IOException {
        var properties = new Properties();
        try (var inputStream = Sql2oFilmRepositoryTest.class.getClassLoader()
                .getResourceAsStream("connection.properties")) {
            properties.load(inputStream);
        }
        var url = properties.getProperty("datasource.url");
        var username = properties.getProperty("datasource.username");
        var password = properties.getProperty("datasource.password");

        var configuration = new DatasourceConfiguration();
        var dataSource = configuration.connectionPool(url, username, password);
        sql2o = configuration.databaseClient(dataSource);
        sql2oFilmRepository = new Sql2oFilmRepository(sql2o);
    }

    @AfterEach
    void tearDown() {
        try (var connection = sql2o.open()) {
            connection.createQuery("DELETE FROM films").executeUpdate();
            connection.createQuery("DELETE FROM genres").executeUpdate();
            connection.createQuery("DELETE FROM files").executeUpdate();
        }
    }

    @Test
    void whenFindAllThenReturnAllFilms() {
        setupTestData();

        Film film1 = createTestFilm("Matt Damon Adventure", "Action-packed thriller", 
                2023, 1, 13, 120, 1);
        Film film2 = createTestFilm("Ryan Reynolds Comedy", "Hilarious comedy", 
                2024, 2, 16, 95, 2);

        insertFilm(film1);
        insertFilm(film2);

        List<Film> films = sql2oFilmRepository.findAll();

        assertThat(films).hasSize(2);
        assertThat(films.get(0).getName()).isEqualTo("Matt Damon Adventure");
        assertThat(films.get(1).getName()).isEqualTo("Ryan Reynolds Comedy");
    }

    private void setupTestData() {
        try (var connection = sql2o.open()) {
            connection.createQuery("INSERT INTO genres (id, name) VALUES (1, 'Action')").executeUpdate();
            connection.createQuery("INSERT INTO genres (id, name) VALUES (2, 'Comedy')").executeUpdate();
            connection.createQuery("INSERT INTO files (id, name, path) VALUES (1, 'file1', 'path1')").executeUpdate();
            connection.createQuery("INSERT INTO files (id, name, path) VALUES (2, 'file2', 'path2')").executeUpdate();
        }
    }

    private Film createTestFilm(String name, String description, int year, int genreId, 
                               int minimalAge, int durationInMinutes, int fileId) {
        return Film.builder()
                .name(name)
                .description(description)
                .year(year)
                .genreId(genreId)
                .minimalAge(minimalAge)
                .durationInMinutes(durationInMinutes)
                .fileId(fileId)
                .build();
    }

    private void insertFilm(Film film) {
        try (var connection = sql2o.open()) {
            connection.createQuery(
                    "INSERT INTO films (name, description, \"year\", genre_id, minimal_age, duration_in_minutes, file_id) "
                    + "VALUES (:name, :description, :year, :genreId, :minimalAge, :durationInMinutes, :fileId)")
                    .addParameter("name", film.getName())
                    .addParameter("description", film.getDescription())
                    .addParameter("year", film.getYear())
                    .addParameter("genreId", film.getGenreId())
                    .addParameter("minimalAge", film.getMinimalAge())
                    .addParameter("durationInMinutes", film.getDurationInMinutes())
                    .addParameter("fileId", film.getFileId())
                    .executeUpdate();
        }
    }

    @Test
    void whenFindAllWithNoFilmsThenReturnEmptyList() {
        List<Film> films = sql2oFilmRepository.findAll();

        assertThat(films).isEmpty();
    }

    @Test
    void whenFindByExistingIdThenReturnFilm() {
        try (var connection = sql2o.open()) {
            // Create prerequisite data
            connection.createQuery("INSERT INTO genres (id, name) VALUES (3, 'Epic')").executeUpdate();
            connection.createQuery("INSERT INTO files (id, name, path) VALUES (3, 'file3', 'path3')").executeUpdate();
        }

        Film film = Film.builder()
                .name("Chris Hemsworth Epic")
                .description("Thor-like adventure")
                .year(2022)
                .genreId(3)
                .minimalAge(12)
                .durationInMinutes(150)
                .fileId(3)
                .build();

        try (var connection = sql2o.open()) {
            String sql = """
                    INSERT INTO films (name, description, "year", genre_id, minimal_age, duration_in_minutes, file_id)
                    VALUES (:name, :description, :year, :genreId, :minimalAge, :durationInMinutes, :fileId)
                    """;
            Query query = connection.createQuery(sql, true);
            Integer generatedId = query
                    .addParameter("name", film.getName())
                    .addParameter("description", film.getDescription())
                    .addParameter("year", film.getYear())
                    .addParameter("genreId", film.getGenreId())
                    .addParameter("minimalAge", film.getMinimalAge())
                    .addParameter("durationInMinutes", film.getDurationInMinutes())
                    .addParameter("fileId", film.getFileId())
                    .executeUpdate()
                    .getKey(Integer.class);
            film.setId(generatedId);
        }

        Optional<Film> foundFilm = sql2oFilmRepository.findById(film.getId());

        assertThat(foundFilm).isPresent();
        assertThat(foundFilm.get().getId()).isEqualTo(film.getId());
        assertThat(foundFilm.get().getName()).isEqualTo("Chris Hemsworth Epic");
        assertThat(foundFilm.get().getDescription()).isEqualTo("Thor-like adventure");
        assertThat(foundFilm.get().getYear()).isEqualTo(2022);
        assertThat(foundFilm.get().getGenreId()).isEqualTo(3);
        assertThat(foundFilm.get().getMinimalAge()).isEqualTo(12);
        assertThat(foundFilm.get().getDurationInMinutes()).isEqualTo(150);
        assertThat(foundFilm.get().getFileId()).isEqualTo(3);
    }

    @Test
    void whenFindByNonExistingIdThenReturnEmpty() {
        Optional<Film> foundFilm = sql2oFilmRepository.findById(999);

        assertThat(foundFilm).isEmpty();
    }
}