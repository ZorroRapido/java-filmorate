package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Validation;
import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@AutoConfigureMockMvc
public class UserValidationTests {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mock;

    @Test
    void createUserEmptyRequestTest() throws Exception {
        mock.perform(
                post("/users")
                        .content(objectMapper.writeValueAsString("{}"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createUserInvalidLoginTest() throws Exception {
        User user = new User("mail@mail.ru", "Nick Name", "Ivan", LocalDate.of(1994, 10, 13));

        mock.perform(
                post("/users")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> result.getResolvedException().getClass().equals(Validation.class));
    }

    @Test
    void createUserNullLoginTest() throws Exception {
        User user = new User("mail@mail.ru", null, "Ivan", LocalDate.of(1994, 10, 13));

        mock.perform(
                post("/users")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> result.getResolvedException().getClass().equals(Validation.class));
    }

    @Test
    void createUserBlankLoginTest() throws Exception {
        User user = new User("mail@mail.ru", "", "Ivan", LocalDate.of(1994, 10, 13));

        mock.perform(
                post("/users")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> result.getResolvedException().getClass().equals(Validation.class));
    }

    @Test
    void createUserInvalidEmailTest() throws Exception {
        User user = new User("test.mail.ru", "ivan", "Ivan", LocalDate.of(1994, 10, 13));

        mock.perform(
                post("/users")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> result.getResolvedException().getClass().equals(Validation.class));
    }

    @Test
    void createUserNullEmailTest() throws Exception {
        User user = new User(null, "ivan", "Ivan", LocalDate.of(1994, 10, 13));

        mock.perform(
                post("/users")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> result.getResolvedException().getClass().equals(Validation.class));
    }

    @Test
    void createUserBlankEmailTest() throws Exception {
        User user = new User("", "ivan", "Ivan", LocalDate.of(1994, 10, 13));

        mock.perform(
                post("/users")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> result.getResolvedException().getClass().equals(Validation.class));
    }

    @Test
    void createUserInvalidBirthdayTest() throws Exception {
        User user = new User("mail@mail.ru", "ivan", "Ivan", LocalDate.now().plusDays(1));

        mock.perform(
                post("/users")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> result.getResolvedException().getClass().equals(Validation.class));
    }

    @Test
    void createUserNullBirthdayTest() throws Exception {
        User user = new User("mail@mail.ru", "ivan", "Ivan", null);

        mock.perform(
                post("/users")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> result.getResolvedException().getClass().equals(Validation.class));
    }

    @Test
    void createUserEmptyNameTest() throws Exception {
        User user = new User("mail@mail.ru", "ivan", "", LocalDate.of(1994, 10, 13));

        mock.perform(
                post("/users")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("ivan"));
    }

    @Test
    void createUserNullNameTest() throws Exception {
        User user = new User("mail@mail.ru", "ivan", null, LocalDate.of(1994, 10, 13));

        mock.perform(
                post("/users")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("ivan"));
    }
}
