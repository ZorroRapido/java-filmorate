package ru.yandex.practicum.filmorate.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class FilmsGenresRepository {
    private final JdbcTemplate jdbcTemplate;
    private final GenreRepository genreRepository;

    @Autowired
    public FilmsGenresRepository(JdbcTemplate jdbcTemplate, GenreRepository genreRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.genreRepository = genreRepository;
    }

    public void save(FilmGenre filmGenre) {
        String sql = "insert into films_genres (film_id, genre_id) values (?, ?)";

        jdbcTemplate.update(sql, filmGenre.getFilmId(), filmGenre.getGenreId());
    }

    public List<Genre> findFilmGenres(Long id) {
        String sql = "select film_id, genre_id from films_genres where film_id = ?";

        List<Genre> filmGenres = new ArrayList<>();
        List<FilmGenre> entities = jdbcTemplate.query(sql, this::mapRowToFilmGenre, id);

        entities.forEach(entity -> filmGenres.add(genreRepository.findOne(entity.getGenreId())));

        return filmGenres;
    }

    public void update(Long filmId, Long genreId) {
        String sql = "update films_genres set genre_id = ? where film_id = ?";

        jdbcTemplate.update(sql, genreId, filmId);
    }

    public boolean exists(Long filmId, Long genreId) {
        String sql = "select exists(select 1 from films_genres where film_id = ? and genre_id = ?)";

        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, new Object[]{filmId, genreId}, Boolean.class));
    }

    public List<FilmGenre> findAll() {
        String sql = "select film_id, genre_id from films_genres";

        return jdbcTemplate.query(sql, this::mapRowToFilmGenre);
    }

    public boolean delete(Long filmId) {
        String sql = "delete from films_genres where film_id = ?";

        return jdbcTemplate.update(sql, filmId) > 0;
    }

    public FilmGenre mapRowToFilmGenre(ResultSet resultSet, int resNum) throws SQLException {
        return FilmGenre.builder()
                .filmId(resultSet.getLong("film_id"))
                .genreId(resultSet.getLong("genre_id"))
                .build();
    }
}
