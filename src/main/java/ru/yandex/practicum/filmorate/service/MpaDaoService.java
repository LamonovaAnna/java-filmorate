package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.MpaNotFoundException;
import ru.yandex.practicum.filmorate.mapRow.RowTo;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

@Slf4j
@Service
public class MpaDaoService {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MpaDaoService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Mpa> getMpa() {
        String sqlQuery = "SELECT * FROM mpa_ratings";
        return jdbcTemplate.query(sqlQuery, RowTo::mapRowToMpa);
    }

    public Mpa findMpaById(long id) {
        if(id > 0) {
            String sqlQuery = "SELECT * FROM mpa_ratings WHERE mpa_rating_id = ?";
            return jdbcTemplate.queryForObject(sqlQuery, RowTo::mapRowToMpa, id);
        }
        log.debug("Incorrect id");
        throw new MpaNotFoundException(String.format("Film with id %d not found", id));
    }
}
