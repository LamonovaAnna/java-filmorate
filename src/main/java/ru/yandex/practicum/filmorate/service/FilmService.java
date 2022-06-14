package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(InMemoryFilmStorage filmStorage, InMemoryUserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public FilmStorage getFilmStorage() {
        return filmStorage;
    }

    public void addLike(long id, long userId) {
        if (filmStorage.findFilmById(id) != null && userStorage.findById(userId) != null) {
            filmStorage.findFilmById(id).getLikes().add(userId);
            log.info("User with id {} liked film with id {}", userId, id);
        }
    }

    public void deleteLike(long id, long userId) {
        if (filmStorage.findFilmById(id) != null && userStorage.findById(userId) != null) {
            filmStorage.findFilmById(id).getLikes().remove(userId);
            log.info("User with id {} deleted like for film with id {}", userId, id);
        }
    }

    public List<Film> getPopularFilms(Integer count) {
        if (count <= filmStorage.getFilms().size()) {
            return filmStorage.getFilms().stream()
                    .sorted(Comparator.comparingInt(f0 -> -(f0.getLikes().size())))
                    .limit(count)
                    .collect(Collectors.toList());
        } else {
            return filmStorage.getFilms().stream()
                    .sorted(Comparator.comparingInt(f0 -> -(f0.getLikes().size())))
                    .collect(Collectors.toList());
        }
    }
}