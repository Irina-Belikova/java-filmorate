package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.constraints.Positive;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.validation.OnCreate;
import ru.yandex.practicum.filmorate.validation.OnUpdate;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
@Validated
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public User createUser(@Validated(OnCreate.class) @RequestBody User user) {
        log.info("Получен запрос на создание пользователя: {}", user);
        userService.validateUserForCreate(user);
        return userService.create(user);
    }

    @PutMapping
    public User updateUser(@Validated(OnUpdate.class) @RequestBody User newUser) {
        log.info("Получен запрос на обновление пользователя: {}", newUser);
        userService.validateUserForUpdate(newUser);
        return userService.update(newUser);
    }

    @GetMapping
    public List<User> getAll() {
        return userService.getAll();
    }

    @DeleteMapping("/{id}")
    public String deleteUserById(@PathVariable @Positive(message = "Некорректный id пользователя.") Long id) {
        userService.checkUserExists(id);
        userService.deleteById(id);
        return String.format("Данные пользователя с id - %d успешно удалены.", id);
    }

    @PutMapping("{id}/friends/{friendId}")
    public User addFriend(@PathVariable @Positive(message = "Некорректный id первого пользователя.") Long id,
                          @PathVariable @Positive(message = "Некорректный id второго пользователя.") Long friendId) {
        log.info("Получен запрос на добавление в друзья пользователей с id: {} и {}", id, friendId);
        userService.validateFriend(id, friendId);
        return userService.addNewFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public ResponseEntity<Void> removeFriend(@PathVariable @Positive(message = "Некорректный id первого пользователя.") Long id,
                                             @PathVariable @Positive(message = "Некорректный id второго пользователя.") Long friendId) {
        log.info("Получен запрос на удаление пользователей с id: {} и {}", id, friendId);
        userService.validateFriend(id, friendId);
        userService.removeFriend(id, friendId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/friends")
    public List<User> getAllFriends(@PathVariable @Positive(message = "Некорректный id пользователя.") Long id) {
        log.info("Получен запрос на получение списка всех друзей у пользователя {}", id);
        userService.checkUserExists(id);
        log.info("Получаемый список друзей {}", userService.getAllFriends(id));
        return userService.getAllFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getMutualFriends(@PathVariable @Positive(message = "Некорректный id первого пользователя.") long id,
                                       @PathVariable @Positive(message = "Некорректный id второго пользователя.") long otherId) {
        userService.validateMutualFriends(id, otherId);
        return userService.getMutualFriends(id, otherId);
    }
}
