package ru.yandex.practicum.filmorate.constant;

import java.time.LocalDate;

public class Constant {
    public static final String QUERY_GET_MPA_BY_ID = "SELECT * FROM mpa_ratings WHERE mpa_rating_id = ?";
    public static final String QUERY_GET_GENRE_BY_FILM_ID = "SELECT * FROM genres JOIN film_genres " +
            "AS fg ON genres.genre_id = fg.genre_id  " +
            "WHERE fg.film_id = ?";
    public static final String QUERY_ADD_LIKE_TO_FILM = "INSERT INTO film_likes" +
            " (film_id, user_id)" +
            " VALUES (?, ?)";
    public static final String QUERY_DELETE_FROM_FILM = "DELETE FROM film_likes WHERE film_id = ? AND user_id = ?";
    public static final String QUERY_GET_POPULAR_FILMS = "SELECT f.* " +
            "FROM films AS f " +
            "LEFT JOIN film_likes AS fl ON f.film_id=fl.film_id " +
            "GROUP BY f.film_id " +
            "ORDER BY COUNT(fl.user_id) DESC " +
            "LIMIT ?";
    public static final String QUERY_GET_ALL_MPA = "SELECT * FROM mpa_ratings";
    public static final String QUERY_ADD_FRIEND = "INSERT INTO friends" +
            " (request_user_id, accept_user_id, is_accepted)" +
            " VALUES (?, ?, ?)";
    public static final String QUERY_DELETE_FRIEND = "DELETE FROM friends WHERE request_user_id = ? " +
            "AND accept_user_id = ?";
    public static final String GET_ALL_FRIENDS = "SELECT * " +
            "FROM users " +
            "WHERE user_id IN (SELECT accept_user_id " +
            "FROM friends " +
            "WHERE request_user_id=?)";
    public static final String QUERY_GET_COMMON_FRIENDS = "SELECT * " +
            "FROM users " +
            "WHERE user_id IN (SELECT accept_user_id " +
            "FROM friends " +
            "WHERE request_user_id=? " +
            "AND accept_user_id IN " +
            "(SELECT accept_user_id " +
            "FROM friends " +
            "WHERE request_user_id=?))";
    public static final String QUERY_GET_ALL_GENRES = "SELECT * FROM genres";
    public static final String QUERY_GET_GENRE_BY_ID = "SELECT * FROM genres WHERE genre_id = ?";
    public static final LocalDate MOVIE_BIRTHDAY = LocalDate.of(1895, 12, 28);

}
