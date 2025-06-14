package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.validation.OnCreate;
import ru.yandex.practicum.filmorate.validation.OnUpdate;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public User createUser(@Validated(OnCreate.class) @RequestBody User user) {
        log.info("Получен запрос на создание пользователя: {}", user);
        return userService.create(user);
    }

    @PutMapping
    public User updateUser(@Validated(OnUpdate.class) @RequestBody User newUser) {
        log.info("Получен запрос на обновление пользователя: {}", newUser);
        return userService.update(newUser);
    }

    @GetMapping
    public List<User> getAll() {
        return userService.getAll();
    }

    @DeleteMapping("/{id}")
    public String deleteUserById(@PathVariable Long id) {
        if (id <= 0) {
            throw new ValidationException("Некорректный id пользователя.");
        }
        userService.deleteById(id);
        return String.format("Данные пользователя с id - %d успешно удалены.", id);
    }

    @PutMapping("{id}/friends/{friendId}")
    public ResponseEntity<String> addFriend(@PathVariable Long id, @PathVariable Long friendId) {
        log.info("Получен запрос на добавление в друзья пользователей с id: {} и {}", id, friendId);

        if (id <= 0 || friendId <= 0) {
            throw new ValidationException("Некорректный id одного из пользователей.");
        }
        userService.addNewFriend(id, friendId);
        return ResponseEntity.status(HttpStatus.OK).body("Пользователи добавлены друг другу в друзья.");
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public ResponseEntity<String> removeFriend(@PathVariable Long id, @PathVariable Long friendId) {
        if (id <= 0 || friendId <= 0) {
            throw new ValidationException("Id одного из пользователей некорректен.");
        }
        userService.removeFriend(id, friendId);
        return ResponseEntity.status(HttpStatus.OK).body("Пользователи удалены из друзей друг у друга.");
    }

    @GetMapping("/{id}/friends")
    public List<User> getAllFriends(@PathVariable Long id) {
        if (id <= 0) {
            throw new ValidationException("Некорректный id пользователя.");
        }
        return userService.getAllFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<Long> getMutualFriends(@PathVariable long id, @PathVariable long otherId) {
        if (id <= 0 || otherId <= 0) {
            throw new ValidationException("Id одного из пользователей некорректен.");
        }
        return userService.getMutualFriends(id, otherId);
    }
}
