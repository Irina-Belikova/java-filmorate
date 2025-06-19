package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ServiceErrorException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Film create(Film film) {
        try {
            return filmStorage.create(film);
        } catch (Exception e) {
            throw new ServiceErrorException("Ошибка сохранения фильма на сервере.");
        }
    }

    public Film update(Film newFilm) {
        try {
            return filmStorage.update(newFilm);
        } catch (Exception e) {
            throw new ServiceErrorException("Ошибка сохранения обновлений фильма на сервере.");
        }
    }

    public List<Film> getAll() {
        return filmStorage.getAll();
    }

    public void deleteById(long id) {
        filmStorage.deleteById(id);
    }

    public Film addNewLike(long id, long userId) {
        Film film = filmStorage.getFilmById(id);

        if (!film.addLike(userId)) {
            throw new DuplicatedDataException("Данный пользователь уже поставил лайк этому фильму.");
        }
        return filmStorage.update(film);
    }

    public void removeLike(long id, long userId) {
        Film film = filmStorage.getFilmById(id);

        if (!film.deleteLike(userId)) {
            throw new ValidationException("Данный пользователь не ставил лайк этому фильму.");
        }
        filmStorage.update(film);
    }

    public List<Film> getBestFilms(long count) {
        return filmStorage.getBestFilms(count);
    }

    public void validateFilmForCreate(Film film) {
        if (film.getId() != null) {
            if (filmStorage.getFilmById(film.getId()) != null) {
                throw new ValidationException("Такой фильм уже существует.");
            }
        }
        if (!film.validDate()) {
            throw new ValidationException("Дата создания фильма должна быть позже 28 декабря 1895 г.");
        }
    }

    //Не стала эти методы валидации переносить в контроллер, чтобы там были только методы для обработки
    //эндпоинтов и чтобы там не пришлось создавать поле userService; сделала методы публичными и в контроллере
    //через filmService.метод() происходит вся валидация входящих данных
    public void validateFilmForUpdate(Film newFilm) {
        if (newFilm.getId() == null) {
            throw new ValidationException("Id фильма должен быть указан.");
        }
        if (filmStorage.getFilmById(newFilm.getId()) == null) {
            throw new NotFoundException("Фильма с таким Id нет.");
        }
        if (!newFilm.validDate()) {
            throw new ValidationException("Дата создания фильма должна быть позже 28 декабря 1895 г.");
        }
    }

    public void checkFilmExists(long id) {
        if (filmStorage.getFilmById(id) == null) {
            throw new NotFoundException("Фильма с таким id и так нет.");
        }
    }

    public void validateUserLike(long id, long userId) {
        Film film = filmStorage.getFilmById(id);
        User user = userStorage.getUserById(userId);
        if (film == null) {
            throw new NotFoundException("Фильма с таким id нет.");
        }
        if (user == null) {
            throw new NotFoundException("Пользователя с таким id нет.");
        }
    }
}
