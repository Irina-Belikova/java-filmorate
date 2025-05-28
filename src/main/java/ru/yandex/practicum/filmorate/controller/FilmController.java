package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.manager.FilmManager;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmManager fm = new FilmManager();

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        log.info("Получен запрос на создание фильма: {}", film);
        return fm.create(film);
    }

    @PutMapping
    public Film upDateFilm(@Valid @RequestBody Film newFilm) {
        log.info("Получен запрос на обновление фильма: {}", newFilm);
        return fm.update(newFilm);
    }

    @GetMapping
    public List<Film> getAll() {
        return fm.getAll();
    }
}
