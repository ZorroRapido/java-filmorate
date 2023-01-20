package ru.yandex.practicum.filmorate.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class FilmsRepository {
    private final JdbcTemplate jdbcTemplate;
    private final FilmsGenresRepository filmsGenresRepository;
    private final MpaRepository mpaRepository;

    @Autowired
    public FilmsRepository(JdbcTemplate jdbcTemplate, FilmsGenresRepository filmsGenresRepository,
                           MpaRepository mpaRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.filmsGenresRepository = filmsGenresRepository;
        this.mpaRepository = mpaRepository;
    }

    public Film save(Film film) {
        String sql = "insert into films (name, description, release_date, duration, rate, mpa_rating) " +
                "values (?, ?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(con -> {
            PreparedStatement stmt = con.prepareStatement(sql, new String[]{"id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
            stmt.setInt(4, film.getDuration());
            stmt.setInt(5, film.getRate());
            stmt.setLong(6, film.getMpa().getId());

            return stmt;
        }, keyHolder);

        film.setId(keyHolder.getKey().longValue());

        if (film.getGenres() != null) {
            film.getGenres().forEach(genre -> filmsGenresRepository.save(
                    FilmGenre.builder()
                            .filmId(film.getId())
                            .genreId(genre.getId())
                            .build()));
        }

        film.setMpa(mpaRepository.findOne(film.getMpa().getId()));

        List<Genre> genres = filmsGenresRepository.findFilmGenres(film.getId());
        film.setGenres(genres);

        return film;
    }

    public Film findOne(Long id) {
        String sql = "select id, name, description, release_date, duration, rate, mpa_rating from films where id = ?";

        return jdbcTemplate.queryForObject(sql, this::mapRowToFilm, id);
    }

    public boolean exists(Long id) {
        String sql = "select exists(select 1 from films where id = ?)";

        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, new Object[]{id}, Boolean.class));
    }

    public List<Film> findAll() {
        String sql = "select id, name, description, release_date, duration, rate, mpa_rating from films";

        return jdbcTemplate.query(sql, this::mapRowToFilm);
    }

    public Film update(Film film) {
        String sql = "update films set name = ?, description = ?, release_date = ?, duration = ?, rate = ?, " +
                "mpa_rating = ? where id = ?";

        jdbcTemplate.update(sql,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getRate(),
                film.getMpa().getId(),
                film.getId());

        filmsGenresRepository.delete(film.getId());

        if (film.getGenres() != null) {
            film.getGenres().forEach(genre -> {
                if (!filmsGenresRepository.exists(film.getId(), genre.getId())) {
                    filmsGenresRepository.save(FilmGenre.builder()
                            .filmId(film.getId())
                            .genreId(genre.getId())
                            .build());
                }
            });
        }

        return findOne(film.getId());
    }

    public boolean delete(Long id) {
        String sql = "delete from films where id = ?";

        return jdbcTemplate.update(sql, id) > 0;
    }

    public Film mapRowToFilm(ResultSet resultSet, int resNum) throws SQLException {
        List<Genre> genres = filmsGenresRepository.findFilmGenres(resultSet.getLong("id"));

        return Film.builder()
                .id(resultSet.getLong("id"))
                .name(resultSet.getString("name"))
                .description(resultSet.getString("description"))
                .releaseDate(resultSet.getDate("release_date").toLocalDate())
                .duration(resultSet.getInt("duration"))
                .rate(resultSet.getInt("rate"))
                .mpa(mpaRepository.findOne(resultSet.getLong("mpa_rating")))
                .genres(genres)
                .build();
    }
}
