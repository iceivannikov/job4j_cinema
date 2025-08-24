package ru.job4j.cinema.repository.sql2o;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import org.sql2o.Connection;
import org.sql2o.Query;
import org.sql2o.Sql2o;
import ru.job4j.cinema.model.User;
import ru.job4j.cinema.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Repository
public class Sql2oUserRepository implements UserRepository {

    private final Sql2o sql2o;


    @Override
    public Optional<User> findById(Integer id) {
        try (Connection connection = sql2o.open()) {
            Query query = connection.createQuery("SELECT * FROM users WHERE id = :id");

            User user = query
                    .addParameter("id", id)
                    .setColumnMappings(User.COLUMN_MAPPING)
                    .executeAndFetchFirst(User.class);

            return Optional.ofNullable(user);
        }
    }

    @Override
    public User save(User user) {
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

            return User.builder()
                    .id(generatedId)
                    .fullName(user.getFullName())
                    .email(user.getEmail())
                    .password(user.getPassword())
                    .build();
        }
    }

    @Override
    public boolean update(User user) {
        try (Connection connection = sql2o.open()) {
            String sql = """
                    UPDATE users
                    SET full_name = :fullName,
                        email = :email,
                        password = :password
                    WHERE id = :id
                    """;

            Query query = connection.createQuery(sql);

            int affectedRows = query
                    .addParameter("id", user.getId())
                    .addParameter("fullName", user.getFullName())
                    .addParameter("email", user.getEmail())
                    .addParameter("password", user.getPassword())
                    .executeUpdate()
                    .getResult();

            return affectedRows == 1;
        }
    }

    @Override
    public Optional<User> findByEmail(String email) {
        try (Connection connection = sql2o.open()) {
            Query query = connection.createQuery("SELECT * FROM users WHERE email = :email");

            User user = query
                    .addParameter("email", email)
                    .setColumnMappings(User.COLUMN_MAPPING)
                    .executeAndFetchFirst(User.class);

            return Optional.ofNullable(user);
        }
    }

    @Override
    public boolean existsByEmail(String email) {
        try (Connection connection = sql2o.open()) {
            Query query = connection.createQuery("SELECT COUNT(*) FROM users WHERE email = :email");

            Integer count = query
                    .addParameter("email", email)
                    .executeScalar(Integer.class);

            return count > 0;
        }
    }
}
