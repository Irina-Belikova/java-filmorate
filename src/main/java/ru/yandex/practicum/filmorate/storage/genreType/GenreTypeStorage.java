package ru.yandex.practicum.filmorate.storage.genreType;

import ru.yandex.practicum.filmorate.model.GenreType;

import java.util.List;

public interface GenreTypeStorage {

    GenreType findNameById(int id);

    List<GenreType> getAll();
}
