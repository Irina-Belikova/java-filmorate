package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.manager.UserManager;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserManager um = new UserManager();

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        log.info("Получен запрос на создание пользователя: {}", user);
        return um.create(user);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User newUser) {
        log.info("Получен запрос на обновление пользователя: {}", newUser);
        return um.update(newUser);
    }

    @GetMapping
    public List<User> getAll() {
        return um.getAll();
    }
}
