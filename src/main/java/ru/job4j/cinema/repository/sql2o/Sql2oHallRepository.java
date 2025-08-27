package ru.job4j.cinema.repository.sql2o;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import org.sql2o.Connection;
import org.sql2o.Query;
import org.sql2o.Sql2o;
import ru.job4j.cinema.model.Hall;
import ru.job4j.cinema.repository.HallRepository;

import java.util.Optional;

@AllArgsConstructor
@Repository
public class Sql2oHallRepository implements HallRepository {

    private final Sql2o sql2o;

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
}
