package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }


    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        log.info("Получен запрос на создание фильма: {}", film);
        return filmService.create(film);
    }

    @PutMapping
    public Film upDateFilm(@Valid @RequestBody Film newFilm) {
        log.info("Получен запрос на обновление фильма: {}", newFilm);
        return filmService.update(newFilm);
    }

    @GetMapping
    public List<Film> getAll() {
        return filmService.getAll();
    }

    @DeleteMapping("/{id}")
    public String deleteFilmById(@PathVariable Long id) {
        if (id <= 0) {
            throw new ValidationException("Некорректный id фильма.");
        }
        filmService.deleteById(id);
        return String.format("Фильм с id - %d успешно удалён.", id);
    }

    @PutMapping("/{id}/like/{userId}")
    public String addLike(@PathVariable long id, @PathVariable long userId) {
        if (id <= 0) {
            throw new ValidationException("Некорректный id фильма.");
        }
        if (userId <= 0) {
            throw new ValidationException("Некорректный id пользователя.");
        }
        filmService.addNewLike(id, userId);
        return "Фильму добавлен лайк.";
    }

    @DeleteMapping("/{id}/like/{userId}")
    public String removeLike(@PathVariable long id, @PathVariable long userId) {
        if (id <= 0) {
            throw new ValidationException("Некорректный id фильма.");
        }
        if (userId <= 0) {
            throw new ValidationException("Некорректный id пользователя.");
        }
        filmService.removeLike(id, userId);
        return "Лайк пользователя удалён.";
    }

    @GetMapping("/popular")
    public List<Film> getBestFilms(@RequestParam(defaultValue = "10") long count) {
        log.info("Получен запрос на получение списка лучших фильмов в количестве: {}", count);
        return filmService.getBestFilms(count);
    }
}
