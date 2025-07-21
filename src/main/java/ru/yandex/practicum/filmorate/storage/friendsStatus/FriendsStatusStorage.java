package ru.yandex.practicum.filmorate.storage.friendsStatus;

import java.util.Set;

public interface FriendsStatusStorage {
    Set<Long> getFriendsId(long userId);

    void deleteAllFriends(long userId);
}
