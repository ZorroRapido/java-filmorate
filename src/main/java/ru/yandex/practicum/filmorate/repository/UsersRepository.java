package ru.yandex.practicum.filmorate.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class UsersRepository {
    private final JdbcTemplate jdbcTemplate;
    private final FriendshipRepository friendshipRepository;
    private final LikesRepository likesRepository;

    @Autowired
    public UsersRepository(JdbcTemplate jdbcTemplate, FriendshipRepository friendshipRepository,
                           LikesRepository likesRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.friendshipRepository = friendshipRepository;
        this.likesRepository = likesRepository;
    }

    public User save(User user) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("id");

        user.setId(simpleJdbcInsert.executeAndReturnKey(user.toMap()).longValue());
        return user;
    }

    public User findOne(Long id) {
        String sql = "select id, email, login, name, birthday from users where id = ?";

        User user = jdbcTemplate.queryForObject(sql, this::mapRowToUser, id);
        return user;
    }

    public boolean exists(Long id) {
        String sql = "select exists(select 1 from users where id = ?)";

        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, new Object[]{id}, Boolean.class));
    }

    public List<User> findAll() {
        String sql = "select id, email, login, name, birthday from users";

        return jdbcTemplate.query(sql, this::mapRowToUser);
    }

    public User update(User user) {
        String sql = "update users set email = ?, login = ?, name = ?, birthday = ? where id = ?";

        jdbcTemplate.update(sql,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId());

        return findOne(user.getId());
    }

    public boolean delete(Long id) {
        String sql = "delete from users where id = ?";

        return jdbcTemplate.update(sql, id) > 0;
    }

    private User mapRowToUser(ResultSet resultSet, int rowNum) throws SQLException {
        return User.builder()
                .id(resultSet.getLong("id"))
                .email(resultSet.getString("email"))
                .login(resultSet.getString("login"))
                .name(resultSet.getString("name"))
                .birthday(resultSet.getDate("birthday").toLocalDate())
                .friends(friendshipRepository.findFriendsIds(resultSet.getLong("id")))
                .likedFilms(likesRepository.findLikedFilmsByUserId(resultSet.getLong("id")))
                .build();
    }
}
