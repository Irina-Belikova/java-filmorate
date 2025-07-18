package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.dto.MpaDto;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.GenreType;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.genreType.GenreTypeStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.sql.*;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Repository("filmDbStorage")
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbc;
    private final MpaStorage ms;
    private final GenreTypeStorage gts;

    private static final String INSERT_FILM_DATA = """
            INSERT INTO films (name, description, release_date, duration, mpa_id)
            VALUES (?, ?, ?, ?, ?)""";
    private static final String UPDATE_FILM_QUERY = """
            UPDATE films
            SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ?
            WHERE film_id = ?""";
    private static final String FIND_FILM_BY_ID = "SELECT * FROM films WHERE film_id = ?";
    private static final String FIND_ALL_FILMS = "SELECT * FROM films";
    public static final String DELETE_BY_ID = "DELETE FROM films WHERE film_id = ?";

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
        film.getGenres().forEach(genre -> gts.insertGenreData(film.getId(), genre.getId()));
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
        gts.deleteAllGenres(newFilm.getId());
        List<GenreDto> genres = newFilm.getGenres();

        if (!genres.isEmpty()) {
            genres.forEach(genre -> gts.insertGenreData(newFilm.getId(), genre.getId()));
        }
        gts.deleteAllLikes(newFilm.getId());
        Set<Long> likeUserId = newFilm.getLikeUserId();

        if (!likeUserId.isEmpty()) {
            likeUserId.forEach(like -> gts.addLike(newFilm.getId(), like));
        }
        return newFilm;
    }

    @Override
    public List<Film> getAll() {
        return jdbc.query(FIND_ALL_FILMS, this::mapRow);
    }

    @Override
    public void deleteById(long id) {
        jdbc.update(DELETE_BY_ID, id);
    }

    @Override
    public Film getFilmById(long id) {
        try {
            return jdbc.queryForObject(FIND_FILM_BY_ID, this::mapRow, id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public List<Film> getBestFilms(long count) {
        return gts.getBestFilmId(count).stream()
                .map(this::getFilmById)
                .collect(Collectors.toList());
    }

    public FilmDto getFilmDtoById(long id) {
        return jdbc.queryForObject(FIND_FILM_BY_ID, this::mapRowDto, id);
    }

    private FilmDto mapRowDto(ResultSet resultSet, int rowNum) throws SQLException {
        Mpa mpa = ms.findNameById(resultSet.getInt("mpa_id"));
        List<GenreType> genres = gts.getGenreTypes(resultSet.getLong("film_id"));
        return FilmDto.builder()
                .id(resultSet.getLong("film_id"))
                .name(resultSet.getString("name"))
                .description(resultSet.getString("description"))
                .releaseDate(resultSet.getDate("release_date").toLocalDate())
                .duration(resultSet.getLong("duration"))
                .mpa(mpa)
                .genres(genres)
                .build();
    }

    private Film mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Film film = Film.builder()
                .id(resultSet.getLong("film_id"))
                .name(resultSet.getString("name"))
                .description(resultSet.getString("description"))
                .releaseDate(resultSet.getDate("release_date").toLocalDate())
                .duration(resultSet.getLong("duration"))
                .build();

        MpaDto mpa = new MpaDto();
        mpa.setId(resultSet.getInt("mpa_id"));
        film.setMpa(mpa);

        List<Integer> genresId = gts.getGenresByFilmId(film.getId());
        List<GenreDto> genres = genresId.stream()
                .map(GenreDto::new)
                .collect(Collectors.toList());

        film.setGenres(genres);
        gts.getLikes(film.getId()).forEach(film::addLike);
        return film;
    }
}
