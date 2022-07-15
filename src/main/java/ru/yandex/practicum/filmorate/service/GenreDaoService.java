package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.GenreNotFoundException;
import ru.yandex.practicum.filmorate.mapRow.RowTo;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

@Slf4j
@Service
public class GenreDaoService {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public GenreDaoService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Genre> getGenres() {
        String sqlQuery = "SELECT * FROM genres";
        return jdbcTemplate.query(sqlQuery, RowTo::mapRowToGenre);
    }

    public Genre findGenreById(long id) {
        if(id > 0) {
            String sqlQuery = "SELECT * FROM genres WHERE genre_id = ?";
            return jdbcTemplate.queryForObject(sqlQuery, RowTo::mapRowToGenre, id);
        }
        log.debug("Incorrect id");
        throw new GenreNotFoundException(String.format("Film with id %d not found", id));
    }
}