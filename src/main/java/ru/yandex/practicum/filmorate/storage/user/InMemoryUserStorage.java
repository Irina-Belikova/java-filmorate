package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ServiceErrorException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();
    private long userId = 0;

    @Override
    public User create(User user) {
        user.validName();
        log.info("Имя пользователя {} после валидации.", user.getName());
        user.setId(++userId);
        try {
            users.put(user.getId(), user);
            log.info("Пользователь {} успешно сохранён.", user.getId());
        } catch (Exception e) {
            log.error("Ошибка при сохранении пользователя.", e);
            throw new ServiceErrorException("Ошибка сохранения данных пользователя.");
        }
        return user;
    }

    @Override
    public User update(User newUser) {
        if (newUser.getId() == null) {
            log.error("Поле с Id пользователя не должно быть пустым.");
            throw new ValidationException("Id должен быть указан.");
        }

        if (!users.containsKey(newUser.getId())) {
            log.error("Пользователь с таким Id - {} не найден в таблице.", newUser.getId());
            throw new NotFoundException("Пользователя с таким Id нет.");
        }
        newUser.validName();
        log.info("Обновленное имя пользователя {} после валидации.", newUser.getName());
        try {
            users.put(newUser.getId(), newUser);
            log.info("Данные пользователя {} успешно обновлены.", newUser.getId());
        } catch (Exception e) {
            log.error("Ошибка при сохранении обновлённых данных пользователя.", e);
            throw new ServiceErrorException("Ошибка обновления данных пользователя.");
        }
        return newUser;
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public void deleteById(long id) {
        users.remove(id);
    }

    @Override
    public User getUserById(long id) {
        if (!users.containsKey(id)) {
            log.error("Пользователя с таким Id - {} нет в таблице.", id);
            throw new NotFoundException("Пользователя с таким Id нет.");
        }
        return users.get(id);
    }
}
