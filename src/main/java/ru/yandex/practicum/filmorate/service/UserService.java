package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.FriendshipRepository;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.LinkedHashSet;

@Service
@Slf4j
public class UserService {
    private final UserStorage userStorage;
    private final FriendshipRepository friendshipRepository;

    @Autowired
    public UserService(@Qualifier("userDbStorage") UserStorage userStorage, FriendshipRepository friendshipRepository) {
        this.userStorage = userStorage;
        this.friendshipRepository = friendshipRepository;
    }

    public User create(User user, BindingResult errors) throws ValidationException {
        validate(user, errors);
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        return userStorage.create(user);
    }

    public User update(User user, BindingResult errors) throws UserNotFoundException, ValidationException {
        if (!userStorage.getAll().containsKey(user.getId())) {
            log.warn("Пользователь с id = " + user.getId() + " не найден!");
            throw new UserNotFoundException("Пользователь с id = " + user.getId() + " не найден!");
        }
        validate(user, errors);
        userStorage.update(user);
        return user;
    }

    public Collection<User> findAll() {
        return userStorage.getAll().values();
    }

    public User find(Long id) throws UserNotFoundException {
        return userStorage.get(id);
    }

    public void addFriend(Long id, Long friendId) throws UserNotFoundException {
        if (userStorage.getAll().containsKey(friendId)) {
            friendshipRepository.save(Friendship.builder()
                    .id(id)
                    .friendId(friendId)
                    .status("неподтвержденная")
                    .build());

            if (friendshipRepository.exists(friendId, id)) {
                friendshipRepository.update(Friendship.builder()
                        .id(id)
                        .friendId(friendId)
                        .status("подтвержденная")
                        .build());

                friendshipRepository.update(Friendship.builder()
                        .id(friendId)
                        .friendId(id)
                        .status("подтвержденная")
                        .build());
            }
        } else {
            log.warn("Пользователь с id = " + friendId + " не найден!");
            throw new UserNotFoundException("Пользователь с id = " + friendId + " не найден!");
        }
    }

    public void removeFriend(Long id, Long friendId) {
        friendshipRepository.delete(id, friendId);
    }

    public Set<User> findFriends(Long id) throws UserNotFoundException {
        Set<User> friends = new LinkedHashSet<>();
        Set<Long> friendsIds = friendshipRepository.findFriendsIds(id);

        for (Long friendId : friendsIds) {
            friends.add(userStorage.get(friendId));
        }

        return friends;
    }

    public List<User> findCommonFriends(Long id, Long otherId) throws UserNotFoundException {
        Set<User> firstUserFriends = new HashSet<>(findFriends(id));
        Set<User> secondUserFriends = new HashSet<>(findFriends(otherId));

        firstUserFriends.retainAll(secondUserFriends);

        return new ArrayList<>(firstUserFriends);
    }

    private void validate(User user, BindingResult errors) throws ValidationException {
        if (user.getLogin().contains(" ")) {
            log.warn("Неверный формат login! Поле не должно содержать пробелы!");
            throw new ValidationException("Неверный формат login! Поле не должно содержать пробелы!");
        }

        if (errors.hasFieldErrors()) {
            FieldError error = errors.getFieldErrors().get(0);
            log.warn(String.format("Неверный формат %s! Поле %s!", error.getField(), error.getDefaultMessage()));
            throw new ValidationException(String.format("Неверный формат %s! Поле %s!", error.getField(),
                    error.getDefaultMessage()));
        }
    }
}
