package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class FilmoRateApplicationTests {
    private final UserDbStorage userStorage;
    private final FilmDbStorage filmStorage;

    @Test
    public void testCreateAndGetUser() throws UserNotFoundException {
        User testUser = createUser();

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
    public void testUpdateUser() throws UserNotFoundException {
        User testUser = createUser();
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
    public void testDeleteUser() throws UserNotFoundException {
        User testUser = createUser();
        userStorage.create(testUser);

        userStorage.delete(testUser);

        String expectedErrorMessage = "Пользователь с id = " + testUser.getId() + " не найден!";
        Exception exception = assertThrows(UserNotFoundException.class, () -> userStorage.get(testUser.getId()));
        assertThat(exception.getMessage())
                .isEqualTo(expectedErrorMessage);
    }

    @Test
    public void testGetAllUsers() {
        User testUser1 = createUser();
        User testUser2 = createUser();

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
    public void testCreateAndGetFilm() throws FilmNotFoundException {
        Film testFilm = createFilm();

        filmStorage.create(testFilm);
        Optional<Film> filmOptional = Optional.ofNullable(filmStorage.get(testFilm.getId()));

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film -> {
                            assertThat(film).hasFieldOrPropertyWithValue("id", testFilm.getId());
                            assertThat(film).hasFieldOrPropertyWithValue("name", testFilm.getName());
                            assertThat(film).hasFieldOrPropertyWithValue("description", testFilm.getDescription());
                            assertThat(film).hasFieldOrPropertyWithValue("releaseDate", testFilm.getReleaseDate());
                            assertThat(film).hasFieldOrPropertyWithValue("duration", testFilm.getDuration());
                            assertThat(film).hasFieldOrPropertyWithValue("rate", testFilm.getRate());
                            assertThat(film).hasFieldOrPropertyWithValue("mpa", testFilm.getMpa());
                        }
                );
    }

    @Test
    public void testUpdateFilm() throws FilmNotFoundException {
        Film testFilm = createFilm();
        filmStorage.create(testFilm);

        testFilm.setName("Film Updated");
        testFilm.setReleaseDate(LocalDate.of(1989, 4, 17));
        testFilm.setDescription("mail@yandex.ru");
        testFilm.setDuration(190);
        testFilm.setRate(4);

        filmStorage.update(testFilm);

        Optional<Film> filmOptional = Optional.ofNullable(filmStorage.get(testFilm.getId()));

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film -> {
                            assertThat(film).hasFieldOrPropertyWithValue("id", testFilm.getId());
                            assertThat(film).hasFieldOrPropertyWithValue("name", testFilm.getName());
                            assertThat(film).hasFieldOrPropertyWithValue("description", testFilm.getDescription());
                            assertThat(film).hasFieldOrPropertyWithValue("releaseDate", testFilm.getReleaseDate());
                            assertThat(film).hasFieldOrPropertyWithValue("duration", testFilm.getDuration());
                            assertThat(film).hasFieldOrPropertyWithValue("rate", testFilm.getRate());
                            assertThat(film).hasFieldOrPropertyWithValue("mpa", testFilm.getMpa());
                        }
                );
    }

    @Test
    public void testDeleteFilm() throws FilmNotFoundException {
        Film testFilm = createFilm();
        filmStorage.create(testFilm);

        filmStorage.delete(testFilm.getId());

        String expectedErrorMessage = "Фильм с id = " + testFilm.getId() + " не найден!";
        Exception exception = assertThrows(FilmNotFoundException.class, () -> filmStorage.get(testFilm.getId()));
        assertThat(exception.getMessage())
                .isEqualTo(expectedErrorMessage);
    }

    @Test
    public void testGetAllFilms() {
        Film testFilm1 = createFilm();
        Film testFilm2 = createFilm();

        testFilm2.setName("New film");
        testFilm2.setDescription("New film about friends");

        filmStorage.create(testFilm1);
        filmStorage.create(testFilm2);

        Collection<Film> films = filmStorage.getAll().values();

        assertAll(
                () -> assertThat(films.size()).isEqualTo(2),
                () -> assertThat(testFilm1).isIn(films),
                () -> assertThat(testFilm2).isIn(films)
        );
    }


    private User createUser() {
        return User.builder()
                .login("dolore")
                .name("Nick Name")
                .email("mail@mail.ru")
                .friends(new HashSet<>())
                .likedFilms(new HashSet<>())
                .birthday(LocalDate.of(1946, 8, 20))
                .build();
    }

    private Film createFilm() {
        return Film.builder()
                .name("nisi eiusmod")
                .description("adipisicing")
                .releaseDate(LocalDate.of(1967, 3, 25))
                .duration(100)
                .rate(0)
                .mpa(Mpa.builder()
                        .id(1L)
                        .build())
                .build();
    }
}