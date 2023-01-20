package ru.yandex.practicum.filmorate.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Like;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Repository
public class LikesRepository {
    private final JdbcTemplate jdbcTemplate;

    public LikesRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(Like like) {
        String sql = "insert into likes (user_id, film_id) values (?, ?)";

        jdbcTemplate.update(sql, like.getUserId(), like.getFilmId());
    }

    public Like findOne(Long userId, Long filmId) {
        String sql = "select user_id, film_id from likes where user_id = ? and film_id = ?";

        return jdbcTemplate.queryForObject(sql, this::mapRowToLike, userId, filmId);
    }

    public Set<Long> findLikedFilmsByUserId(Long userId) {
        HashSet<Long> likedFilmsIds = new LinkedHashSet<>();

        String sql = "select user_id, film_id from likes where user_id = ?";

        List<Like> likes = jdbcTemplate.query(sql, this::mapRowToLike, userId);
        likes.forEach(like -> likedFilmsIds.add(like.getFilmId()));

        return likedFilmsIds;
    }

    public boolean delete(Long userId, Long filmId) {
        String sql = "delete from likes where user_id = ? and film_id = ?";

        return jdbcTemplate.update(sql, userId, filmId) > 0;
    }

    private Like mapRowToLike(ResultSet resultSet, int rowNum) throws SQLException {
        return Like.builder()
                .userId(resultSet.getLong("user_id"))
                .filmId(resultSet.getLong("film_id"))
                .build();
    }
}
