package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.mapRow.RowTo;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Slf4j
@Service
public class UserDaoService {

    private final UserStorage userStorage;
    private final JdbcTemplate jdbcTemplate;
    private static final String QUERY_ADD_FRIEND = "INSERT INTO friends " +
            "(request_user_id, accept_user_id, is_accepted) " +
            "VALUES (?, ?, ?)";
    private static final String QUERY_DELETE_FRIEND = "DELETE FROM friends WHERE request_user_id = ? " +
            "AND accept_user_id = ?";
    private static final String GET_ALL_FRIENDS = "SELECT * " +
            "FROM users " +
            "WHERE user_id IN (SELECT accept_user_id " +
            "FROM friends " +
            "WHERE request_user_id=?)";
    private static final String QUERY_GET_COMMON_FRIENDS = "SELECT * " +
            "FROM users " +
            "WHERE user_id IN (SELECT accept_user_id " +
            "FROM friends " +
            "WHERE request_user_id=? " +
            "AND accept_user_id IN " +
            "(SELECT accept_user_id " +
            "FROM friends " +
            "WHERE request_user_id=?))";

    @Autowired
    public UserDaoService(@Qualifier("userDbStorage") UserStorage userStorage,
                          JdbcTemplate jdbcTemplate) {
        this.userStorage = userStorage;
        this.jdbcTemplate = jdbcTemplate;
    }

    public User createUser(User user) {
        return userStorage.createUser(user);
    }

    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    public List<User> getUsers() {
        return userStorage.getUsers();
    }

    public User findUserById(long id) {
        return userStorage.findUserById(id);
    }

    public void deleteUser(long id) {
        userStorage.deleteUser(id);
    }

    public void addFriend(long id, long friendId) {
        if (userStorage.findUserById(id) != null && userStorage.findUserById(friendId) != null) {
            jdbcTemplate.update(QUERY_ADD_FRIEND, id, friendId, "true");
            log.info("Friend with id {} was added for user with id {}", friendId, id);
        }
    }

    public void deleteFriend(long id, long friendId) {
        if (findUserById(id) != null && getFriends(id).contains(findUserById(friendId))) {
            jdbcTemplate.update(QUERY_DELETE_FRIEND, id, friendId);
            log.info("Friend with id {} was deleted from user list", id);
        } else {
            log.info("Incorrect friend id {}", friendId);
            throw new UserNotFoundException(
                    String.format("Impossible to remove user with id %d from friends list. User not found", friendId));
        }
    }

    public List<User> getFriends(long id) {
        if (userStorage.findUserById(id) != null) {
            return jdbcTemplate.query(GET_ALL_FRIENDS, RowTo::mapRowToUser, id);
        } else {
            return null;
        }
    }

    public List<User> getCommonFriends(long id, long otherId) {
        return jdbcTemplate.query(QUERY_GET_COMMON_FRIENDS, RowTo::mapRowToUser, id, otherId);
    }
}