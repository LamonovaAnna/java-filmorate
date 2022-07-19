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
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Component
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private static final LocalDate MOVIE_BIRTHDAY = LocalDate.of(1895, 12, 28);
    private static final String QUERY_CREATE_FILM = "INSERT INTO films" +
            " (film_name, description, rate, release_date, duration, mpa_rating_id)" +
            " VALUES (?, ?, ?, ?, ?, ?)";
    private static final String QUERY_UPDATE_FILM = "UPDATE films" +
            " SET film_name = ?, description = ?, rate = ?," +
            " release_date = ?, duration = ?, mpa_rating_id = ?" +
            " WHERE film_id = ?";
    private static final String QUERY_GET_ALL_FILMS = "SELECT * FROM films";
    private static final String QUERY_GET_FILM_BY_ID = "SELECT * FROM films WHERE film_id = ?";
    private static final String QUERY_DELETE_FILM = "DELETE FROM films WHERE film_id = ?";
    private static final String QUERY_DELETE_GENRE_FROM_FILM = "DELETE FROM film_genres WHERE film_id = ?";
    private static final String QUERY_SET_GENRE_TO_FILM = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film createFilm(Film film) throws ValidationException {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        if (validate(film)) {
            jdbcTemplate.update(con -> {
                PreparedStatement stmt = con.prepareStatement(QUERY_CREATE_FILM, new String[]{("film_id")});
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
            jdbcTemplate.update(QUERY_UPDATE_FILM,
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
        return jdbcTemplate.query(QUERY_GET_ALL_FILMS, RowTo::mapRowToFilm);
    }

    @Override
    public Film findFilmById(long id) {
        if (id > 0) {
            return jdbcTemplate.queryForObject(QUERY_GET_FILM_BY_ID, RowTo::mapRowToFilm, id);
        }
        log.debug("Incorrect id");
        throw new FilmNotFoundException(String.format("Film with id %d not found", id));
    }

    @Override
    public void deleteFilm(long id) {
        jdbcTemplate.update(QUERY_DELETE_FILM, id);
    }

    private void setFilmGenresValues(Film film) {
        if (film.getGenres() != null) {
            if (film.getGenres().isEmpty()) {
                jdbcTemplate.update(QUERY_DELETE_GENRE_FROM_FILM, film.getId());
            } else {
                jdbcTemplate.update(QUERY_DELETE_GENRE_FROM_FILM, film.getId());
                for (Genre genre : film.getGenres()) {
                    jdbcTemplate.update(QUERY_SET_GENRE_TO_FILM, film.getId(), genre.getId());
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