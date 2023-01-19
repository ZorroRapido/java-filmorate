package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.repository.GenreRepository;
import ru.yandex.practicum.filmorate.repository.LikesRepository;
import ru.yandex.practicum.filmorate.repository.MpaRepository;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.time.LocalDate;
import java.time.Month;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserService userService;
    private final LikesRepository likesRepository;
    private final GenreRepository genreRepository;
    private final MpaRepository mpaRepository;

    @Autowired
    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage, UserService userService,
                       LikesRepository likesRepository, GenreRepository genreRepository, MpaRepository mpaRepository) {
        this.filmStorage = filmStorage;
        this.userService = userService;
        this.likesRepository = likesRepository;
        this.genreRepository = genreRepository;
        this.mpaRepository = mpaRepository;
    }

    public Film create(Film film, BindingResult errors) throws ValidationException {
        validate(film, errors);
        return filmStorage.create(film);
    }

    public Film update(Film film, BindingResult errors) throws FilmNotFoundException, ValidationException {
        if (!filmStorage.getAll().containsKey(film.getId())) {
            log.warn("Фильм с id = " + film.getId() + " не найден!");
            throw new FilmNotFoundException("Фильм с id = " + film.getId() + " не найден!");
        }
        validate(film, errors);
        return filmStorage.update(film);
    }

    public Film find(Long id) throws FilmNotFoundException {
        return filmStorage.get(id);
    }

    public Collection<Film> findAll() {
        return filmStorage.getAll().values();
    }

    public void like(Long id, Long userId) throws FilmNotFoundException, UserNotFoundException {
        Film film = find(id);
        User user = userService.find(userId);

        if (user != null && !likesRepository.findLikedFilmsByUserId(userId).contains(id)) {
            film.setRate(film.getRate() + 1);
            filmStorage.update(film);
        }

        likesRepository.save(Like.builder()
                .userId(userId)
                .filmId(id)
                .build());
    }

    public void unlike(Long id, Long userId) throws FilmNotFoundException, UserNotFoundException {
        Film film = find(id);
        User user = userService.find(userId);

        if (user != null && likesRepository.findLikedFilmsByUserId(userId).contains(id)) {
            film.setRate(film.getRate() - 1);
            filmStorage.update(film);
        }

        likesRepository.delete(userId, id);
    }

    public List<Film> findMostPopular(int count) {
        return findAll().stream()
                .sorted(Comparator.comparingInt(Film::getRate).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }

    public List<Genre> findAllGenres() {
        return genreRepository.findAll();
    }

    public Genre findGenre(Long id) throws GenreNotFoundException {
        if (genreRepository.exists(id)) {
            return genreRepository.findOne(id);
        } else {
            throw new GenreNotFoundException("Жанр с id = " + id + " не найден!");
        }
    }

    public List<Mpa> findAllMpa() {
        return mpaRepository.findAll();
    }

    public Mpa findMpa(Long id) throws MpaNotFoundException {
        if (mpaRepository.exists(id)) {
            return mpaRepository.findOne(id);
        } else {
            throw new MpaNotFoundException("MPA с id = " + id + " не найден!");
        }
    }

    private void validate(Film film, BindingResult errors) throws ValidationException {
        if (film.getDescription().length() > 200) {
            log.warn("Превышено максимальное кол-во символов в описании (200)!");
            throw new ValidationException("Превышено максимальное кол-во символов в описании (200)!");
        }

        if (film.getReleaseDate().isBefore(LocalDate.of(1895, Month.DECEMBER, 28))) {
            log.warn("Указанная дата релиза раньше дня рождения кино!");
            throw new ValidationException("Указанная дата релиза раньше дня рождения кино!");
        }

        if (errors.hasFieldErrors()) {
            FieldError error = errors.getFieldErrors().get(0);
            log.warn(String.format("Неверный формат %s! Поле %s!", error.getField(), error.getDefaultMessage()));
            throw new ValidationException(String.format("Неверный формат %s! Поле %s!", error.getField(),
                    error.getDefaultMessage()));
        }
    }
}
