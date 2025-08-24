package ru.job4j.cinema.repository.sql2o;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import org.sql2o.Connection;
import org.sql2o.Query;
import org.sql2o.Sql2o;
import ru.job4j.cinema.model.File;
import ru.job4j.cinema.repository.FileRepository;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Repository
public class Sql2oFileRepository implements FileRepository {

    private final Sql2o sql2o;


    @Override
    public Optional<File> findById(Integer id) {
        try (Connection connection = sql2o.open()) {
            Query query = connection.createQuery("SELECT * FROM files WHERE id = :id");

            File file = query
                    .addParameter("id", id)
                    .executeAndFetchFirst(File.class);

            return Optional.ofNullable(file);
        }
    }

    @Override
    public Optional<File> findByName(String name) {
        try (Connection connection = sql2o.open()) {
            Query query = connection.createQuery("SELECT * FROM files WHERE name = :name");

            File file = query
                    .addParameter("name", name)
                    .executeAndFetchFirst(File.class);

            return Optional.ofNullable(file);
        }
    }

}
