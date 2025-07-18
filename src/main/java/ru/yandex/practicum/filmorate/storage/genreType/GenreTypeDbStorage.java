package ru.yandex.practicum.filmorate.storage.genreType;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.GenreType;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class GenreTypeDbStorage implements GenreTypeStorage {
    private final JdbcTemplate jdbc;

    private static final String FIND_NAME_BY_ID = "SELECT * FROM genre_type WHERE genre_id = ?";
    private static final String INSERT_QUERY = """
            INSERT INTO genre (film_id, genre_id)
            VALUES (?, ?)""";
    private static final String DELETE_ALL_GENRES = "DELETE FROM genre WHERE film_id = ?";
    private static final String GET_GENRES_BY_FILM_ID = "SELECT genre_id FROM genre WHERE film_id = ?";
    private static final String GET_BEST_FILM_ID = """
            SELECT film_id
            FROM film_likes
            GROUP BY film_id
            ORDER BY COUNT(user_id) DESC
            LIMIT ?""";
    public static final String GET_LIKES = "SELECT user_id FROM film_likes WHERE film_id = ?";
    private static final String DELETE_ALL_LIKES = "DELETE FROM film_likes WHERE film_id = ?";
    private static final String ADD_LIKE = """
            INSERT INTO film_likes (film_id, user_id)
            VALUES (?, ?)""";
    private static final String GET_ALL_GENRES = "SELECT * FROM genre_type";
    public static final String GET_GENRE_TYPE_BY_FILM_ID = """
            SELECT * FROM genre_type AS gt
            WHERE gt.genre_id IN (SELECT genre_id
            FROM genre WHERE film_id = ?)""";


    @Override
    public GenreType findNameById(int id) {
        return jdbc.queryForObject(FIND_NAME_BY_ID, this::mapRow, id);
    }

    @Override
    public void insertGenreData(long filmId, int genreId) {
        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(INSERT_QUERY);
            ps.setLong(1, filmId);
            ps.setInt(2, genreId);
            return ps;
        });
    }

    @Override
    public void deleteAllGenres(long filmId) {
        jdbc.update(DELETE_ALL_GENRES, filmId);
    }

    @Override
    public List<Integer> getGenresByFilmId(long id) {
        return jdbc.queryForList(GET_GENRES_BY_FILM_ID, Integer.class, id);
    }

    @Override
    public List<Integer> getBestFilmId(long count) {
        return jdbc.queryForList(GET_BEST_FILM_ID, Integer.class, count);
    }

    @Override
    public List<Long> getLikes(long filmId) {
        return jdbc.queryForList(GET_LIKES, Long.class, filmId);
    }

    @Override
    public void deleteAllLikes(long filmId) {
        jdbc.update(DELETE_ALL_LIKES, filmId);
    }

    @Override
    public void addLike(long filmId, long userId) {
        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(ADD_LIKE);
            ps.setLong(1, filmId);
            ps.setLong(2, userId);
            return ps;
        });
    }

    @Override
    public List<GenreType> getAll() {
        return jdbc.query(GET_ALL_GENRES, this::mapRow);
    }

    @Override
    public List<GenreType> getGenreTypes(long id) {
        return jdbc.query(GET_GENRE_TYPE_BY_FILM_ID, this::mapRow, id);
    }

    private GenreType mapRow(ResultSet rs, int rowNum) throws SQLException {
        return GenreType.builder()
                .id(rs.getInt("genre_id"))
                .name(rs.getString("type"))
                .build();
    }
}
