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

            return query
                    .setColumnMappings(Film.COLUMN_MAPPING)
                    .executeAndFetch(Film.class);
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

    @Override
    public Film save(Film film) {
        try (Connection connection = sql2o.open()) {
            String sql = """
                    INSERT INTO films (name, description, year, genre_id, minimal_age, duration_in_minutes, file_id)
                    VALUES (:name, :description, :year, :genreId, :minimalAge, :durationInMinutes, :fileId)
                    """;

            Query query = connection.createQuery(sql, true); // true означает "вернуть сгенерированные ключи"

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

            return Film.builder()
                    .id(generatedId)
                    .name(film.getName())
                    .description(film.getDescription())
                    .year(film.getYear())
                    .genreId(film.getGenreId())
                    .minimalAge(film.getMinimalAge())
                    .durationInMinutes(film.getDurationInMinutes())
                    .fileId(film.getFileId())
                    .build();
        }
    }

    @Override
    public boolean update(Film film) {
        try (Connection connection = sql2o.open()) {
            String sql = """
                    UPDATE films
                    SET name = :name,
                        description = :description,
                        year = :year,
                        genre_id = :genreId,
                        minimal_age = :minimalAge,
                        duration_in_minutes = :durationInMinutes,
                        file_id = :fileId
                    WHERE id = :id
                    """;

            Query query = connection.createQuery(sql);

            int affectedRows = query
                    .addParameter("id", film.getId())
                    .addParameter("name", film.getName())
                    .addParameter("description", film.getDescription())
                    .addParameter("year", film.getYear())
                    .addParameter("genreId", film.getGenreId())
                    .addParameter("minimalAge", film.getMinimalAge())
                    .addParameter("durationInMinutes", film.getDurationInMinutes())
                    .addParameter("fileId", film.getFileId())
                    .executeUpdate()
                    .getResult();

            return affectedRows == 1;
        }
    }

    @Override
    public boolean deleteById(Integer id) {
        try (Connection connection = sql2o.open()) {
            Query query = connection.createQuery("DELETE FROM films WHERE id = :id");

            int affectedRows = query
                    .addParameter("id", id)
                    .executeUpdate()
                    .getResult();

            return affectedRows == 1;
        }
    }

    @Override
    public List<Film> findByGenreId(Integer genreId) {
        try (Connection connection = sql2o.open()) {
            Query query = connection.createQuery("SELECT * FROM films WHERE genre_id = :genreId");

            return query
                    .addParameter("genreId", genreId)
                    .setColumnMappings(Film.COLUMN_MAPPING)
                    .executeAndFetch(Film.class);
        }
    }
}
