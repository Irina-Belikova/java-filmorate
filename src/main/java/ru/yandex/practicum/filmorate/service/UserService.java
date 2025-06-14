package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserService(InMemoryUserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User create(User user) {
        return userStorage.create(user);
    }

    public User update(User newUser) {
        return userStorage.update(newUser);
    }

    public List<User> getAll() {
        return userStorage.getAll();
    }

    public void deleteById(long id) {
        userStorage.deleteById(id);
    }

    public void addNewFriend(long id, long friendId) {
        if (id == friendId) {
            throw new ValidationException("Нельзя добавить себя самого в друзья");
        }

        User user = userStorage.getUserById(id);
        User friend = userStorage.getUserById(friendId);
        log.info("Данные пользователя user: {}", user);
        log.info("Данные пользователя friend: {}", friend);

        if (!user.addFriend(friend) || !friend.addFriend(user)) {
            throw new DuplicatedDataException("Пользователи с такими id уже в друзьях.");
        }

        userStorage.update(user);
        userStorage.update(friend);
        log.info("Данные пользователя user с добавленным другом: {}, id друзей - {}", user, user.getFriendsId());
        log.info("Данные пользователя friend с добавленным другом: {}, id друзей -  {}", friend, friend.getFriendsId());
    }

    public void removeFriend(long id, long friendId) {
        if (id == friendId) {
            throw new ValidationException("Id пользователей не должны совпадать.");
        }

        User user = userStorage.getUserById(id);
        User friend = userStorage.getUserById(friendId);

        if (!user.deleteFriend(friend) || !friend.deleteFriend(user)) {
            throw new ValidationException("Пользователи и так не в друзьях друг у друга.");
        }

        userStorage.update(user);
        userStorage.update(friend);
    }

    public List<User> getAllFriends(long id) {
        User user = userStorage.getUserById(id);
        if (user.getFriendsId() == null) {
            return List.of(user);
        }
        return user.getFriendsId().stream()
                .map(userStorage::getUserById)
                .toList();
    }

    public List<Long> getMutualFriends(long id, long otherId) {
        if (id == otherId) {
            throw new ValidationException("Нельзя искать общих друзей у одного пользователя.");
        }
        Set<Long> userId = userStorage.getUserById(id).getFriendsId();
        Set<Long> otherUserId = userStorage.getUserById(otherId).getFriendsId();
        return userId.stream()
                .filter(otherUserId::contains)
                .collect(Collectors.toList());
    }
}
