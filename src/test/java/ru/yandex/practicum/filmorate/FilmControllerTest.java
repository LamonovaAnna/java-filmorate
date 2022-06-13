package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;


public class FilmControllerTest {
    private final FilmController filmController;
    private final InMemoryUserStorage userStorage = new InMemoryUserStorage();

    public FilmControllerTest() {
        this.filmController = new FilmController(new FilmService(new InMemoryFilmStorage(), userStorage));
    }

    @Test
    void test1_createFilmWithCorrectParameters() throws ValidationException {
        Film film = new Film("test", "desc", LocalDate.of(2022, 1,12),
                300);

        filmController.createFilm(film);
        film.setId(1);
        Film filmFromLibrary = filmController.getFilms().get(0);

        assertNotNull(filmFromLibrary, "Film not found");
        assertEquals(film, filmFromLibrary, "Films don't match");
        assertEquals(1, filmController.getFilms().size(), "Invalid number of films");
    }

    @MethodSource("test2MethodSource")
    @ParameterizedTest
    void test2_createFilmWithIncorrectParameters(Film film, String message) {

        final ValidationException exception = assertThrows(ValidationException.class,
                () -> filmController.createFilm(film));
        assertEquals(message, exception.getMessage(), "Incorrect message");
        assertThrows(ValidationException.class, () -> filmController.createFilm(film), "Incorrect exception");
        assertEquals(0, filmController.getFilms().size(), "Incorrect number of films");
    }

    private static Stream<Arguments> test2MethodSource() {
        return Stream.of(
                Arguments.of(new Film("", "desc",
                                LocalDate.of(2022, 1,12),300),
                                "Incorrect parameter: name"),
                        Arguments.of(new Film("test", "In a small town where everyone knows " +
                                "everyone, a peculiar incident starts a chain of events that leads to a child's " +
                                "disappearance, which begins to tear at the fabric of an otherwise-peaceful " +
                                "community. ", LocalDate.of(2022, 1,12),
                                300),
                                "Incorrect parameter: description. Description is too long"),
                        Arguments.of(new Film("test", "desc",
                                LocalDate.of(1822, 1,12),300),
                                "Incorrect parameter: release date"),
                        Arguments.of(new Film("test", "desc",
                                LocalDate.of(2022, 1,12),-100),
                                "Incorrect parameter: duration")
        );
    }

    @Test
    void test3_updateFilmWithCorrectId() throws ValidationException {
        final Film film = new Film("test", "desc", LocalDate.of(2022, 1,12),
                300);

        filmController.createFilm(film);
        Film filmFromLibrary = (Film) filmController.getFilms().get(0);
        filmFromLibrary.setName("updateName");
        filmController.updateFilm(filmFromLibrary);

        assertEquals(filmFromLibrary.getName(), ((Film) filmController.getFilms().get(0)).getName(),
                "Incorrect film name");
        assertEquals(1, filmController.getFilms().size(), "Invalid number of films");
    }

    @Test
    void test4_updateFilmWithIncorrectId() throws ValidationException {
        final Film film = new Film("test", "desc", LocalDate.of(2022, 1,12),
                300);

        filmController.createFilm(film);
        Film filmFromLibrary = filmController.getFilms().get(0);
        filmFromLibrary.setName("updateName");
        filmFromLibrary.setId(-10);

        final FilmNotFoundException exception = assertThrows(FilmNotFoundException.class,
                () -> filmController.updateFilm(filmFromLibrary));
        assertEquals("Film with id -10 not found", exception.getMessage(), "Incorrect message");
        assertThrows(FilmNotFoundException.class, () -> filmController.updateFilm(filmFromLibrary),
                "Incorrect exception");
        assertEquals(1, filmController.getFilms().size(), "Incorrect number of films");
    }

