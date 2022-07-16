package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapRow.RowTo;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.Comparator;
import java.util.List;

import static ru.yandex.practicum.filmorate.constant.Constant.MOVIE_BIRTHDAY;


@Slf4j
@Component
public class FilmDbStorage implements FilmStorage{
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film createFilm(Film film) throws ValidationException {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        if(validate(film)) {
            String sqlQuery = "INSERT INTO films" +
                    " (film_name, description, rate, release_date, duration, mpa_rating_id)" +
                    " VALUES (?, ?, ?, ?, ?, ?)";
            jdbcTemplate.update(con -> {
                PreparedStatement stmt = con.prepareStatement(sqlQuery, new String[]{("film_id")});
                stmt.setString(1, film.getName());
                stmt.setString(2, film.getDescription());
                stmt.setInt(3, film.getRate());
                stmt.setDate(4, Date.valueOf(film.getReleaseDate()));
                stmt.setLong(5, film.getDuration());
                stmt.setInt(6, film.getMpa().getId());
                return stmt;
            }, keyHolder);
        }
        if(film.getGenres() != null && !film.getGenres().isEmpty()) {
            film.getGenres().stream().sorted(Comparator.comparingInt(Genre::getId));
        }
        film.setId(keyHolder.getKey().longValue());
        log.info("Film was created with id {}", film.getId());
        setFilmGenresValues(film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) throws ValidationException {
        if(validate(film)) {
            String sqlQuery = "UPDATE films" +
                    " SET film_name = ?, description = ?, rate = ?," +
                    " release_date = ?, duration = ?, mpa_rating_id = ?" +
                    " WHERE film_id = ?";
            jdbcTemplate.update(sqlQuery,
                    film.getName(),
                    film.getDescription(),
                    film.getRate(),
                    film.getReleaseDate(),
                    film.getDuration(),
                    film.getMpa().getId(),
                    film.getId());
            if(film.getGenres() != null && !film.getGenres().isEmpty()) {
                film.getGenres().stream().sorted(Comparator.comparingInt(Genre::getId));
            }
            setFilmGenresValues(film);
            log.info("Film with id {} was updated", film.getId());
        }
        return film;
    }

    @Override
    public List<Film> getFilms() {
        String sqlQuery = "SELECT * FROM films";
        return jdbcTemplate.query(sqlQuery, RowTo::mapRowToFilm);
    }

    @Override
    public Film findFilmById(long id) {
        if (id > 0) {
            String sqlQuery = "SELECT * FROM films WHERE film_id = ?";
            return jdbcTemplate.queryForObject(sqlQuery, RowTo::mapRowToFilm, id);
        }
        log.debug("Incorrect id");
        throw new FilmNotFoundException(String.format("Film with id %d not found", id));
    }

    @Override
    public void deleteFilm(long id) {
        String sqlQuery = "DELETE FROM films WHERE film_id = ?";
        jdbcTemplate.update(sqlQuery, id);
    }

    private void setFilmGenresValues(Film film) {
        String sqlQuery = "DELETE FROM film_genres WHERE film_id = ?";
        String sqlQueryGenre = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";
        if (film.getGenres() != null) {
            if (film.getGenres().isEmpty()) {
                jdbcTemplate.update(sqlQuery, film.getId());
            } else {
                jdbcTemplate.update(sqlQuery, film.getId());
                for (Genre genre : film.getGenres()) {
                    jdbcTemplate.update(sqlQueryGenre, film.getId(), genre.getId());
                }
            }
        }
    }

    private boolean validate(Film film) throws ValidationException {
        if (film.getName().isBlank()) {
            log.debug("Incorrect film name");
            throw new ValidationException("name");
        } else if (film.getDescription().length() > 200) {
            log.debug("Description is too long");
            throw new ValidationException("description. Description is too long");
        } else if (film.getReleaseDate().isBefore(MOVIE_BIRTHDAY)) {
            log.debug("date");
            throw new ValidationException("release date");
        } else if (film.getDuration() <= 0) {
            log.debug("Incorrect duration");
            throw new ValidationException("duration");
        } else if (film.getId() < 0) {
            log.debug("Incorrect id");
            throw new FilmNotFoundException(String.format("Film with id %d not found", film.getId()));
        }
        return true;
    }
}