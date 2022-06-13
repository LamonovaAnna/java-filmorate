package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/users")
    public User createUser(@RequestBody User user) throws ValidationException {
        return userService.getUserStorage().createUser(user);
    }

    @PutMapping("/users")
    public User updateUser(@RequestBody User user) throws ValidationException {
        return userService.getUserStorage().updateUser(user);
    }

    @GetMapping("/users")
    public List getUsers() {
        return userService.getUserStorage().getUsers();
    }

    @GetMapping("/users/{id}")
    public User findUserById(@PathVariable long id) {
        return userService.getUserStorage().findById(id);
    }

    @PutMapping("/users/{id}/friends/{friendId}")
    public User addFriend() {

    }
}
