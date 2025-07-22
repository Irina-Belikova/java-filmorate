package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.GenreType;
import ru.yandex.practicum.filmorate.storage.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.storage.mappers.GenreTypeRowMapper;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbc;
    private final FilmRowMapper filmRowMapper;
    private final GenreTypeRowMapper genreMapper;

    private static final String INSERT_FILM_DATA = """
            INSERT INTO films (name, description, release_date, duration, mpa_id)
            VALUES (?, ?, ?, ?, ?)""";
    private static final String INSERT_GENRES = """
            MERGE INTO genre
            USING (VALUES (?, ?)) AS source(film_id, genre_id)
                         ON genre.film_id = source.film_id AND genre.genre_id = source.genre_id
                         WHEN NOT MATCHED THEN
                         INSERT (film_id, genre_id) VALUES (source.film_id, source.genre_id)""";
    private static final String UPDATE_FILM_QUERY = """
            UPDATE films
            SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ?
            WHERE film_id = ?""";
    private static final String FIND_FILM_BY_ID = """
            SELECT f.*,
                   m.rating,
                FROM films AS f
                LEFT JOIN MPA AS m ON f.mpa_id = m.mpa_id
                WHERE f.film_id = ?""";
    private static final String FIND_ALL_FILMS = """
            SELECT f.*,
                   m.rating,
            FROM films AS f
            LEFT JOIN MPA AS m ON f.mpa_id = m.mpa_id""";
    public static final String DELETE_BY_ID = "DELETE FROM films WHERE film_id = ?";
    private static final String DELETE_ALL_GENRES = "DELETE FROM genre WHERE film_id = ?";
    private static final String GET_BEST_FILM_ID = """
            SELECT film_id
            FROM film_likes
            GROUP BY film_id
            ORDER BY COUNT(user_id) DESC
            LIMIT ?""";
    private static final String ADD_LIKE = """
            INSERT INTO film_likes (film_id, user_id)
            VALUES (?, ?)""";
    public static final String REMOVE_LIKE = "DELETE FROM film_likes WHERE film_id = ? AND user_id = ?";
    public static final String GET_GENRES_BY_FILM_ID = """
            SELECT gt.genre_id, gt.type
            FROM genre_type AS gt
            JOIN genre AS g ON gt.genre_id = g.genre_id
            WHERE g.film_id = ?""";
    private static final String ALL_GENRES_AND_FILMS_ID = """
            SELECT g.film_id, gt.genre_id, gt.type
            FROM genre_type AS gt
            JOIN genre AS g ON gt.genre_id = g.genre_id
            ORDER BY g.film_id""";

    @Override
    public Film create(Film film) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(INSERT_FILM_DATA, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, java.sql.Date.valueOf(film.getReleaseDate()));
            ps.setLong(4, film.getDuration());
            ps.setInt(5, film.getMpa().getId());
            return ps;
        }, keyHolder);

        if (keyHolder.getKey() == null) {
            throw new IllegalStateException("Не сгенерировался ключ пользователя");
        }
        long id = keyHolder.getKey().longValue();
        film.setId(id);
        film.getGenres().forEach(genre -> insertGenreData(film.getId(), genre.getId()));
        return film;
    }

    @Override
    public Film update(Film newFilm) {
        jdbc.update(
                UPDATE_FILM_QUERY,
                newFilm.getName(),
                newFilm.getDescription(),
                java.sql.Date.valueOf(newFilm.getReleaseDate()),
                newFilm.getDuration(),
                newFilm.getMpa().getId(),
                newFilm.getId());
        jdbc.update(DELETE_ALL_GENRES, newFilm.getId());
        List<GenreType> genres = newFilm.getGenres();

        if (!genres.isEmpty()) {
            genres.forEach(genre -> insertGenreData(newFilm.getId(), genre.getId()));
        }
        return newFilm;
    }

    @Override
    public List<Film> getAll() {
        List<Film> films = jdbc.query(FIND_ALL_FILMS, filmRowMapper);
        Map<Long, List<GenreType>> allGenres = new HashMap<>();

        jdbc.query(ALL_GENRES_AND_FILMS_ID, (rs, rowNuw) -> {
            GenreType genre = GenreType.builder()
                    .id(rs.getInt("genre_id"))
                    .name(rs.getString("type"))
                    .build();
            Long id = rs.getLong("film_id");
            if (allGenres.containsKey(id)) {
                allGenres.get(id).add(genre);
            } else {
                List<GenreType> genres = new ArrayList<>();
                genres.add(genre);
                allGenres.put(id, genres);
            }
            return genre;
        });

        films.forEach(film -> {
            if (allGenres.containsKey(film.getId())) {
                film.setGenres(allGenres.get(film.getId()));
            }
        });
        return films;
    }

    @Override
    public void deleteById(long id) {
        jdbc.update(DELETE_BY_ID, id);
    }

    @Override
    public Film getFilmById(long id) {
        try {
            Film film = jdbc.queryForObject(FIND_FILM_BY_ID, filmRowMapper, id);
            List<GenreType> genres = jdbc.query(GET_GENRES_BY_FILM_ID, genreMapper, id);
            if (!genres.isEmpty()) {
                film.setGenres(genres);
            }
            return film;
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public List<Film> getBestFilms(long count) {
        List<Integer> bestFilmId = jdbc.queryForList(GET_BEST_FILM_ID, Integer.class, count);

        return bestFilmId.stream()
                .map(this::getFilmById)
                .collect(Collectors.toList());
    }

    @Override
    public void insertGenreData(long filmId, int genreId) {
        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(INSERT_GENRES);
            ps.setLong(1, filmId);
            ps.setInt(2, genreId);
            return ps;
        });
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
    public int removeLike(long filmId, long userId) {
        return jdbc.update(REMOVE_LIKE, filmId, userId);
    }
}
