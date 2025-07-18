package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.friendsStatus.FriendsStatusStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository("userDbStorage")
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbc;
    private final FriendsStatusStorage fs;

    private static final String FIND_BY_ID_QUERY = "SELECT * FROM users WHERE user_id = ?";
    private static final String FIND_ALL_QUERY = "SELECT * FROM users";
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
        fs.deleteAllFriends(newUser.getId());
        newUser.getFriendsId().forEach(friendId -> fs.addFriendId(newUser.getId(), friendId));
        return newUser;
    }

    @Override
    public List<User> getAll() {
        return jdbc.query(FIND_ALL_QUERY, this::mapRow);
    }

    @Override
    public void deleteById(long id) {
        jdbc.update(DELETE_BY_ID_QUERY, id);
    }

    @Override
    public User getUserById(long id) {
        try {
            return jdbc.queryForObject(FIND_BY_ID_QUERY, this::mapRow, id);
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

    private User mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        long userId = resultSet.getLong("user_id");
        Set<Long> friends = fs.getFriendsId(userId);
        User user = User.builder()
                .id(resultSet.getLong("user_id"))
                .name(resultSet.getString("name"))
                .login(resultSet.getString("login"))
                .email(resultSet.getString("email"))
                .birthday(resultSet.getDate("birthday").toLocalDate())
                .build();
        user.getFriendsId().addAll(friends);
        return user;
    }
}
