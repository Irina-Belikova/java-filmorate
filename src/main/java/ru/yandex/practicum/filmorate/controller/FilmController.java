package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
@Validated
public class FilmController {
    private final FilmService filmService;


    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        log.info("Получен запрос на создание фильма: {}", film);
        filmService.validateFilmForCreate(film);
        return filmService.create(film);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film newFilm) {
        log.info("Получен запрос на обновление фильма: {}", newFilm);
        filmService.validateFilmForUpdate(newFilm);
        return filmService.update(newFilm);
    }

    @GetMapping
    public List<Film> getAll() {
        return filmService.getAll();
    }

    @DeleteMapping("/{id}")
    public String deleteFilmById(@PathVariable @Positive(message = "Некорректный id фильма.") Long id) {
        filmService.checkFilmExists(id);
        filmService.deleteById(id);
        return String.format("Фильм с id - %d успешно удалён.", id);
    }

    @GetMapping("/{id}")
    public FilmDto getFilmById(@PathVariable @Positive(message = "Некорректный id фильма.") long id) {
        log.info("Получен запрос на поиск фильма по id: {}", id);
        filmService.checkFilmExists(id);
        return filmService.getFilmById(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public Film addLike(@PathVariable @Positive(message = "Некорректный id фильма.") long id,
                        @PathVariable @Positive(message = "Некорректный id пользователя.") long userId) {
        filmService.validateUserLike(id, userId);
        return filmService.addNewLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public String removeLike(@PathVariable @Positive(message = "Некорректный id фильма.") long id,
                             @PathVariable @Positive(message = "Некорректный id пользователя.") long userId) {
        filmService.validateUserLike(id, userId);
        filmService.removeLike(id, userId);
        return "Лайк пользователя удалён.";
    }

    @GetMapping("/popular")
    public List<Film> getBestFilms(@RequestParam(defaultValue = "10") long count) {
        log.info("Получен запрос на получение списка лучших фильмов в количестве: {}", count);
        return filmService.getBestFilms(count);
    }

}
