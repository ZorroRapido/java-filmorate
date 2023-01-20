package ru.yandex.practicum.filmorate.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class GenreRepository {
    private final JdbcTemplate jdbcTemplate;

    public GenreRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(Genre genre) {
        String sql = "insert into genres (name) values (?)";

        jdbcTemplate.update(sql, genre.getId(), genre.getName());
    }

    public Genre findOne(Long id) {
        String sql = "select id, name from genres where id = ?";

        return jdbcTemplate.queryForObject(sql, this::mapRowToGenre, id);
    }

    public boolean exists(Long id) {
        String sql = "select exists(select 1 from genres where id = ?)";

        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, new Object[]{id}, Boolean.class));
    }

    public List<Genre> findAll() {
        String sql = "select id, name from genres";

        return jdbcTemplate.query(sql, this::mapRowToGenre);
    }

    public boolean delete(Long id) {
        String sql = "delete from genres where id = ?";

        return jdbcTemplate.update(sql, id) > 0;
    }

    private Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        return Genre.builder()
                .id(resultSet.getLong("id"))
                .name(resultSet.getString("name"))
                .build();
    }
}
