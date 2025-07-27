package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class MpaDbStorage implements MpaStorage {
    private final JdbcTemplate jdbc;

    private static final String FIND_BY_ID = "SELECT * FROM MPA WHERE mpa_id = ?";
    public static final String GET_ALL_MPA = "SELECT * FROM MPA";

    @Override
    public Mpa findNameById(int id) {
        return jdbc.queryForObject(FIND_BY_ID, this::mapRow, id);
    }

    @Override
    public List<Mpa> getAll() {
        return jdbc.query(GET_ALL_MPA, this::mapRow);
    }

    private Mpa mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Mpa.builder()
                .id(rs.getInt("mpa_id"))
                .name(rs.getString("rating"))
                .build();
    }
}
