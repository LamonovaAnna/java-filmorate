package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films = new HashMap<>();
    private long filmId = 1;
    private static final LocalDate MOVIE_BIRTHDAY = LocalDate.of(1895, 12, 28);

    private long generateId() {
        return filmId++;
    }

    @Override
    public Film createFilm(Film film) throws ValidationException {
        if (validate(film)) {
            film.setId(generateId());
            films.put(film.getId(), film);
        }
        log.info("Film was created with id {}", film.getId());
        return film;
    }

    @Override
    public Film updateFilm(Film film) throws ValidationException {
        if (validate(film)) {
            if (film.getId() != 0 && films.containsKey(film.getId())) {
                films.put(film.getId(), film);
                log.info("Film with id {} was updated", film.getId());
            } else if (film.getId() == 0) {
                film.setId(generateId());
                films.put(film.getId(), film);
                log.info("Film not found in library. The film was added with id {}", film.getId());
            } else {
                throw new FilmNotFoundException(String.format("Film with id %d not found", film.getId()));
            }
        }
        return film;
    }

    @Override
    public List<Film> getFilms() {
        return List.copyOf(films.values());
    }

    @Override
    public Film findFilmById(long id) {
        if (!films.containsKey(id)) {
            log.debug("Incorrect id");
            throw new FilmNotFoundException(String.format("Film with id %d not found", id));
        }
        return films.get(id);
    }

    public void deleteFilm(long id) {
        films.remove(id);
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
        }
        return true;
    }
}