package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ServiceErrorException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.GenreType;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genreType.GenreTypeStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final MpaStorage mpaStorage;
    private final GenreTypeStorage genreStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage,
                       MpaStorage mpaStorage, GenreTypeStorage genreStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.mpaStorage = mpaStorage;
        this.genreStorage = genreStorage;
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

    public Film getFilmById(long id) {
        return filmStorage.getFilmById(id);
    }

    public void deleteById(long id) {
        filmStorage.deleteById(id);
    }

    public Film addNewLike(long id, long userId) {
        try {
            filmStorage.addLike(id, userId);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicatedDataException("Данный пользователь уже поставил лайк этому фильму.");
        }
        return filmStorage.getFilmById(id);
    }

    public void removeLike(long id, long userId) {
        if (filmStorage.removeLike(id, userId) == 0) {
            throw new ValidationException("Данный пользователь не ставил лайк этому фильму.");
        }
    }

    public List<Film> getBestFilms(long count) {
        return filmStorage.getBestFilms(count);
    }

    //Не стала эти методы валидации переносить в контроллер, чтобы там были только методы для обработки
    //эндпоинтов и чтобы там не пришлось создавать поле userService; сделала методы публичными и в контроллере
    //через filmService.метод() происходит вся валидация входящих данных
    public void validateFilmForCreate(Film film) {
        if (film.getId() != null) {
            if (filmStorage.getFilmById(film.getId()) != null) {
                throw new ValidationException("Такой фильм уже существует.");
            }
        }
        if (!film.validDate()) {
            throw new ValidationException("Дата создания фильма должна быть позже 28 декабря 1895 г.");
        }
        checkMpaAndGenre(film);
    }

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
        checkMpaAndGenre(newFilm);
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

    private void checkMpaAndGenre(Film film) {
        try {
            mpaStorage.findNameById(film.getMpa().getId());
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Id возрастного рейтинга должен быть в диапазоне от 1 до 5.");
        }
        for (GenreType genre : film.getGenres()) {
            List<GenreType> allGenres = genreStorage.getAll();
            if (!allGenres.contains(genre)) {
                throw new NotFoundException("Id жанра должен быть в диапазоне от 1 до 6.");
            }
        }
    }
}
