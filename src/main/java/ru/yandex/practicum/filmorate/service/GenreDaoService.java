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
    private static final String QUERY_GET_ALL_GENRES = "SELECT * FROM genres";
    private static final String QUERY_GET_GENRE_BY_ID = "SELECT * FROM genres WHERE genre_id = ?";

    @Autowired
    public GenreDaoService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Genre> getGenres() {
        return jdbcTemplate.query(QUERY_GET_ALL_GENRES, RowTo::mapRowToGenre);
    }

    public Genre findGenreById(long id) {
        if(id > 0) {
            return jdbcTemplate.queryForObject(QUERY_GET_GENRE_BY_ID, RowTo::mapRowToGenre, id);
        }
        log.debug("Incorrect id");
        throw new GenreNotFoundException(String.format("Film with id %d not found", id));
    }
}