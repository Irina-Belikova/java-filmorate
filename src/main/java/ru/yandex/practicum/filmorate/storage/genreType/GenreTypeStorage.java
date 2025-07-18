package ru.yandex.practicum.filmorate.storage.genreType;

import ru.yandex.practicum.filmorate.model.GenreType;

import java.util.List;

public interface GenreTypeStorage {

    GenreType findNameById(int id);

    void insertGenreData(long filmId, int genreId);

    void deleteAllGenres(long filmId);

    List<Integer> getGenresByFilmId(long id);

    List<Integer> getBestFilmId(long count);

    List<Long> getLikes(long filmId);

    void deleteAllLikes(long filmId);

    void addLike(long filmId, long userId);

    List<GenreType> getAll();

    List<GenreType> getGenreTypes(long id);
}
