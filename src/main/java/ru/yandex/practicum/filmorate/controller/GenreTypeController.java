package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.constraints.Positive;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.GenreType;
import ru.yandex.practicum.filmorate.service.GenreTypeService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/genres")
@Validated
public class GenreTypeController {
    private final GenreTypeService genreService;

    @Autowired
    public GenreTypeController(GenreTypeService genreService) {
        this.genreService = genreService;
    }

    @GetMapping
    public List<GenreType> getAll() {
        log.info("Получен запрос на вывод списка всех жанров.");
        return genreService.getAll();
    }

    @GetMapping("/{id}")
    public GenreType getGenreById(@PathVariable @Positive(message = "Некорректный id жанра.") int id) {
        log.info("Поступил запрос на получение имени жанра по его id.");
        genreService.validateGenreId(id);
        return genreService.getGenreById(id);
    }
}
