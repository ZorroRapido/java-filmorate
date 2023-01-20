package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmDbStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class FilmTests {
    private final FilmDbStorage filmStorage;

    @Test
    public void testGetFilmPositiveCase() throws FilmNotFoundException {
        Film testFilm = buildFilm();

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
    public void testGetNonexistentFilm() throws FilmNotFoundException {
        Long id = 1L;

        String expectedErrorMessage = "Фильм с id = " + id + " не найден!";
        Exception exception = assertThrows(FilmNotFoundException.class, () -> filmStorage.get(id));
        assertThat(exception.getMessage())
                .isEqualTo(expectedErrorMessage);
    }

    @Test
    public void testUpdateFilmPositiveCase() throws FilmNotFoundException {
        Film testFilm = buildFilm();
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
    public void testUpdateNonexistentFilm() {
        Long id = 1L;

        Film updatedFilm = buildFilm();
        updatedFilm.setId(id);

        String expectedErrorMessage = "Фильм с id = " + id + " не найден!";
        Exception exception = assertThrows(FilmNotFoundException.class, () -> filmStorage.update(updatedFilm));
        assertThat(exception.getMessage())
                .isEqualTo(expectedErrorMessage);
    }

    @Test
    public void testDeleteFilm() throws FilmNotFoundException {
        Film testFilm = buildFilm();
        filmStorage.create(testFilm);

        filmStorage.delete(testFilm.getId());

        String expectedErrorMessage = "Фильм с id = " + testFilm.getId() + " не найден!";
        Exception exception = assertThrows(FilmNotFoundException.class, () -> filmStorage.get(testFilm.getId()));
        assertThat(exception.getMessage())
                .isEqualTo(expectedErrorMessage);
    }

    @Test
    public void testGetAllFilmsPositiveCase() {
        Film testFilm1 = buildFilm();
        Film testFilm2 = buildFilm();

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

    @Test
    public void testGetAllFilmsEmptyList() {
        Collection<Film> films = filmStorage.getAll().values();

        assertThat(films.size())
                .isEqualTo(0);
    }

    private Film buildFilm() {
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