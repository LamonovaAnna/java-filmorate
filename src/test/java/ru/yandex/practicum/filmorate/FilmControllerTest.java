package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;

import java.time.LocalDate;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;


public class FilmControllerTest {
    private final FilmController filmController;

    public FilmControllerTest() {
        this.filmController = new FilmController(new InMemoryFilmStorage());
    }

    @Test
    void test1_createFilmWithCorrectParameters() throws ValidationException {
        Film film = new Film("test", "desc", LocalDate.of(2022, 1,12),
                300);

        filmController.createFilm(film);
        film.setId(1);
        Film filmFromLibrary = (Film) filmController.getFilms().get(0);

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
                                "Incorrect film name"),
                        Arguments.of(new Film("test", "In a small town where everyone knows " +
                                "everyone, a peculiar incident starts a chain of events that leads to a child's " +
                                "disappearance, which begins to tear at the fabric of an otherwise-peaceful " +
                                "community. ", LocalDate.of(2022, 1,12),
                                300),
                                "Description is too long"),
                        Arguments.of(new Film("test", "desc",
                                LocalDate.of(1822, 1,12),300),
                                "Incorrect Release date"),
                        Arguments.of(new Film("test", "desc",
                                LocalDate.of(2022, 1,12),-100),
                                "Incorrect duration")
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
        Film filmFromLibrary = (Film) filmController.getFilms().get(0);
        filmFromLibrary.setName("updateName");
        filmFromLibrary.setId(-10);

        final ValidationException exception = assertThrows(ValidationException.class,
                () -> filmController.updateFilm(filmFromLibrary));
        assertEquals("Incorrect id", exception.getMessage(), "Incorrect message");
        assertThrows(ValidationException.class, () -> filmController.updateFilm(filmFromLibrary),
                "Incorrect exception");
        assertEquals(1, filmController.getFilms().size(), "Incorrect number of films");
    }

    @Test
    void test5_updateFilmWithoutId() throws ValidationException {
        final Film film = new Film("test", "desc", LocalDate.of(2022, 1,12),
                300);

        filmController.createFilm(film);
        Film filmFromLibrary = (Film) filmController.getFilms().get(0);
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
}