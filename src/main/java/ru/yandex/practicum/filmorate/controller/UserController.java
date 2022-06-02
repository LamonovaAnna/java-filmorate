package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
public class UserController {
    private Map<Long, User> users = new HashMap<>();
    private long userId = 1;

    private long generateId() {
        return userId++;
    }

    @PostMapping("/users")
    public User createUser(@RequestBody User user) throws ValidationException {
        if (validate(user)) {
            user.setId(generateId());
            users.put(user.getId(), user);
        }
        log.info("User was created with id {}", user.getId());
        return user;
    }

    @PutMapping("/users")
    public User updateUser(@RequestBody User user) throws ValidationException {
        if (validate(user)) {
            if (user.getId() != 0 && users.containsKey(user.getId())) {
                users.put(user.getId(), user);
                log.info("User with id {} was updated", user.getId());
            } else {
                user.setId(generateId());
                users.put(user.getId(), user);
                log.info("User not found. User was added with id {}", user.getId());
            }
        }
        return user;
    }

    @GetMapping("/users")
    public List getUsers() {
        return new ArrayList(users.values());
    }

    private boolean validate(User user) throws ValidationException {
        if (user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.debug("Incorrect email");
            throw new ValidationException("Incorrect email");
        } else if (user.getLogin().isBlank()) {
            log.debug("Incorrect login");
            throw new ValidationException("Incorrect login");
        } else if (user.getBirthday().isAfter(LocalDate.now())) {
            log.debug("Incorrect birthday");
            throw new ValidationException("Incorrect birthday");
        } else if (user.getId() < 0) {
            log.debug("Incorrect id");
            throw new ValidationException("Incorrect id");
        } else if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("Name has been changed to value {}", user.getLogin());
        }
        return true;
    }
}