    @Test
    void test5_updateFilmWithoutId() throws ValidationException {
        final Film film = new Film("test", "desc", LocalDate.of(2022, 1,12),
                300);

        filmController.createFilm(film);
        Film filmFromLibrary = filmController.getFilms().get(0);
        filmFromLibrary.setName("updateName");
        filmFromLibrary.setId(0);
        filmController.updateFilm(filmFromLibrary);

        assertEquals(2, filmController.getFilms().size(), "Invalid number of films");
    }

    @Test
    void test6_getFilms() throws ValidationException {
        final Film film = new Film("test", "desc", LocalDate.of(2022, 1,12),
                300);
        filmController.createFilm(film);
        film.setId(1);

        Film[] expectedFilms = {film};

        assertNotNull(filmController.getFilms(), "List wasn't returned");
        assertArrayEquals(expectedFilms, filmController.getFilms().toArray(new Film[0]),
                "Arrays aren't match");
        assertEquals(1, filmController.getFilms().size(), "Invalid number of films");
    }

    @Test
    void test7_findFilmById() throws ValidationException {
        filmController.createFilm(new Film("test", "desc",
                LocalDate.of(2022, 1,12),300));
        filmController.createFilm(new Film("test2", "desc",
                LocalDate.of(2022, 1,23),8900));

        Film returnedFilm = filmController.findFilmById(2);

        assertEquals(2, returnedFilm.getId(), "Incorrect film id");
        assertEquals(8900, returnedFilm.getDuration(), "Incorrect film duration");
        assertEquals("test2", returnedFilm.getName(), "Incorrect film name");
    }

    @Test
    void test8_findFilmByIncorrectId() throws ValidationException {
        filmController.createFilm(new Film("test", "desc",
                LocalDate.of(2022, 1,12),300));

        final FilmNotFoundException exception = assertThrows(FilmNotFoundException.class,
                () -> filmController.findFilmById(10));
        assertEquals("Film with id 10 not found", exception.getMessage(), "Incorrect message");
        assertThrows(FilmNotFoundException.class, () -> filmController.findFilmById(10),
                "Incorrect exception");
    }

    @Test
    void test9_addLike() throws ValidationException {
        filmController.createFilm(new Film("test", "desc",
                LocalDate.of(2022, 1,12),300));
        userStorage.createUser(new User("test@yandex.ru", "test",
                LocalDate.of(1990, 1,15)));
        userStorage.createUser(new User("test2@yandex.ru", "test2",
                LocalDate.of(1991, 1,15)));

        filmController.addLike(1, 1);
        filmController.addLike(1, 2);

        Long[] expectedList = {1L, 2L};

        assertEquals(2, filmController.findFilmById(1).getLikes().size(), "Incorrect list size");
        assertArrayEquals(expectedList, filmController.findFilmById(1).getLikes().toArray(),
                "Arrays don't match");
    }

    @Test
    void test10_addLikeToIncorrectFilm() throws ValidationException {
        filmController.createFilm(new Film("test", "desc",
                LocalDate.of(2022, 1,12),300));
        userStorage.createUser(new User("test@yandex.ru", "test",
                LocalDate.of(1990, 1,15)));

        final FilmNotFoundException exception = assertThrows(FilmNotFoundException.class,
                () -> filmController.addLike(10,1));
        assertEquals("Film with id 10 not found", exception.getMessage(), "Incorrect message");
        assertThrows(FilmNotFoundException.class, () -> filmController.addLike(10,1),
                "Incorrect exception");
    }

    @Test
    void test11_addLikeByIncorrectUser() throws ValidationException {
        filmController.createFilm(new Film("test", "desc",
                LocalDate.of(2022, 1,12),300));
        userStorage.createUser(new User("test@yandex.ru", "test",
                LocalDate.of(1990, 1,15)));

        final UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> filmController.addLike(1,10));
        assertEquals("User with id 10 wasn't found", exception.getMessage(), "Incorrect message");
        assertThrows(UserNotFoundException.class, () -> filmController.addLike(1,10),
                "Incorrect exception");
        assertTrue(filmController.findFilmById(1).getLikes().isEmpty(), "Incorrect number of likes");
    }
}