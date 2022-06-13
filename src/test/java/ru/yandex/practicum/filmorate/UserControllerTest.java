package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserControllerTest {
    private final UserController userController;

    public UserControllerTest() {
        this.userController = new UserController(new UserService(new InMemoryUserStorage()));
    }

    @Test
    void test1_createUserWithCorrectParameters() throws ValidationException {
        User user = new User("test@yandex.ru", "test", LocalDate.of(1990, 1,15));

        userController.createUser(user);
        user.setId(1L);
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
        assertEquals(0, userController.getUsers().size(), "Incorrect number of users");
    }

    private static Stream<Arguments> test2MethodSource() {
        return Stream.of(
                Arguments.of(new User("", "test",
                                LocalDate.of(1990, 1,15)),
                        "Incorrect parameter: email"),
                Arguments.of(new User("test.yandex.ru", "test",
                                LocalDate.of(1990, 1,15)),
                        "Incorrect parameter: email"),
                Arguments.of(new User("test@yandex.ru", "",
                                LocalDate.of(1990, 1,15)),
                        "Incorrect parameter: login"),
                Arguments.of(new User("test@yandex.ru", "test",
                                LocalDate.of(2023, 1,15)),
                        "Incorrect parameter: birthday")
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
        userFromLibrary.setId(-10L);

        final ValidationException exception = assertThrows(ValidationException.class,
                () -> userController.updateUser(userFromLibrary));
        assertEquals("Incorrect parameter: id", exception.getMessage(), "Incorrect message");
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
        userFromLibrary.setId(0L);
        userController.updateUser(userFromLibrary);

        assertEquals(2, userController.getUsers().size(), "Invalid number of users");
    }

    @Test
    void test6_getUsers() throws ValidationException {
        User user = new User("test@yandex.ru", "test", LocalDate.of(1990, 1,15));
        userController.createUser(user);
        user.setId(1L);

        User[] expectedUsers = {user};

        assertNotNull(userController.getUsers(), "List wasn't returned");
        assertArrayEquals(expectedUsers, userController.getUsers().toArray(new User[0]),
                "Arrays aren't match");
        assertEquals(1, userController.getUsers().size(), "Invalid number of users");
    }

    @Test
    void test7_findUserById() throws ValidationException {
        userController.createUser(new User("test@yandex.ru", "test",
                LocalDate.of(1990, 1,15)));
        userController.createUser(new User("test2@yandex.ru", "test2",
                LocalDate.of(1991, 1,15)));
        userController.createUser(new User("test3@yandex.ru", "test3",
                LocalDate.of(1992, 1,15)));

        User returnedUser = userController.findUserById(2);

        assertEquals(2, returnedUser.getId(), "Incorrect user id");
        assertEquals("test2@yandex.ru", returnedUser.getEmail(), "Incorrect user email");
        assertEquals("test2", returnedUser.getName(), "Incorrect user name");
    }

    @Test
    void test8_findUserByIncorrectId() throws ValidationException {
        userController.createUser(new User("test@yandex.ru", "test",
                LocalDate.of(1990, 1,15)));
        userController.createUser(new User("test2@yandex.ru", "test2",
                LocalDate.of(1991, 1,15)));

        final UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> userController.findUserById(125));
        assertEquals("User with id 125 wasn't found", exception.getMessage(), "Incorrect message");
        assertThrows(UserNotFoundException.class, () -> userController.findUserById(125),
                "Incorrect exception");
    }

    @Test
    void test9_addFriend() throws ValidationException {
        userController.createUser(new User("test@yandex.ru", "test",
                LocalDate.of(1990, 1,15)));
        userController.createUser(new User("test2@yandex.ru", "test2",
                LocalDate.of(1991, 1,15)));
        userController.createUser(new User("test3@yandex.ru", "test3",
                LocalDate.of(1992, 1,15)));

        userController.addFriend(1, 3);

        assertTrue(userController.findUserById(1).getFriends().contains(3L), "Id wasn't added");
        assertEquals(1, userController.findUserById(1).getFriends().size(),
                "Incorrect number of friends");
        assertTrue(userController.findUserById(3).getFriends().contains(1L), "Id wasn't added");
        assertEquals(1, userController.findUserById(3).getFriends().size(),
                "Incorrect number of friends");
        assertTrue(userController.findUserById(2).getFriends().isEmpty(), "Incorrect number of friends");
    }

    @Test
    void test10_addFriendWithIncorrectId() throws ValidationException {
        userController.createUser(new User("test@yandex.ru", "test",
                LocalDate.of(1990, 1,15)));
        userController.createUser(new User("test2@yandex.ru", "test2",
                LocalDate.of(1991, 1,15)));

        final UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> userController.addFriend(125, 1));
        assertEquals("User with id 125 wasn't found", exception.getMessage(), "Incorrect message");
        assertThrows(UserNotFoundException.class, () -> userController.addFriend(125, 1),
                "Incorrect exception");
        assertTrue(userController.findUserById(1).getFriends().isEmpty(), "Incorrect number of friends");
    }

    @Test
    void test11_addFriendWithIncorrectFriendId() throws ValidationException {
        userController.createUser(new User("test@yandex.ru", "test",
                LocalDate.of(1990, 1,15)));

        final UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> userController.addFriend(1, 34));
        assertEquals("User with id 34 wasn't found", exception.getMessage(), "Incorrect message");
        assertThrows(UserNotFoundException.class, () -> userController.addFriend(1, 34),
                "Incorrect exception");
        assertTrue(userController.findUserById(1).getFriends().isEmpty(), "Incorrect number of friends");
    }

    @Test
    void test12_deleteFriend() throws ValidationException {
        userController.createUser(new User("test@yandex.ru", "test",
                LocalDate.of(1990, 1,15)));
        userController.createUser(new User("test2@yandex.ru", "test2",
                LocalDate.of(1991, 1,15)));
        userController.createUser(new User("test3@yandex.ru", "test3",
                LocalDate.of(1992, 1,15)));

        userController.addFriend(1, 3);
        userController.addFriend(1, 2);
        userController.deleteFriend(1, 3);

        assertTrue(userController.findUserById(1).getFriends().contains(2L),
                "Incorrect value in friends list");
        assertEquals(1, userController.findUserById(1).getFriends().size(),
                "Incorrect number of friends");
        assertTrue(userController.findUserById(2).getFriends().contains(1L),
                "Incorrect value in friends list");
        assertEquals(1, userController.findUserById(1).getFriends().size(),
                "Incorrect number of friends");
        assertTrue(userController.findUserById(3).getFriends().isEmpty(), "Incorrect number of friends");
    }

    @Test
    void test12_deleteFriendWithIncorrectUserId() throws ValidationException {
        userController.createUser(new User("test@yandex.ru", "test",
                LocalDate.of(1990, 1,15)));
        userController.createUser(new User("test2@yandex.ru", "test2",
                LocalDate.of(1991, 1,15)));

        userController.addFriend(1,2);

        final UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> userController.deleteFriend(125, 2));
        assertEquals("User with id 125 wasn't found", exception.getMessage(), "Incorrect message");
        assertThrows(UserNotFoundException.class, () -> userController.deleteFriend(125, 1),
                "Incorrect exception");
        assertEquals(1, userController.findUserById(2).getFriends().size(),
                "Incorrect number of friends");
    }

    @Test
    void test13_deleteFriendWithIncorrectId() throws ValidationException {
        userController.createUser(new User("test@yandex.ru", "test",
                LocalDate.of(1990, 1,15)));
        userController.createUser(new User("test2@yandex.ru", "test2",
                LocalDate.of(1991, 1,15)));

        userController.addFriend(1,2);

        final UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> userController.deleteFriend(1, 45));
        assertEquals("Impossible to remove user with id 45 from friends list. User not found",
                exception.getMessage(), "Incorrect message");
        assertThrows(UserNotFoundException.class, () -> userController.deleteFriend(1, 45),
                "Incorrect exception");
        assertEquals(1, userController.findUserById(1).getFriends().size(),
                "Incorrect number of friends");
    }

    @Test
    void test14_getFriends() throws ValidationException {
        userController.createUser(new User("test@yandex.ru", "test",
                LocalDate.of(1990, 1,15)));
        userController.createUser(new User("test2@yandex.ru", "test2",
                LocalDate.of(1991, 1,15)));
        userController.createUser(new User("test3@yandex.ru", "test3",
                LocalDate.of(1992, 1,15)));

        userController.addFriend(1,2);

        Long[] expectedList = {2L};

        assertNotNull(userController.getFriends(1), "List wasn't returned");
        assertArrayEquals(expectedList, userController.getFriends(1).toArray(), "Arrays aren't match");
        assertEquals(1, userController.getFriends(1).size(), "Invalid number of id");
        assertNull(userController.getFriends(3), "Incorrect list");
    }

    @Test
    void test14_getCommonFriends() throws ValidationException {
        userController.createUser(new User("test@yandex.ru", "test",
                LocalDate.of(1990, 1,15)));
        userController.createUser(new User("test2@yandex.ru", "test2",
                LocalDate.of(1991, 1,15)));
        userController.createUser(new User("test3@yandex.ru", "test3",
                LocalDate.of(1992, 1,15)));

        userController.addFriend(1, 2);
        userController.addFriend(1,3);
        userController.addFriend(2, 3);

        User[] expectedList = {userController.findUserById(3)};

        assertArrayEquals(expectedList, userController.getCommonFriends(1, 2).toArray(),
                "Arrays aren't match");
        assertEquals(3, userController.getCommonFriends(1, 2).get(0).getId(),
                "Incorrect friend id");
        assertEquals("test3@yandex.ru", userController.getCommonFriends(1, 2).get(0).getEmail(),
                "Incorrect friend email");
    }
}