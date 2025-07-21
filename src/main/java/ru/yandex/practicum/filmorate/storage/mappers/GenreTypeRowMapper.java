package ru.yandex.practicum.filmorate.storage.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.GenreType;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class GenreTypeRowMapper implements RowMapper<GenreType> {
    @Override
    public GenreType mapRow(ResultSet rs, int rowNum) throws SQLException {
        return GenreType.builder()
                .id(rs.getInt("genre_id"))
                .name(rs.getString("type"))
                .build();
    }
}
