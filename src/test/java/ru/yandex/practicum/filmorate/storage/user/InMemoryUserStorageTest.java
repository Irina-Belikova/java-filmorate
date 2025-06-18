package ru.yandex.practicum.filmorate.storage.user;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryUserStorageTest {
    private final UserService userStorage = new UserService(new InMemoryUserStorage());

    @Test
    void shouldBeCreateAndUpdate() {
        User user = User.builder().email("pochta@mail.ru").login("login")
                .birthday(LocalDate.of(2000, 5, 5)).build();
        userStorage.create(user);
        List<User> users = userStorage.getAll();
        assertEquals(1, users.size(), "Пользователь не добавился в таблицу.");
        assertEquals(user.getName(), user.getLogin(), "Имени не присвоился логин.");

        user.setLogin("new login");
        userStorage.update(user);
        users = userStorage.getAll();
        User updateUser = users.getFirst();
        assertEquals(updateUser.getLogin(), user.getLogin(), "Данные в таблице не обновились.");
    }
}
/* надеюсь, правильно поняла, что если в классе используются аннотации для валидации полей класса,
то в тестах напрямую не сможем проверить работу аннотаций по выбрасыванию исключений, т.к. в этом
тестовом классе нет контекста Spring Boot
 */