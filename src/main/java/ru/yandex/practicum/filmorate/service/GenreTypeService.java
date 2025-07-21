package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.GenreType;
import ru.yandex.practicum.filmorate.storage.genreType.GenreTypeStorage;

import java.util.List;

@Service
public class GenreTypeService {
    private final GenreTypeStorage genreStorage;

    @Autowired
    public GenreTypeService(GenreTypeStorage genreStorage) {
        this.genreStorage = genreStorage;
    }

    public List<GenreType> getAll() {
        return genreStorage.getAll();
    }

    public GenreType getGenreById(int genreId) {
        return genreStorage.findNameById(genreId);
    }

    //пробовала добавлять аннотации в классе GenreType @Min(1) и @Max(6), но при ошибке они возвращают
    //код 400, а тест проверяет код 404. Поэтому решила просто переделать запрос, не привязываясь к цифрам,
    //так как список жанров может измениться
    public void validateGenreId(int id) {
        try {
            genreStorage.findNameById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Жанра с таким id нет в таблице.");
        }
    }
}
