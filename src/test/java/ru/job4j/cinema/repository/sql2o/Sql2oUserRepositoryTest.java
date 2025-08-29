package ru.job4j.cinema.repository.sql2o;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.sql2o.Sql2o;
import ru.job4j.cinema.configuration.DatasourceConfiguration;
import ru.job4j.cinema.model.User;

import java.io.IOException;
import java.util.Optional;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

class Sql2oUserRepositoryTest {

    private static Sql2oUserRepository sql2oUserRepository;
    private static Sql2o sql2o;

    @BeforeAll
    static void initRepositories() throws IOException {
        var properties = new Properties();
        try (var inputStream = Sql2oUserRepositoryTest.class.getClassLoader()
                .getResourceAsStream("connection.properties")) {
            properties.load(inputStream);
        }
        var url = properties.getProperty("datasource.url");
        var username = properties.getProperty("datasource.username");
        var password = properties.getProperty("datasource.password");

        var configuration = new DatasourceConfiguration();
        var dataSource = configuration.connectionPool(url, username, password);
        sql2o = configuration.databaseClient(dataSource);
        sql2oUserRepository = new Sql2oUserRepository(sql2o);
    }

    @AfterEach
    void tearDown() {
        try (var connection = sql2o.open()) {
            connection.createQuery("DELETE FROM users").executeUpdate();
        }
    }

    @Test
    void whenSaveUserThenReturnUserWithId() {
        User user = User.builder()
                .fullName("John Doe")
                .email("john.doe@example.com")
                .password("password123")
                .build();

        Optional<User> savedUser = sql2oUserRepository.save(user);

        assertThat(savedUser).isPresent();
        assertThat(savedUser.get().getId()).isNotNull();
        assertThat(savedUser.get().getFullName()).isEqualTo("John Doe");
        assertThat(savedUser.get().getEmail()).isEqualTo("john.doe@example.com");
        assertThat(savedUser.get().getPassword()).isEqualTo("password123");
    }

    @Test
    void whenSaveUserWithDuplicateEmailThenReturnEmpty() {
        User user1 = User.builder()
                .fullName("John Doe")
                .email("john.doe@example.com")
                .password("password123")
                .build();

        User user2 = User.builder()
                .fullName("Jane Doe")
                .email("john.doe@example.com")
                .password("password456")
                .build();

        sql2oUserRepository.save(user1);
        Optional<User> result = sql2oUserRepository.save(user2);

        assertThat(result).isEmpty();
    }

    @Test
    void whenFindByExistingEmailThenReturnUser() {
        User user = User.builder()
                .fullName("John Doe")
                .email("john.doe@example.com")
                .password("password123")
                .build();

        sql2oUserRepository.save(user);

        Optional<User> foundUser = sql2oUserRepository.findByEmail("john.doe@example.com");

        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getFullName()).isEqualTo("John Doe");
        assertThat(foundUser.get().getEmail()).isEqualTo("john.doe@example.com");
        assertThat(foundUser.get().getPassword()).isEqualTo("password123");
    }

    @Test
    void whenFindByNonExistingEmailThenReturnEmpty() {
        Optional<User> foundUser = sql2oUserRepository.findByEmail("nonexistent@example.com");

        assertThat(foundUser).isEmpty();
    }

    @Test
    void whenFindByExistingIdThenReturnUser() {
        User user = User.builder()
                .fullName("John Doe")
                .email("john.doe@example.com")
                .password("password123")
                .build();

        Optional<User> savedUser = sql2oUserRepository.save(user);
        Integer userId = savedUser.get().getId();

        Optional<User> foundUser = sql2oUserRepository.findById(userId);

        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getId()).isEqualTo(userId);
        assertThat(foundUser.get().getFullName()).isEqualTo("John Doe");
        assertThat(foundUser.get().getEmail()).isEqualTo("john.doe@example.com");
        assertThat(foundUser.get().getPassword()).isEqualTo("password123");
    }

    @Test
    void whenFindByNonExistingIdThenReturnEmpty() {
        Optional<User> foundUser = sql2oUserRepository.findById(999);

        assertThat(foundUser).isEmpty();
    }

    @Test
    void whenExistsByEmailForExistingUserThenReturnTrue() {
        User user = User.builder()
                .fullName("John Doe")
                .email("john.doe@example.com")
                .password("password123")
                .build();

        sql2oUserRepository.save(user);

        boolean exists = sql2oUserRepository.existsByEmail("john.doe@example.com");

        assertThat(exists).isTrue();
    }

    @Test
    void whenExistsByEmailForNonExistingUserThenReturnFalse() {
        boolean exists = sql2oUserRepository.existsByEmail("nonexistent@example.com");

        assertThat(exists).isFalse();
    }

    @Test
    void whenSaveMultipleUsersThenAllAreSaved() {
        User user1 = User.builder()
                .fullName("John Doe")
                .email("john.doe@example.com")
                .password("password123")
                .build();

        User user2 = User.builder()
                .fullName("Jane Smith")
                .email("jane.smith@example.com")
                .password("password456")
                .build();

        Optional<User> savedUser1 = sql2oUserRepository.save(user1);
        Optional<User> savedUser2 = sql2oUserRepository.save(user2);

        assertThat(savedUser1).isPresent();
        assertThat(savedUser2).isPresent();
        assertThat(savedUser1.get().getId()).isNotEqualTo(savedUser2.get().getId());

        assertThat(sql2oUserRepository.existsByEmail("john.doe@example.com")).isTrue();
        assertThat(sql2oUserRepository.existsByEmail("jane.smith@example.com")).isTrue();
    }
}