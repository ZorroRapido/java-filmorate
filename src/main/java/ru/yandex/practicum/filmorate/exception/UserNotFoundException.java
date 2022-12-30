package ru.yandex.practicum.filmorate.exception;

public class UserNotFoundException extends Exception {
    public UserNotFoundException(String msg) {
        super(msg);
    }
}
