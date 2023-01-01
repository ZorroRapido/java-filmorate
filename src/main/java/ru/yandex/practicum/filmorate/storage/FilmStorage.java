package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Map;

public interface FilmStorage {
    Film create(Film film);

    Film update(Film film) throws FilmNotFoundException;

    Film get(Long id) throws FilmNotFoundException;

    Map<Long, Film> getAll();
}
