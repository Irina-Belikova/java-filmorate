package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();
    private long userId = 0;
    private final Set<String> emails = new HashSet<>();

    @Override
    public User create(User user) {
        user.setId(++userId);
        users.put(user.getId(), user);
        emails.add(user.getEmail());
        return user;
    }

    @Override
    public User update(User newUser) {
        User oldUser = getUserById(newUser.getId());
        emails.remove(oldUser.getEmail());
        emails.add(newUser.getEmail());
        users.put(newUser.getId(), newUser);
        return newUser;
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public void deleteById(long id) {
        users.remove(id);
        emails.remove(getUserById(id).getEmail());
    }

    @Override
    public User getUserById(long id) {
        return users.get(id);
    }

    @Override
    public Set<String> getEmails() {
        return emails;
    }

    @Override
    public boolean checkEmail(String email) {
        return emails.contains(email);
    }
}
