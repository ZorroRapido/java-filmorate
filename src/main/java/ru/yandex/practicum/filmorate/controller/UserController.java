package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/users")
public class UserController {
    private Map<Integer, User> users = new HashMap<>();
    private int counter = 1;

    @PostMapping
    private User create(@Valid @RequestBody User user) throws ValidationException {
        validate(user);

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        user.setId(counter);
        users.put(user.getId(), user);
        counter++;
        return user;
    }

    @PutMapping
    private User update(@Valid @RequestBody User user) throws UserNotFoundException, ValidationException {
        if (!users.containsKey(user.getId())) {
            log.warn("Пользователь с id = " + user.getId() + " не найден!");
            throw new UserNotFoundException("Пользователь с id = " + user.getId() + " не найден!");
        }
        validate(user);
        users.put(user.getId(), user);
        return user;
    }

    @GetMapping
    private Collection<User> findAll() {
        return users.values();
    }

    private void validate(User user) throws ValidationException {
        if (user.getLogin().contains(" ")) {
            log.warn("Логин указан некорректно!");
            throw new ValidationException("Логин указан некорректно!");
        }
    }
}
