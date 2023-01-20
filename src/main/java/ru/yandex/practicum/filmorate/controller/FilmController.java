package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {
    private final FilmService filmService;

    @PostMapping
    private Film create(@Valid @RequestBody Film film, BindingResult errors) throws ValidationException {
        return filmService.create(film, errors);
    }

    @PutMapping
    private Film update(@Valid @RequestBody Film film, BindingResult errors) throws ValidationException, FilmNotFoundException {
        return filmService.update(film, errors);
    }

    @GetMapping
    private Collection<Film> findAll() {
        return filmService.findAll();
    }

    @GetMapping("/{id}")
    private Film find(@PathVariable(name = "id") Long id) throws FilmNotFoundException {
        return filmService.find(id);
    }

    @PutMapping("/{id}/like/{userId}")
    private void like(@PathVariable(name = "id") Long id,
                      @PathVariable(name = "userId") Long userId) throws FilmNotFoundException, UserNotFoundException {
        filmService.like(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    private void unlike(@PathVariable(name = "id") Long id,
                        @PathVariable(name = "userId") Long userId) throws FilmNotFoundException, UserNotFoundException {
        filmService.unlike(id, userId);
    }

    @GetMapping("/popular")
    private List<Film> findMostPopular(@RequestParam(name = "count", defaultValue = "10") int count) {
        return filmService.findMostPopular(count);
    }
}
