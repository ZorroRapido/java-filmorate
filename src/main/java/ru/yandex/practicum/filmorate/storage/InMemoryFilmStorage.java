package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private Map<Long, Film> films = new HashMap<>();
    private Long counter = 0L;

    public Film create(Film film) {
        film.setId(getNextId());
        films.put(film.getId(), film);
        return film;
    }

    public Film update(Film film) throws FilmNotFoundException {
        if (!films.containsKey(film.getId())) {
            throw new FilmNotFoundException("Фильм с id = " + film.getId() + " не найден!");
        }
        films.put(film.getId(), film);
        return film;
    }

    public Film get(Long id) throws FilmNotFoundException {
        if (!films.containsKey(id)) {
            throw new FilmNotFoundException("Фильм с id = " + id + " не найден!");
        }
        return films.get(id);
    }

    public Map<Long, Film> getAll() {
        return films;
    }

    private Long getNextId() {
        counter++;
        return counter;
    }
}
