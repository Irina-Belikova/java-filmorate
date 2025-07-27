package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.constraints.Positive;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/mpa")
@Validated
public class MpaController {
    private final MpaService mpaService;

    @Autowired
    public MpaController(MpaService mpaService) {
        this.mpaService = mpaService;
    }

    @GetMapping
    public List<Mpa> getAll() {
        log.info("Получен запрос на вывод списка всех возрастных рейтингов.");
        return mpaService.getAll();
    }

    @GetMapping("/{id}")
    public Mpa getMpaById(@PathVariable @Positive(message = "Некорректный id рейтинга.") int id) {
        log.info("Поступил запрос на получение возрастного рейтинга по его id.");
        mpaService.validateMpaId(id);
        return mpaService.getMpaById(id);
    }
}
