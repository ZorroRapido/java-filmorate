package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.UsersRepository;

import java.util.HashMap;
import java.util.Map;

@Component("userDbStorage")
public class UserDbStorage implements UserStorage {
    private final UsersRepository usersRepository;

    @Autowired
    public UserDbStorage(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    @Override
    public User create(User user) {
        return usersRepository.save(user);
    }

    @Override
    public User update(User user) {
        return usersRepository.update(user);
    }

    @Override
    public void delete(User user) {
        usersRepository.delete(user.getId());
    }

    @Override
    public User get(Long id) throws UserNotFoundException {
        if (usersRepository.exists(id)) {
            return usersRepository.findOne(id);
        } else {
            throw new UserNotFoundException("Пользователь с id = " + id + " не найден!");
        }
    }

    @Override
    public Map<Long, User> getAll() {
        Map<Long, User> allUsers = new HashMap<>();
        usersRepository.findAll()
                .forEach(user -> allUsers.put(user.getId(), user));
        return allUsers;
    }
}
