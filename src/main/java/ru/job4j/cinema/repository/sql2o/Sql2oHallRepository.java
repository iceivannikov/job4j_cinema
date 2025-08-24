package ru.job4j.cinema.repository.sql2o;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import org.sql2o.Connection;
import org.sql2o.Query;
import org.sql2o.Sql2o;
import ru.job4j.cinema.model.Hall;
import ru.job4j.cinema.repository.HallRepository;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Repository
public class Sql2oHallRepository implements HallRepository {

    private final Sql2o sql2o;

    @Override
    public List<Hall> findAll() {
        try (Connection connection = sql2o.open()) {
            Query query = connection.createQuery("SELECT * FROM halls");

            return query
                    .setColumnMappings(Hall.COLUMN_MAPPING)
                    .executeAndFetch(Hall.class);
        }
    }

    @Override
    public Optional<Hall> findById(Integer id) {
        try (Connection connection = sql2o.open()) {
            Query query = connection.createQuery("SELECT * FROM halls WHERE id = :id");

            Hall hall = query
                    .addParameter("id", id)
                    .setColumnMappings(Hall.COLUMN_MAPPING)
                    .executeAndFetchFirst(Hall.class);

            return Optional.ofNullable(hall);
        }
    }

    @Override
    public Hall save(Hall hall) {
        try (Connection connection = sql2o.open()) {
            String sql = """
                    INSERT INTO halls (name, row_count, place_count, description)
                    VALUES (:name, :rowCount, :placeCount, :description)
                    """;
            Query query = connection.createQuery(sql, true);

            Integer generatedId = query
                    .addParameter("name", hall.getName())
                    .addParameter("rowCount", hall.getRowCount())
                    .addParameter("placeCount", hall.getPlaceCount())
                    .addParameter("description", hall.getDescription())
                    .executeUpdate()
                    .getKey(Integer.class);

            return Hall.builder()
                    .id(generatedId)
                    .name(hall.getName())
                    .rowCount(hall.getRowCount())
                    .placeCount(hall.getPlaceCount())
                    .description(hall.getDescription())
                    .build();
        }
    }

    @Override
    public boolean update(Hall hall) {
        try (Connection connection = sql2o.open()) {
            String sql = """
                    UPDATE halls
                    SET name = :name,
                        row_count = :rowCount,
                        place_count = :placeCount,
                        description = :description
                    WHERE id = :id
                    """;

            Query query = connection.createQuery(sql);

            int affectedRows = query
                    .addParameter("id", hall.getId())
                    .addParameter("name", hall.getName())
                    .addParameter("rowCount", hall.getRowCount())
                    .addParameter("placeCount", hall.getPlaceCount())
                    .addParameter("description", hall.getDescription())
                    .executeUpdate()
                    .getResult();

            return affectedRows == 1;
        }
    }

    @Override
    public boolean deleteById(Integer id) {
        try (Connection connection = sql2o.open()) {
            Query query = connection.createQuery("DELETE FROM halls WHERE id = :id");

            int affectedRows = query
                    .addParameter("id", id)
                    .executeUpdate()
                    .getResult();

            return affectedRows == 1;
        }
    }
}
