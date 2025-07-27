package ru.yandex.practicum.filmorate.storage.friendsStatus;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class FriendsStatusDbStorage implements FriendsStatusStorage {
    private final JdbcTemplate jdbc;

    private static final String GET_FRIENDS_ID = "SELECT friend_id FROM friends_status WHERE user_id = ?";
    private static final String DELETE_ALL_FRIENDS = "DELETE FROM friends_status WHERE user_id = ?";


    @Override
    public Set<Long> getFriendsId(long userId) {
        return new HashSet<>(jdbc.queryForList(GET_FRIENDS_ID, Long.class, userId));
    }

    @Override
    public void deleteAllFriends(long userId) {
        jdbc.update(DELETE_ALL_FRIENDS, userId);
    }
}
