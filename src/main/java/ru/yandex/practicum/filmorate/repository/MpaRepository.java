package ru.yandex.practicum.filmorate.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class MpaRepository {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MpaRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Long save(Mpa mpa) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("mpa")
                .usingGeneratedKeyColumns("id");

        return simpleJdbcInsert.executeAndReturnKey(mpa.toMap()).longValue();
    }

    public Mpa findOne(Long id) {
        String sql = "select id, name from mpa where id = ?";

        return jdbcTemplate.queryForObject(sql, this::mapRowToMpa, id);
    }

    public boolean exists(Long id) {
        String sql = "select exists(select 1 from mpa where id = ?)";

        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, new Object[]{id}, Boolean.class));
    }

    public List<Mpa> findAll() {
        String sql = "select id, name from mpa";

        return jdbcTemplate.query(sql, this::mapRowToMpa);
    }

    public boolean delete(Long id) {
        String sql = "delete from mpa where id = ?";

        return jdbcTemplate.update(sql, id) > 0;
    }

    private Mpa mapRowToMpa(ResultSet resultSet, int rowNum) throws SQLException {
        return Mpa.builder()
                .id(resultSet.getLong("id"))
                .name(resultSet.getString("name"))
                .build();
    }
}
