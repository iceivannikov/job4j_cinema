package ru.job4j.cinema.repository.sql2o;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import org.sql2o.Connection;
import org.sql2o.Query;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;
import ru.job4j.cinema.model.User;
import ru.job4j.cinema.repository.UserRepository;

import java.util.Optional;

@AllArgsConstructor
@Repository
public class Sql2oUserRepository implements UserRepository {

    private final Sql2o sql2o;

    @Override
    public Optional<User> save(User user) {
        try (Connection connection = sql2o.open()) {
            String sql = """
                    INSERT INTO users (full_name, email, password)
                    VALUES (:fullName, :email, :password)
                    """;
            Query query = connection.createQuery(sql, true);

            Integer generatedId = query
                    .addParameter("fullName", user.getFullName())
                    .addParameter("email", user.getEmail())
                    .addParameter("password", user.getPassword())
                    .executeUpdate()
                    .getKey(Integer.class);

            User newUser = User.builder()
                    .id(generatedId)
                    .fullName(user.getFullName())
                    .email(user.getEmail())
                    .password(user.getPassword())
                    .build();

            return Optional.of(newUser);
        } catch (Sql2oException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<User> findByEmail(String email) {
        try (Connection connection = sql2o.open()) {
            String sql = """
                    SELECT * FROM users WHERE email = :email
                    """;
            User user = connection.createQuery(sql)
                    .addParameter("email", email)
                    .setColumnMappings(User.COLUMN_MAPPING)
                    .executeAndFetchFirst(User.class);
            return Optional.ofNullable(user);
        }
    }

    @Override
    public Optional<User> findById(Integer id) {
        try (Connection connection = sql2o.open()) {
            String sql = """
                    SELECT * FROM users WHERE id = :id
                    """;
            User user = connection.createQuery(sql)
                    .addParameter("id", id)
                    .setColumnMappings(User.COLUMN_MAPPING)
                    .executeAndFetchFirst(User.class);
            return Optional.ofNullable(user);
        }
    }

    @Override
    public boolean existsByEmail(String email) {
        try (Connection connection = sql2o.open()) {
            String sql = """
                    SELECT COUNT(*) FROM users WHERE email = :email
                    """;
            Query query = connection.createQuery(sql);
            query.addParameter("email", email);
            Integer executed = query.executeScalar(Integer.class);
            return executed > 0;
        }
    }
}
