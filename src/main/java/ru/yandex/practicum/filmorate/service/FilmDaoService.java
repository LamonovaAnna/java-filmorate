package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.mapRow.RowTo;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Slf4j
@Service
public class FilmDaoService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmDaoService(@Qualifier("filmDbStorage") FilmStorage filmStorage,
                          @Qualifier("userDbStorage") UserStorage userStorage,
                          JdbcTemplate jdbcTemplate) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.jdbcTemplate = jdbcTemplate;
    }

    public Film createFilm(Film film) {
        return filmStorage.createFilm(film);
    }

    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    public List<Film> getFilms() {
        return filmStorage.getFilms();
    }

    public Film findFilmById(long id) {
        return filmStorage.findFilmById(id);
    }

    public void deleteFilm(long id) {
        filmStorage.deleteFilm(id);
    }

    public void addLike(long id, long userId) {
        if (filmStorage.findFilmById(id) != null && userStorage.findUserById(userId) != null) {
            String sqlQuery = "INSERT INTO film_likes" +
                    " (film_id, user_id)" +
                    " VALUES (?, ?)";
            jdbcTemplate.update(sqlQuery, id, userId);
            log.info("User with id {} liked film with id {}", userId, id);
        }
    }

    public void deleteLike(long id, long userId) {
        if (filmStorage.findFilmById(id) != null && userStorage.findUserById(userId) != null) {
            String sqlQuery = "DELETE FROM film_likes WHERE film_id = ? AND user_id = ?";
            jdbcTemplate.update(sqlQuery, id, userId);
            log.info("User with id {} deleted like for film with id {}", userId, id);
        }
    }

    public List<Film> getPopularFilms(Integer count) {
        String sqlQuery = "SELECT f.* " +
                "FROM films AS f " +
                "LEFT JOIN film_likes AS fl ON f.film_id=fl.film_id " +
                "GROUP BY f.film_id " +
                "ORDER BY COUNT(fl.user_id) DESC " +
                "LIMIT ?";
        return jdbcTemplate.query(sqlQuery, RowTo::mapRowToFilm, count);
    }
}