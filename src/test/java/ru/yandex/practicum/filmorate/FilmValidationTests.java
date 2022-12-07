package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Validation;
import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest
@AutoConfigureWebMvc
@TestMethodOrder(MethodOrderer.MethodName.class)
public class FilmValidationTests {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mock;

    private static int counter = 1;

    @Test
    void createFilmEmptyRequestTest() throws Exception {
        mock.perform(
                post("/films")
                        .content(objectMapper.writeValueAsString("{}"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createFilmNullNameTest() throws Exception {
        Film film = new Film(null, "test_description", LocalDate.now().plusYears(1), 90);

        mock.perform(
                post("/films")
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> result.getResolvedException().getClass().equals(Validation.class));
    }

    @Test
    void createFilmEmptyNameTest() throws Exception {
        Film film = new Film("", "test_description", LocalDate.now().plusYears(1), 90);

        mock.perform(
                post("/films")
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> result.getResolvedException().getClass().equals(Validation.class));
    }

     @Order(1)
    @ParameterizedTest
    @ValueSource(ints = {199, 200})
    void createFilmValidDescriptionLengthTest(int descriptionLength) throws Exception {
        String description = "a".repeat(descriptionLength);
        Film film = new Film("The Godfather", description, LocalDate.now().plusYears(1), 90);
        film.setId(counter);
        counter++;

        mock.perform(
                post("/films")
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value(description));
    }

    @Test
    void createFilmInvalidDescriptionLengthTest() throws Exception {
        Film film = new Film("The Godfather", "a".repeat(201), LocalDate.now().plusYears(1), 90);

        mock.perform(
                post("/films")
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> result.getResolvedException().getClass().equals(Validation.class));
    }

     @Order(2)
    @ParameterizedTest
    @CsvSource({"1895-12-28", "1895-12-29"})
    void createFilmValidReleaseDateTest(String date) throws Exception {
        Film film = new Film("The Godfather", "test_description", LocalDate.parse(date), 90);
        film.setId(counter);
        counter++;

        mock.perform(
                post("/films")
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(film)));
    }

    @Test
    void createFilmInvalidReleaseDateTest() throws Exception {
        Film film = new Film("The Godfather", "test_description", LocalDate.of(1895, 12,
                27), 90);

        mock.perform(
                post("/films")
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> result.getResolvedException().getClass().equals(Validation.class));
    }

    @Test
    void createFilmZeroDurationTest() throws Exception {
        Film film = new Film("The Godfather", "test_description", LocalDate.now().plusYears(1), 0);

        mock.perform(
                post("/films")
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> result.getResolvedException().getClass().equals(Validation.class));
    }

    @Test
    void createFilmNegativeDurationTest() throws Exception {
        Film film = new Film("The Godfather", "test_description", LocalDate.now().plusYears(1), -1);

        mock.perform(
                post("/films")
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> result.getResolvedException().getClass().equals(Validation.class));
    }
}
