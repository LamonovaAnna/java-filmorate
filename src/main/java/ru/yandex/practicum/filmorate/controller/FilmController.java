package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
public class FilmController {
    private Map<Long, Film> films = new HashMap<>();
    private static final LocalDate MOVIE_BIRTHDAY = LocalDate.of(1895, 12, 28);
    private long filmId = 1;

    private long generateId() {
        return filmId++;
    }

    @PostMapping("/films")
    public Film createFilm(@RequestBody Film film) throws ValidationException {
        if (validate(film)) {
            film.setId(generateId());
            films.put(film.getId(), film);
        }
        log.info("Film was created with id {}", film.getId());
        return film;
    }

    @PutMapping("/films")
    public Film updateFilm(@RequestBody Film film) throws ValidationException {
        if (validate(film)) {
            if (film.getId() != 0 && films.containsKey(film.getId())) {
                films.put(film.getId(), film);
                log.info("Film with id {} was updated", film.getId());
            } else {
                film.setId(generateId());
                films.put(film.getId(), film);
                log.info("Film not found in library. The film was added with id {}", film.getId());
            }
        }
        return film;
    }

    @GetMapping("/films")
    public List getFilms() {
        return new ArrayList(films.values());
    }

    private boolean validate(Film film) throws ValidationException {
        if (film.getName().isBlank()) {
            log.debug("Incorrect film name");
            throw new ValidationException("Incorrect film name");
        } else if (film.getDescription().length() > 200) {
            log.debug("Description is too long");
            throw new ValidationException("Description is too long");
        } else if (film.getReleaseDate().isBefore(MOVIE_BIRTHDAY)) {
            log.debug("Incorrect Release date");
            throw new ValidationException("Incorrect Release date");
        } else if (film.getId() < 0) {
            log.debug("Incorrect id");
            throw new ValidationException("Incorrect id");
        } else if (film.getDuration() <= 0) {
            log.debug("Incorrect duration");
            throw new ValidationException("Incorrect duration");
        }
        return true;
    }
}