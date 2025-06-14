package ru.yandex.practicum.filmorate.storage.film;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryFilmStorageTest {
    private final FilmStorage filmStorage = new InMemoryFilmStorage();

    @Test
    void shouldBeCreateAndUpdate() {
        Film film = Film.builder().name("название").description("описание")
                .releaseDate(LocalDate.of(2022, 1, 1)).duration(100L).build();
        filmStorage.create(film);
        List<Film> films = filmStorage.getAll();
        assertEquals(1, films.size(), "Фильм не добавился в таблицу.");

        film.setDuration(150L);
        filmStorage.update(film);
        films = filmStorage.getAll();
        Film updateFilm = films.getFirst();
        assertEquals(updateFilm.getDuration(), film.getDuration(), "Данные в таблице не обновились.");

        film.setReleaseDate(LocalDate.of(1700, 1, 1));
        ValidationException exception = assertThrows(ValidationException.class, () -> filmStorage.update(film), "Исключение не выбрасывается.");
        assertEquals("Дата создания фильма должна быть позже 28 декабря 1895 г.", exception.getMessage());
    }
}
/* надеюсь, правильно поняла, что если в классе используются аннотации для валидации полей класса,
то в тестах напрямую не сможем проверить работу аннотаций по выбрасыванию исключений, т.к. в этом
тестовом классе нет контекста Spring Boot
 */