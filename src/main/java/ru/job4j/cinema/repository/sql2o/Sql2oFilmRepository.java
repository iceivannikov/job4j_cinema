package ru.job4j.cinema.repository.sql2o;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import org.sql2o.Connection;
import org.sql2o.Query;
import org.sql2o.Sql2o;
import ru.job4j.cinema.model.Film;
import ru.job4j.cinema.repository.FilmRepository;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Repository
public class Sql2oFilmRepository implements FilmRepository {

    private final Sql2o sql2o;

    @Override
    public List<Film> findAll() {
        try (Connection connection = sql2o.open()) {
            Query query = connection.createQuery("SELECT * FROM films");
            query.setColumnMappings(Film.COLUMN_MAPPING);
            return query.executeAndFetch(Film.class);
        }
    }

    @Override
    public Optional<Film> findById(Integer id) {
        try (Connection connection = sql2o.open()) {
            Query query = connection.createQuery("SELECT * FROM films WHERE id = :id");

            Film film = query
                    .addParameter("id", id)
                    .setColumnMappings(Film.COLUMN_MAPPING)
                    .executeAndFetchFirst(Film.class);

            return Optional.ofNullable(film);
        }
    }
}
