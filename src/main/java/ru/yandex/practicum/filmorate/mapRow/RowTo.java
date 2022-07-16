package ru.yandex.practicum.filmorate.mapRow;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashSet;

import static ru.yandex.practicum.filmorate.constant.Constant.*;

@Component
public class RowTo {

    private static JdbcTemplate jdbcTemplate;

    @Autowired
    public RowTo(JdbcTemplate jdbcTemplate) {
        RowTo.jdbcTemplate = jdbcTemplate;
    }

    public static Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        return Film.builder()
                .id(resultSet.getLong("film_id"))
                .name(resultSet.getString("film_name"))
                .description(resultSet.getString("description"))
                .rate(resultSet.getInt("rate"))
                .releaseDate(resultSet.getDate("release_date").toLocalDate())
                .duration(resultSet.getLong("duration"))
                .mpa(jdbcTemplate.queryForObject(QUERY_GET_MPA_BY_ID, RowTo::mapRowToMpa,
                        resultSet.getInt("mpa_rating_id")))
                .genres(new LinkedHashSet<>(jdbcTemplate.query(QUERY_GET_GENRE_BY_FILM_ID, RowTo::mapRowToGenre,
                        resultSet.getLong("film_id"))))
                .build();
    }

    public static Mpa mapRowToMpa(ResultSet resultSet, int rowNum) throws SQLException {
        return Mpa.builder()
                .id(resultSet.getInt("mpa_rating_id"))
                .name(resultSet.getString("mpa_rating_name"))
                .build();
    }

    public static User mapRowToUser(ResultSet resultSet, int rowNum) throws SQLException {
        return User.builder()
                .id(resultSet.getInt("user_id"))
                .name(resultSet.getString("user_name"))
                .login(resultSet.getString("login"))
                .email(resultSet.getString("email"))
                .birthday(resultSet.getDate("birthday").toLocalDate())
                .build();
    }

    public static Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        return Genre.builder()
                .id(resultSet.getInt("genre_id"))
                .name(resultSet.getString("genre_name"))
                .build();
    }
}