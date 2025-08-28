package ru.job4j.cinema.repository.sql2o;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import org.sql2o.Connection;
import org.sql2o.Query;
import org.sql2o.Sql2o;
import ru.job4j.cinema.model.Genre;
import ru.job4j.cinema.repository.GenreRepository;

import java.util.Optional;

@AllArgsConstructor
@Repository
public class Sql2oGenreRepository implements GenreRepository {

    private final Sql2o sql2o;

    @Override
    public Optional<Genre> findById(Integer id) {
        try (Connection connection = sql2o.open()) {
            Query query = connection.createQuery("SELECT * FROM genres WHERE id = :id");

            Genre genre = query
                    .addParameter("id", id)
                    .executeAndFetchFirst(Genre.class);

            return Optional.ofNullable(genre);
        }
    }
}
