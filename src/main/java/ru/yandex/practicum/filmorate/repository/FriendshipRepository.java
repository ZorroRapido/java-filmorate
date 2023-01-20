package ru.yandex.practicum.filmorate.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Friendship;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Repository
public class FriendshipRepository {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FriendshipRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Friendship save(Friendship friendship) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("friendship")
                .usingGeneratedKeyColumns();

        simpleJdbcInsert.execute(friendship.toMap());
        return friendship;
    }

    public Set<Long> findFriendsIds(Long id) {
        Set<Long> friendsIds = new LinkedHashSet<>();

        String sql = "select id, friend_id, status from friendship where id = ?";

        List<Friendship> entities1 = jdbcTemplate.query(sql, this::mapRowToFriendship, id);
        entities1.forEach(entity -> friendsIds.add(entity.getFriendId()));

        return friendsIds;
    }

    public Friendship findOne(Long id, Long friend_id) {
        String sql = "select id, friend_id, status from friendship where id = ? and friend_id = ?";

        return jdbcTemplate.queryForObject(sql, this::mapRowToFriendship, id, friend_id);
    }

    public boolean exists(Long id, Long friendId) {
        String sql = "select exists(select 1 from friendship where id = ? and friend_id = ?)";

        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, new Object[]{id, friendId}, Boolean.class));
    }

    public void update(Friendship friendship) {
        String sql = "update friendship set status = ? where id = ? and friend_id = ?";

        jdbcTemplate.update(sql, friendship.getStatus(), friendship.getId(), friendship.getFriendId());
    }

    public boolean delete(Long id, Long friend_id) {
        String sql = "delete from friendship where id = ? and friend_id = ?";

        return jdbcTemplate.update(sql, id, friend_id) > 0;
    }

    private Friendship mapRowToFriendship(ResultSet resultSet, int rowNum) throws SQLException {
        return Friendship.builder()
                .id(resultSet.getLong("id"))
                .friendId(resultSet.getLong("friend_id"))
                .status(resultSet.getString("status"))
                .build();
    }
}
