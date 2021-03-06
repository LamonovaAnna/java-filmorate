package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(InMemoryUserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public UserStorage getUserStorage() {
        return userStorage;
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
            userStorage.findUserById(id).getFriends().add(friendId);
            log.info("Friend with id {} was added for user with id {}", friendId, id);

            userStorage.findUserById(friendId).getFriends().add(id);
            log.info("Friend with id {} was added for user with id {}", id, friendId);
        }
    }

    public void deleteFriend(long id, long friendId) {
        if (userStorage.findUserById(id) != null && userStorage.findUserById(id).getFriends().contains(friendId)) {
            userStorage.findUserById(id).getFriends().remove(friendId);
            log.info("Friend with id {} was deleted from user list", friendId);

            userStorage.findUserById(friendId).getFriends().remove(id);
            log.info("Friend with id {} was deleted from user list", id);
        } else {
            log.info("Incorrect friend id {}", friendId);
            throw new UserNotFoundException(
                    String.format("Impossible to remove user with id %d from friends list. User not found", friendId));
        }
    }

    public List<User> getFriends(long id) {
        List<User> friends = new ArrayList<>();
        if (userStorage.findUserById(id) != null && !userStorage.findUserById(id).getFriends().isEmpty()) {
            for(long friendId : userStorage.findUserById(id).getFriends()){
                friends.add(userStorage.findUserById(friendId));
            }
            return friends;
        } else {
            return null;
        }
    }

    public List<User> getCommonFriends(long id, long otherId) {
        List<User> commonFriends = new ArrayList<>();
        for(long friendId : userStorage.findUserById(id).getFriends()) {
            if (userStorage.findUserById(otherId).getFriends().contains(friendId)) {
                commonFriends.add(userStorage.findUserById(friendId));
            }
        }
        return commonFriends;
    }
}