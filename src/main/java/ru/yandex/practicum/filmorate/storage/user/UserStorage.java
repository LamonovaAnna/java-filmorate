package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {

    User createUser(User user) throws ValidationException;

    User updateUser(User user) throws ValidationException;

    List getUsers();

    User findById(long id);
}
