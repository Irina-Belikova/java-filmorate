package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
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

    public void validateGenreId(int id) {
        if (id < 1 || id > 6) {
            throw new NotFoundException("Id жанра должен быть в диапазоне от 1 до 6.");
        }
    }
}
