package ru.yandex.practicum.filmorate.manager;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class UserManager {
    private final Map<Long, User> users = new HashMap<>();
    private long userId = 0;

    public User create(User user) {
        user.validName();
        log.info("Имя пользователя {} после валидации.", user.getName());
        user.setId(++userId);
        try {
            users.put(user.getId(), user);
            log.info("Пользователь {} успешно сохранён.", user.getId());
        } catch (Exception e) {
            log.error("Ошибка при сохранении пользователя.", e);
        }
        return user;
    }

    public User update(User newUser) {
        if (newUser.getId() == null) {
            log.error("Поле с Id пользователя не должно быть пустым.");
            throw new ValidationException("Id должен быть указан.");
        }

        if (!users.containsKey(newUser.getId())) {
            log.error("Пользователь с таким Id - {} не найден в таблице.", newUser.getId());
            throw new ValidationException("Пользователя с таким Id нет.");
        }
        newUser.validName();
        log.info("Обновленное имя пользователя {} после валидации.", newUser.getName());
        try {
            users.put(newUser.getId(), newUser);
            log.info("Данные пользователя {} успешно обновлены.", newUser.getId());
        } catch (Exception e) {
            log.error("Ошибка при сохранении обновлённых данных пользователя.", e);
        }
        return newUser;
    }

    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }
}
