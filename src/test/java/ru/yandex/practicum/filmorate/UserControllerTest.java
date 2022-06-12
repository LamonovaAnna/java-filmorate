package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserControllerTest {
    private final UserController userController;

    public UserControllerTest() {
        this.userController = new UserController();
    }

    @Test
    void test1_createUserWithCorrectParameters() throws ValidationException {
        User user = new User("test@yandex.ru", "test", LocalDate.of(1990, 1,15));

        userController.createUser(user);
        user.setId(1);
        User userFromLibrary = (User) userController.getUsers().get(0);

        assertNotNull(userFromLibrary, "User not found");
        assertEquals(user, userFromLibrary, "User's don't match");
        assertEquals(1, userController.getUsers().size(), "Invalid number of users");
    }

    @MethodSource("test2MethodSource")
    @ParameterizedTest
    void test2_createUserWithIncorrectParameters(User user, String message) {

        final ValidationException exception = assertThrows(ValidationException.class,
                () -> userController.createUser(user));
        assertEquals(message, exception.getMessage(), "Incorrect message");
        assertThrows(ValidationException.class, () -> userController.createUser(user), "Incorrect exception");
        assertEquals(0, userController.getUsers().size(), "Incorrect number of films");
    }

    private static Stream<Arguments> test2MethodSource() {
        return Stream.of(
                Arguments.of(new User("", "test",
                                LocalDate.of(1990, 1,15)),
                        "Incorrect email"),
                Arguments.of(new User("test.yandex.ru", "test",
                                LocalDate.of(1990, 1,15)),
                        "Incorrect email"),
                Arguments.of(new User("test@yandex.ru", "",
                                LocalDate.of(1990, 1,15)),
                        "Incorrect login"),
                Arguments.of(new User("test@yandex.ru", "test",
                                LocalDate.of(2023, 1,15)),
                        "Incorrect birthday")
        );
    }

    @Test
    void test3_updateUserWithCorrectId() throws ValidationException {
        User user = new User("test@yandex.ru", "test", LocalDate.of(1990, 1,15));

        userController.createUser(user);
        User userFromLibrary = (User) userController.getUsers().get(0);
        userFromLibrary.setName("testName");
        userController.updateUser(userFromLibrary);

        assertEquals("testName", ((User) userController.getUsers().get(0)).getName(),
                "Incorrect user name");
        assertEquals(userFromLibrary.getName(), ((User) userController.getUsers().get(0)).getName(),
                "Incorrect user name");
        assertEquals(1, userController.getUsers().size(), "Invalid number of users");
    }

    @Test
    void test4_updateUserWithIncorrectId() throws ValidationException {
        User user = new User("test@yandex.ru", "test", LocalDate.of(1990, 1,15));

        userController.createUser(user);
        User userFromLibrary = (User) userController.getUsers().get(0);
        userFromLibrary.setName("updateName");
        userFromLibrary.setId(-10);

        final ValidationException exception = assertThrows(ValidationException.class,
                () -> userController.updateUser(userFromLibrary));
        assertEquals("Incorrect id", exception.getMessage(), "Incorrect message");
        assertThrows(ValidationException.class, () -> userController.updateUser(userFromLibrary),
                "Incorrect exception");
        assertEquals(1, userController.getUsers().size(), "Incorrect number of users");
    }

    @Test
    void test5_updateUserWithoutId() throws ValidationException {
        User user = new User("test@yandex.ru", "test", LocalDate.of(1990, 1,15));

        userController.createUser(user);
        User userFromLibrary = (User) userController.getUsers().get(0);
        userFromLibrary.setName("updateName");
        userFromLibrary.setId(0);
        userController.updateUser(userFromLibrary);

        assertEquals(2, userController.getUsers().size(), "Invalid number of users");
    }

    @Test
    void test6_getUsers() throws ValidationException {
        User user = new User("test@yandex.ru", "test", LocalDate.of(1990, 1,15));
        userController.createUser(user);
        user.setId(1);

        User[] expectedUsers = {user};

        assertNotNull(userController.getUsers(), "List wasn't returned");
        assertArrayEquals(expectedUsers, userController.getUsers().toArray(new User[0]),
                "Arrays aren't match");
        assertEquals(1, userController.getUsers().size(), "Invalid number of users");
    }
}