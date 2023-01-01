package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Map;

public interface UserStorage {
    User create(User user);

    User update(User user);

    void delete(User user);

    User get(Long id) throws UserNotFoundException;

    Map<Long, User> getAll();
}
