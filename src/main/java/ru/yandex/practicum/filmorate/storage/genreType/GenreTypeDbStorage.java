package ru.yandex.practicum.filmorate.storage.genreType;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.GenreType;
import ru.yandex.practicum.filmorate.storage.mappers.GenreTypeRowMapper;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class GenreTypeDbStorage implements GenreTypeStorage {
    private final JdbcTemplate jdbc;
    private final GenreTypeRowMapper mapper;

    private static final String FIND_NAME_BY_ID = "SELECT * FROM genre_type WHERE genre_id = ?";
    private static final String GET_ALL_GENRES = "SELECT * FROM genre_type";

    @Override
    public GenreType findNameById(int id) {
        return jdbc.queryForObject(FIND_NAME_BY_ID, mapper, id);
    }

    @Override
    public List<GenreType> getAll() {
        return jdbc.query(GET_ALL_GENRES, mapper);
    }
}
