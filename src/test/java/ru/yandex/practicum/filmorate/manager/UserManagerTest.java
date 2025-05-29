package ru.yandex.practicum.filmorate.manager;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserManagerTest {
    private final UserManager um = new UserManager();

    @Test
    void shouldBeCreateAndUpdate() {
        User user = User.builder().email("pochta@mail.ru").login("login")
                .birthday(LocalDate.of(2000, 5, 5)).build();
        um.create(user);
        List<User> users = um.getAll();
        assertEquals(1, users.size(), "Пользователь не добавился в таблицу.");
        assertEquals(user.getName(), user.getLogin(), "Имени не присвоился логин.");

        user.setLogin("new login");
        um.update(user);
        users = um.getAll();
        User updateUser = users.getFirst();
        assertEquals(updateUser.getLogin(), user.getLogin(), "Данные в таблице не обновились.");
    }
}
/* надеюсь, правильно поняла, что если в классе используются аннотации для валидации полей класса,
то в тестах напрямую не сможем проверить работу аннотаций по выбрасыванию исключений, т.к. в этом
тестовом классе нет контекста Spring Boot
 */