package ru.job4j.cinema.repository.sql2o;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.sql2o.Sql2o;
import ru.job4j.cinema.configuration.DatasourceConfiguration;
import ru.job4j.cinema.model.File;

import java.io.IOException;
import java.util.Optional;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

class Sql2oFileRepositoryTest {

    private static Sql2oFileRepository sql2oFileRepository;
    private static Sql2o sql2o;

    @BeforeAll
    static void initRepositories() throws IOException {
        var properties = new Properties();
        try (var inputStream = Sql2oFileRepositoryTest.class.getClassLoader()
                .getResourceAsStream("connection.properties")) {
            properties.load(inputStream);
        }
        var url = properties.getProperty("datasource.url");
        var username = properties.getProperty("datasource.username");
        var password = properties.getProperty("datasource.password");

        var configuration = new DatasourceConfiguration();
        var dataSource = configuration.connectionPool(url, username, password);
        sql2o = configuration.databaseClient(dataSource);
        sql2oFileRepository = new Sql2oFileRepository(sql2o);
    }

    @AfterEach
    void tearDown() {
        try (var connection = sql2o.open()) {
            connection.createQuery("DELETE FROM files").executeUpdate();
        }
    }

    @Test
    void whenFindByExistingIdThenReturnFile() {
        File file = File.builder()
                .name("Brad Pitt Avatar")
                .path("files/brad_pitt.jpg")
                .build();

        try (var connection = sql2o.open()) {
            Integer generatedId = connection.createQuery(
                    "INSERT INTO files (name, path) VALUES (:name, :path)", true)
                    .addParameter("name", file.getName())
                    .addParameter("path", file.getPath())
                    .executeUpdate()
                    .getKey(Integer.class);
            file.setId(generatedId);
        }

        Optional<File> foundFile = sql2oFileRepository.findById(file.getId());

        assertThat(foundFile).isPresent();
        assertThat(foundFile.get().getId()).isEqualTo(file.getId());
        assertThat(foundFile.get().getName()).isEqualTo("Brad Pitt Avatar");
        assertThat(foundFile.get().getPath()).isEqualTo("files/brad_pitt.jpg");
    }

    @Test
    void whenFindByNonExistingIdThenReturnEmpty() {
        Optional<File> foundFile = sql2oFileRepository.findById(999);

        assertThat(foundFile).isEmpty();
    }

    @Test
    void whenFindMultipleFilesThenReturnCorrectOne() {
        File file1 = File.builder()
                .name("Leonardo DiCaprio Photo")
                .path("files/leonardo.jpg")
                .build();
        
        File file2 = File.builder()
                .name("Tom Hanks Picture")
                .path("files/tom_hanks.jpg")
                .build();

        try (var connection = sql2o.open()) {
            Integer id1 = connection.createQuery(
                    "INSERT INTO files (name, path) VALUES (:name, :path)", true)
                    .addParameter("name", file1.getName())
                    .addParameter("path", file1.getPath())
                    .executeUpdate()
                    .getKey(Integer.class);
            file1.setId(id1);

            Integer id2 = connection.createQuery(
                    "INSERT INTO files (name, path) VALUES (:name, :path)", true)
                    .addParameter("name", file2.getName())
                    .addParameter("path", file2.getPath())
                    .executeUpdate()
                    .getKey(Integer.class);
            file2.setId(id2);
        }

        Optional<File> foundFile1 = sql2oFileRepository.findById(file1.getId());
        Optional<File> foundFile2 = sql2oFileRepository.findById(file2.getId());

        assertThat(foundFile1).isPresent();
        assertThat(foundFile1.get().getName()).isEqualTo("Leonardo DiCaprio Photo");
        
        assertThat(foundFile2).isPresent();
        assertThat(foundFile2.get().getName()).isEqualTo("Tom Hanks Picture");
    }
}