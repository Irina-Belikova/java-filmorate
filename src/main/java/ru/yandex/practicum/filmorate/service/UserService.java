package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ServiceErrorException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User create(User user) {
        try {
            return userStorage.create(user);
        } catch (Exception e) {
            throw new ServiceErrorException("Ошибка сохранения данных пользователя.");
        }
    }

    public User update(User newUser) {
        try {
            return userStorage.update(newUser);
        } catch (Exception e) {
            throw new ServiceErrorException("Ошибка обновления данных пользователя.");
        }
    }

    public List<User> getAll() {
        return userStorage.getAll();
    }

    public void deleteById(long id) {
        userStorage.deleteById(id);
    }

    public User addNewFriend(long id, long friendId) {
        User user = userStorage.getUserById(id);
        User friend = userStorage.getUserById(friendId);

        if (!user.addFriend(friend)) {
            throw new DuplicatedDataException("Пользователь с таким id уже в друзьях.");
        }
        userStorage.update(user);
        return user;
    }

    public void removeFriend(long id, long friendId) {
        User user = userStorage.getUserById(id);
        User friend = userStorage.getUserById(friendId);
        user.deleteFriend(friend);
        userStorage.update(user);
    }

    public List<User> getAllFriends(long id) {
        User user = userStorage.getUserById(id);
        return user.getFriendsId().stream()
                .map(userStorage::getUserById)
                .toList();
    }

    public List<User> getMutualFriends(long id, long otherId) {
        Set<Long> userId = userStorage.getUserById(id).getFriendsId();
        Set<Long> otherUserId = userStorage.getUserById(otherId).getFriendsId();
        return userId.stream()
                .filter(otherUserId::contains)
                .map(userStorage::getUserById)
                .collect(Collectors.toList());
    }

    //Не стала эти методы валидации переносить в контроллер, чтобы там были только методы для обработки
    //эндпоинтов; сделала методы публичными и в контроллере через userService.метод() происходит вся валидация входящих данных
    public void validateUserForCreate(User user) {
        if (user.getId() != null) {
            if (userStorage.getUserById(user.getId()) != null) {
                throw new ValidationException("Пользователь с таким id уже существует.");
            }
        }
        if (userStorage.getEmails().contains(user.getEmail())) {
            throw new ValidationException("Пользователь с таким email уже существует.");
        }
        user.validName();
    }

    public void validateUserForUpdate(User newUser) {
        if (newUser.getId() == null) {
            throw new ValidationException("Id пользователя должен быть указан.");
        }
        if (userStorage.getUserById(newUser.getId()) == null) {
            throw new NotFoundException("Пользователя с таким Id нет.");
        }
        User oldUser = userStorage.getUserById(newUser.getId());
        if (!oldUser.getEmail().equals(newUser.getEmail())) {
            if (userStorage.checkEmail(newUser.getEmail())) {
                throw new ValidationException("Пользователь с таким email уже существует.");
            }
        }
        newUser.validName();
    }

    public void checkUserExists(long id) {
        if (userStorage.getUserById(id) == null) {
            throw new NotFoundException("Пользователя с таким id нет.");
        }
    }

    public void validateFriend(long id, long friendId) {
        if (id == friendId) {
            throw new ValidationException("Нельзя добавить себя самого в друзья");
        }
        if (userStorage.getUserById(id) == null || userStorage.getUserById(friendId) == null) {
            throw new NotFoundException("Один из друзей не найден в сервисе.");
        }
    }

    public void validateMutualFriends(long id, long otherId) {
        if (id == otherId) {
            throw new ValidationException("Нельзя искать общих друзей у одного пользователя.");
        }
        if (userStorage.getUserById(id) == null || userStorage.getUserById(otherId) == null) {
            throw new NotFoundException("Один из друзей не найден в сервисе.");
        }
    }
}
