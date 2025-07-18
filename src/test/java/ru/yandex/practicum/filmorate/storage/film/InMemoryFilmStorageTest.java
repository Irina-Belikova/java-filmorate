package ru.yandex.practicum.filmorate.storage.film;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.dto.MpaDto;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryFilmStorageTest {
    private final FilmController filmStorage = new FilmController(new FilmService(new InMemoryFilmStorage(), new InMemoryUserStorage()));

    @Test
    void shouldBeCreateAndUpdate() {
        Film film = Film.builder()
                .name("название")
                .description("описание")
                .releaseDate(LocalDate.of(2022, 1, 1))
                .duration(100L)
                .mpa(new MpaDto(1))
                .genres(new ArrayList<>())
                .build();
        filmStorage.createFilm(film);
        List<Film> films = filmStorage.getAll();
        assertEquals(1, films.size(), "Фильм не добавился в таблицу.");

        film.setDuration(150L);
        filmStorage.updateFilm(film);
        films = filmStorage.getAll();
        Film updateFilm = films.getFirst();
        assertEquals(updateFilm.getDuration(), film.getDuration(), "Данные в таблице не обновились.");

        film.setReleaseDate(LocalDate.of(1700, 1, 1));
        ValidationException exception = assertThrows(ValidationException.class, () -> filmStorage.updateFilm(film), "Исключение не выбрасывается.");
        assertEquals("Дата создания фильма должна быть позже 28 декабря 1895 г.", exception.getMessage());
    }
}
