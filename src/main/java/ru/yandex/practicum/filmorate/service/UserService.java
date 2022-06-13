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

    public void addFriend(long id, long friendId) {
        if (userStorage.findById(id) != null && userStorage.findById(friendId) != null) {
            userStorage.findById(id).getFriends().add(friendId);
            log.info(String.format("Friend with id %d was added for user with id %d", friendId, id));

            userStorage.findById(friendId).getFriends().add(id);
            log.info(String.format("Friend with id %d was added for user with id %d", id, friendId));
        }
    }

    public void deleteFriend(long id, long friendId) {
        if (userStorage.findById(id) != null && userStorage.findById(id).getFriends().contains(friendId)) {
            userStorage.findById(id).getFriends().remove(friendId);
            log.info(String.format("Friend with id %d was deleted from user list", friendId));

            userStorage.findById(friendId).getFriends().remove(id);
            log.info(String.format("Friend with id %d was deleted from user list", id));
        } else {
            log.info(String.format("Incorrect friend id %d", friendId));
            throw new UserNotFoundException(
                    String.format("Impossible to remove user with id %d from friends list. User not found", friendId));
        }
    }

    public List<Long> getFriends(long id) {
        if (userStorage.findById(id) != null && !userStorage.findById(id).getFriends().isEmpty()) {
            return List.copyOf(userStorage.findById(id).getFriends());
        } else {
            return null;
        }
    }

    public List<User> getCommonFriends(long id, long otherId) {
        List<User> commonFriends = new ArrayList<>();
        for(long friendId : userStorage.findById(id).getFriends()) {
            if (userStorage.findById(otherId).getFriends().contains(friendId)) {
                commonFriends.add(userStorage.findById(friendId));
            }
        }
        return commonFriends;
    }
}
