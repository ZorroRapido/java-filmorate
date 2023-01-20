package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.repository.FilmsRepository;

import java.util.HashMap;
import java.util.Map;

@Component("filmDbStorage")
public class FilmDbStorage implements FilmStorage {
    private final FilmsRepository filmsRepository;

    @Autowired
    public FilmDbStorage(FilmsRepository filmsRepository) {
        this.filmsRepository = filmsRepository;
    }

    @Override
    public Film create(Film film) {
        return filmsRepository.save(film);
    }

    @Override
    public Film update(Film film) throws FilmNotFoundException {
        if (filmsRepository.exists(film.getId())) {
            return filmsRepository.update(film);
        } else {
            throw new FilmNotFoundException("Фильм с id = " + film.getId() + " не найден!");
        }
    }

    public void delete(Long id) {
        filmsRepository.delete(id);
    }

    @Override
    public Film get(Long id) throws FilmNotFoundException {
        if (filmsRepository.exists(id)) {
            return filmsRepository.findOne(id);
        } else {
            throw new FilmNotFoundException("Фильм с id = " + id + " не найден!");
        }
    }

    @Override
    public Map<Long, Film> getAll() {
        Map<Long, Film> allFilms = new HashMap<>();
        filmsRepository.findAll()
                .forEach(film -> allFilms.put(film.getId(), film));
        return allFilms;
    }
}
