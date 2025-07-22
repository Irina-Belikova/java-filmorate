package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.mappers.UserRowMapper;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.*;

@Repository
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbc;
    private final UserRowMapper userRowMapper;

    private static final String FIND_BY_ID_QUERY = "SELECT * FROM users WHERE user_id = ?";
    private static final String FIND_ALL_QUERY = """
            SELECT u.*, fs.friend_id
            FROM users AS u
            LEFT JOIN friends_status AS fs ON u.user_id = fs.user_id
            ORDER BY u.user_id""";
    private static final String DELETE_BY_ID_QUERY = "DELETE FROM users WHERE user_id = ?";
    private static final String FIND_ALL_EMAILS = "SELECT email FROM users";
    private static final String CHECK_EMAIL = "SELECT EXISTS (SELECT 1 FROM users WHERE email = ?)";
    private static final String INSERT_QUERY = """
            INSERT INTO users (name, login, email, birthday)
            VALUES (?, ?, ?, ?)""";
    private static final String UPDATE_QUERY = """
            UPDATE users
            SET name = ?, login = ?, email = ?, birthday = ?
            WHERE user_id = ?""";
    private static final String DELETE_ALL_FRIENDS = "DELETE FROM friends_status WHERE user_id = ?";
    private static final String INSERT_FRIEND = "INSERT INTO friends_status (user_id, friend_id) VALUES (?, ?)";
    private static final String GET_FRIENDS_ID = "SELECT friend_id FROM friends_status WHERE user_id = ?";

    @Override
    public User create(User user) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(INSERT_QUERY, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getName());
            ps.setString(2, user.getLogin());
            ps.setString(3, user.getEmail());
            ps.setDate(4, java.sql.Date.valueOf(user.getBirthday()));
            return ps;
        }, keyHolder);

        if (keyHolder.getKey() == null) {
            throw new IllegalStateException("Не сгенерировался ключ пользователя");
        }
        long id = keyHolder.getKey().longValue();
        user.setId(id);
        return user;
    }

    @Override
    public User update(User newUser) {
        jdbc.update(
                UPDATE_QUERY,
                newUser.getName(),
                newUser.getLogin(),
                newUser.getEmail(),
                java.sql.Date.valueOf(newUser.getBirthday()),
                newUser.getId());
        jdbc.update(DELETE_ALL_FRIENDS, newUser.getId());
        newUser.getFriendsId().forEach(friendId -> jdbc.update(INSERT_FRIEND, newUser.getId(), friendId));
        return newUser;
    }

    //решила здесь использовать вариант одного запроса, чтобы было 2 способа решения подобных задач
    @Override
    public List<User> getAll() {
        Map<Long, User> allUsers = new HashMap<>();

        jdbc.query(FIND_ALL_QUERY, (rs, rowNum) -> {
            Long id = rs.getLong("user_id");
            User user = allUsers.get(id);
            if (user == null) {
                user = userRowMapper.mapRow(rs, rowNum);
                allUsers.put(id, user);
            } else {
                user.getFriendsId().add(rs.getLong("friend_id"));
            }
            return user;
        });
        return new ArrayList<>(allUsers.values());
    }

    @Override
    public void deleteById(long id) {
        jdbc.update(DELETE_BY_ID_QUERY, id);
    }

    @Override
    public User getUserById(long id) {
        try {
            User user = jdbc.queryForObject(FIND_BY_ID_QUERY, userRowMapper, id);
            Set<Long> friends = new HashSet<>(jdbc.queryForList(GET_FRIENDS_ID, Long.class, id));
                user.getFriendsId().addAll(friends);
            return user;
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public Set<String> getEmails() {
        return new HashSet<>(jdbc.queryForList(FIND_ALL_EMAILS, String.class));
    }

    @Override
    public boolean checkEmail(String email) {
        return jdbc.queryForObject(CHECK_EMAIL, Boolean.class, email);
    }
}
