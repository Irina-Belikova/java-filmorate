package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component("inMemoryFilmStorage")
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films = new HashMap<>();
    private long filmId = 0;

    @Override
    public Film create(Film film) {
        film.setId(++filmId);
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film update(Film newFilm) {
        films.put(newFilm.getId(), newFilm);
        return newFilm;
    }

    @Override
    public List<Film> getAll() {
        return new ArrayList<>(films.values());
    }

    @Override
    public void deleteById(long id) {
        films.remove(id);
    }

    @Override
    public Film getFilmById(long id) {
        return films.get(id);
    }

    @Override
    public List<Film> getBestFilms(long count) {
        return getAll().stream()
                .sorted((f1, f2) -> Integer.compare(f2.getLikeUserId().size(), f1.getLikeUserId().size()))
                .limit(count)
                .collect(Collectors.toList());
    }

    //не стала в этом классе реализовывать логику этого метода, т.к. его не было в прошлом ФЗ-11
    @Override
    public FilmDto getFilmDtoById(long id) {
        return null;
    }
}
