package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class UserTests {
    private final UserDbStorage userStorage;

    @Test
    public void testGetUserPositiveCase() throws UserNotFoundException {
        User testUser = buildUser();

        userStorage.create(testUser);
        Optional<User> userOptional = Optional.ofNullable(userStorage.get(testUser.getId()));

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user -> {
                            assertThat(user).hasFieldOrPropertyWithValue("id", testUser.getId());
                            assertThat(user).hasFieldOrPropertyWithValue("login", testUser.getLogin());
                            assertThat(user).hasFieldOrPropertyWithValue("name", testUser.getName());
                            assertThat(user).hasFieldOrPropertyWithValue("email", testUser.getEmail());
                            assertThat(user).hasFieldOrPropertyWithValue("birthday", testUser.getBirthday());
                        }
                );
    }

    @Test
    public void testGetNonexistentUser() {
        Long id = 1L;

        String expectedErrorMessage = "Пользователь с id = " + id + " не найден!";
        Exception exception = assertThrows(UserNotFoundException.class, () -> userStorage.get(id));
        assertThat(exception.getMessage())
                .isEqualTo(expectedErrorMessage);
    }

    @Test
    public void testUpdateUserPositiveCase() {
        User testUser = buildUser();
        userStorage.create(testUser);

        testUser.setLogin("doloreUpdate");
        testUser.setName("doloreUpdate");
        testUser.setEmail("mail@yandex.ru");
        testUser.setBirthday(LocalDate.of(1976, 9, 20));

        userStorage.update(testUser);

        Optional<User> userOptional = Optional.ofNullable(userStorage.get(testUser.getId()));

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user -> {
                            assertThat(user).hasFieldOrPropertyWithValue("id", testUser.getId());
                            assertThat(user).hasFieldOrPropertyWithValue("login", testUser.getLogin());
                            assertThat(user).hasFieldOrPropertyWithValue("name", testUser.getName());
                            assertThat(user).hasFieldOrPropertyWithValue("email", testUser.getEmail());
                            assertThat(user).hasFieldOrPropertyWithValue("birthday", testUser.getBirthday());
                        }
                );
    }

    @Test
    public void testUpdateNonexistentUser() {
        Long id = 1L;

        User updatedUser = buildUser();
        updatedUser.setId(id);

        String expectedErrorMessage = "Пользователь с id = " + id + " не найден!";
        Exception exception = assertThrows(UserNotFoundException.class, () -> userStorage.update(updatedUser));
        assertThat(exception.getMessage())
                .isEqualTo(expectedErrorMessage);
    }

    @Test
    public void testDeleteUser() throws UserNotFoundException {
        User testUser = buildUser();
        userStorage.create(testUser);

        userStorage.delete(testUser);

        String expectedErrorMessage = "Пользователь с id = " + testUser.getId() + " не найден!";
        Exception exception = assertThrows(UserNotFoundException.class, () -> userStorage.get(testUser.getId()));
        assertThat(exception.getMessage())
                .isEqualTo(expectedErrorMessage);
    }

    @Test
    public void testGetAllUsersPositiveCase() {
        User testUser1 = buildUser();
        User testUser2 = buildUser();

        testUser2.setLogin("dolore2");
        testUser2.setName("Nick Name2");

        userStorage.create(testUser1);
        userStorage.create(testUser2);

        Collection<User> users = userStorage.getAll().values();

        assertAll(
                () -> assertThat(users.size()).isEqualTo(2),
                () -> assertThat(testUser1).isIn(users),
                () -> assertThat(testUser2).isIn(users)
        );
    }

    @Test
    public void testGetAllUsersEmptyList() {
        Collection<User> users = userStorage.getAll().values();

        assertThat(users.size())
                .isEqualTo(0);
    }

    private User buildUser() {
        return User.builder()
                .login("dolore")
                .name("Nick Name")
                .email("mail@mail.ru")
                .friends(new HashSet<>())
                .likedFilms(new HashSet<>())
                .birthday(LocalDate.of(1946, 8, 20))
                .build();
    }
}