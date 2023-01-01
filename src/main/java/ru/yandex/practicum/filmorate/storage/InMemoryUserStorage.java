package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component("inMemoryUserStorage")
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private Long counter = 0L;

    @Override
    public User create(User user) {
        user.setId(getNextId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public void delete(User user) {
        users.remove(user.getId());
    }

    @Override
    public User get(Long id) throws UserNotFoundException {
        User user = users.get(id);
        if (user == null) {
            log.warn("Пользователь с id = " + id + " не найден!");
            throw new UserNotFoundException("Пользователь с id = " + id + " не найден!");
        }
        return user;
    }

    @Override
    public Map<Long, User> getAll() {
        return users;
    }

    private Long getNextId() {
        counter++;
        return counter;
    }
}
