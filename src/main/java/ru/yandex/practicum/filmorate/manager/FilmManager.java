package ru.yandex.practicum.filmorate.manager;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class FilmManager {
    private final Map<Long, Film> films = new HashMap<>();
    private long filmId = 0;


    public Film create(@Valid Film film) {
        if (!film.validDate()) {
            log.error("Дата создания фильма {} не может быть раньше 28.12.1895.", film.getReleaseDate());
            throw new ValidationException("Дата создания фильма должна быть позже 28 декабря 1895 г.");
        }
        film.setId(++filmId);
        log.info("Объект Film перед сохранением в хеш-мап: {}", film);
        try {
            films.put(film.getId(), film);
            log.info("Фильм успешно сохранён: {}", film);
        } catch (Exception e) {
            log.error("Ошибка при сохранении фильма.", e);
        }
        return film;
    }

    public Film update(@Valid Film newFilm) {
        if (newFilm.getId() == null) {
            log.error("Поле с Id фильма не должно быть пустым.");
            throw new ValidationException("Id должен быть указан.");
        }

        if (!films.containsKey(newFilm.getId())) {
            log.error("Фильм с таким Id - {} не найден в таблице.", newFilm.getId());
            throw new ValidationException("Фильма с таким Id нет.");
        }

        if (!newFilm.validDate()) {
            log.error("Новая дата создания фильма {} не может быть раньше 28.12.1895.", newFilm.getReleaseDate());
            throw new ValidationException("Дата создания фильма должна быть позже 28 декабря 1895 г.");
        }

        try {
            films.put(newFilm.getId(), newFilm);
            log.info("Данные фильма {} успешно обновлены.", newFilm.getName());
        } catch (Exception e) {
            log.error("Ошибка при сохранении обновлённых данных фильма.", e);
        }
        return newFilm;
    }

    public List<Film> getAll() {
        return new ArrayList<>(films.values());
    }
}
