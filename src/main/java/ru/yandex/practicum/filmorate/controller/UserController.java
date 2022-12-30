package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    private User create(@Valid @RequestBody User user, BindingResult errors) throws ValidationException {
        return userService.create(user, errors);
    }

    @PutMapping
    private User update(@Valid @RequestBody User user,
                        BindingResult errors) throws UserNotFoundException, ValidationException {
        return userService.update(user, errors);
    }

    @GetMapping
    private Collection<User> findAll() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    private User find(@PathVariable(name = "id") Long id) throws UserNotFoundException {
        return userService.find(id);
    }

    @PutMapping("/{id}/friends/{friendId}")
    private void addFriend(@PathVariable(name = "id") Long id,
                           @PathVariable(name = "friendId") Long friendId) throws UserNotFoundException {
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    private void removeFriend(@PathVariable(name = "id") Long id,
                              @PathVariable(name = "friendId") Long friendId) throws UserNotFoundException {
        userService.removeFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    private List<User> findFriends(@PathVariable(name = "id") Long id) throws UserNotFoundException {
        return userService.findFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    private List<User> findCommonFriends(@PathVariable("id") Long id,
                                         @PathVariable("otherId") Long otherId) throws UserNotFoundException {
        return userService.findCommonFriends(id, otherId);
    }
}
