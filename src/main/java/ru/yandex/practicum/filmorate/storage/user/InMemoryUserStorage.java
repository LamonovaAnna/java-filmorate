package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private long userId = 1;

    private long generateId() {
        return userId++;
    }

    public User createUser(User user) throws ValidationException {
        if (validate(user)) {
            user.setId(generateId());
            users.put(user.getId(), user);
        }
        log.info("User was created with id {}", user.getId());
        return user;
    }

    public User updateUser(User user) throws ValidationException {
        if (validate(user)) {
            if (user.getId() != 0 && users.containsKey(user.getId())) {
                users.put(user.getId(), user);
                log.info("User with id {} was updated", user.getId());
            } else if(user.getId() == 0) {
                user.setId(generateId());
                users.put(user.getId(), user);
                log.info("User with id {} not found. Instead of updating the user was created", user.getId());
            } else {
                log.info("User with id {} not found.", user.getId());
                throw new UserNotFoundException(String.format("User with id %d wasn't found", user.getId()));
            }
        }
        return user;
    }

    public List<User> getUsers() {
        return List.copyOf(users.values());
    }

    public User findById(long id) {
        if (!users.containsKey(id)) {
            log.debug("Incorrect id");
            throw new UserNotFoundException(String.format("User with id %d wasn't found", id));
        }
        return users.get(id);
    }

    private boolean validate(User user) throws ValidationException {
        if (user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.debug("Incorrect email");
            throw new ValidationException("email");
        } else if (user.getLogin().isBlank()) {
            log.debug("Incorrect login");
            throw new ValidationException("login");
        } else if (user.getBirthday().isAfter(LocalDate.now())) {
            log.debug("Incorrect birthday");
            throw new ValidationException("birthday");
        } else if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("Name has been changed to value {}", user.getLogin());
        }
        return true;
    }
}