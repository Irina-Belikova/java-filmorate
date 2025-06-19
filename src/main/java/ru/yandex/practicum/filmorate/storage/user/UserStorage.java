package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Set;

public interface UserStorage {

    User create(User user);

    User update(User user);

    List<User> getAll();

    void deleteById(long id);

    User getUserById(long id);

    Set<String> getEmails();

    boolean checkEmail(String email);
}
