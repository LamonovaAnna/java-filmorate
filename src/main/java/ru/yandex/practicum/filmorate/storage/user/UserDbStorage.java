package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapRow.RowTo;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
public class UserDbStorage implements UserStorage{
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User createUser(User user) throws ValidationException {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        if(validate(user)) {
            String sqlQuery = "INSERT INTO users (user_name, login, email, birthday) VALUES (?, ?, ?, ?)";
            jdbcTemplate.update(con -> {
                PreparedStatement stmt = con.prepareStatement(sqlQuery, new String[]{("user_id")});
                stmt.setString(1, user.getName());
                stmt.setString(2, user.getLogin());
                stmt.setString(3, user.getEmail());
                stmt.setDate(4, Date.valueOf(user.getBirthday()));
                return stmt;
            }, keyHolder);
        }
        user.setId(keyHolder.getKey().longValue());
        log.info("User was created with id {}", user.getId());
        return user;
    }


    @Override
    public User updateUser(User user) throws ValidationException {
        if(validate(user)) {
            String sqlQuery = "UPDATE users SET user_name = ?, login = ?, email = ?, birthday = ? " +
                        "WHERE user_id = ?";
            jdbcTemplate.update(sqlQuery,
                    user.getName(),
                    user.getLogin(),
                    user.getEmail(),
                    user.getBirthday(),
                    user.getId());
            log.info("User with id {} was updated", user.getId());
        }
        return user;
    }

    @Override
    public List<User> getUsers() {
        String sqlQuery = "SELECT * FROM users";
        return jdbcTemplate.query(sqlQuery, RowTo::mapRowToUser);
    }

    @Override
    public User findUserById(long id) {
        if(id <= 0) {
            throw new UserNotFoundException(String.format("User with id %d wasn't found", id));
        }
        String sqlQuery = "SELECT * FROM users WHERE user_id = ?";
        return jdbcTemplate.queryForObject(sqlQuery, RowTo::mapRowToUser, id);
    }

    @Override
    public void deleteUser(long id) {
        String sqlQuery = "DELETE FROM usres WHERE user_id = ?";
        jdbcTemplate.update(sqlQuery, id);
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
        } else if (user.getId() < 0) {
            log.debug("Incorrect id");
            throw new UserNotFoundException(String.format("User with id %d wasn't found", user.getId()));
        }
        return true;
    }
}