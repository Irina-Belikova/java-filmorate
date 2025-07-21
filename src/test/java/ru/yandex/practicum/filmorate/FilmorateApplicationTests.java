package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.friendsStatus.FriendsStatusDbStorage;
import ru.yandex.practicum.filmorate.storage.friendsStatus.FriendsStatusStorage;
import ru.yandex.practicum.filmorate.storage.genreType.GenreTypeDbStorage;
import ru.yandex.practicum.filmorate.storage.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.storage.mappers.GenreTypeRowMapper;
import ru.yandex.practicum.filmorate.storage.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({UserDbStorage.class, FriendsStatusDbStorage.class,
        FilmDbStorage.class, MpaDbStorage.class, GenreTypeDbStorage.class,
        UserRowMapper.class, FilmRowMapper.class, GenreTypeRowMapper.class})
class FilmorateApplicationTests {
    private final UserDbStorage userStorage;
    private final FriendsStatusStorage friendsStatusStorage;
    private final FilmDbStorage filmDbStorage;

    @Test
    public void testUser() {

        Optional<User> userOptional = Optional.ofNullable(userStorage.getUserById(1));
        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user)
                                .hasFieldOrPropertyWithValue("id", 1L)
                );

        Set<Long> friendsIds = friendsStatusStorage.getFriendsId(1L);
        assertThat(friendsIds)
                .isNotNull()
                .hasSize(2);

        friendsStatusStorage.deleteAllFriends(1L);
        assertThat(friendsStatusStorage.getFriendsId(1L)).isEmpty();
    }

    @Test
    public void testFilm() {
        Optional<Film> filmOptional = Optional.ofNullable(filmDbStorage.getFilmById(1));
        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film -> {
                    assertThat(film.getId()).isEqualTo(1L);
                    assertThat(film.getGenres()).hasSize(3);
                });
    }
}