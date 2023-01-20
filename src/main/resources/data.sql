-- -- Наполнение таблицы users
--
-- INSERT INTO USERS (EMAIL, LOGIN, NAME, BIRTHDAY)
-- VALUES ('john@mail.ru', 'john', 'John', '2002-02-20'),
--        ('ann@mail.ru', 'ann', 'Ann', '2001-07-10'),
--        ('max@mail.ru', 'max', 'Max', '2000-08-15');
--
-- -- Наполнение таблицы friendship
--
-- INSERT INTO FRIENDSHIP (ID, FRIEND_ID, STATUS)
-- VALUES (1, 2, 'confirmed'),
--        (2, 1, 'confirmed'),
--        (1, 3, 'unconfirmed'),
--        (3, 1, 'unconfirmed'),
--        (2, 3, 'confirmed'),
--        (3, 2, 'confirmed');
--
-- -- Наполнение таблицы films
--
-- INSERT INTO FILMS (NAME, DESCRIPTION, RELEASE_DATE, DURATION, RATE)
-- VALUES ('Чебурашка', '-', '2023-01-01', 113, 8),
--        ('Операция "Фортуна": Искусство побеждать', '-', '2023-01-03', 114, 7),
--        ('Мира', 'Для семьи космос не расстояние', '2022-12-22', 116, 7);
--
-- -- Наполнение таблицы films_genre
--
-- INSERT INTO FILMS_GENRE (FILM_ID, GENRE_ID)
-- VALUES (1, 1), -- Чебурашка - Комедия
--        (1, 6), -- Чебурашка - Семейный
--        (2, 6), -- Операция "Фортуна" - Боевик
--        (2, 4), -- Операция "Фортуна" - Триллер
--        (2, 1), -- Операция "Фортуна" - Комедия
--        (3, 2), -- Мира - Драма
--        (3, 8), -- Мира - Фантастика
--        (3, 9); -- Мира - Приключения
--
-- Наполнение таблицы genre

INSERT INTO GENRES (NAME)
VALUES ('Комедия'),
       ('Драма'),
       ('Мультфильм'),
       ('Триллер'),
       ('Документальный'),
       ('Боевик');

-- Наполнение таблицы mpa

INSERT INTO MPA (NAME)
VALUES ('G'),
       ('PG'),
       ('PG-13'),
       ('R'),
       ('NC-17');