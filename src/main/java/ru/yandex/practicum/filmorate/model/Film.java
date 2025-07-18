package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.dto.MpaDto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Film {
    private static final LocalDate MOVIE_BIRTHDAY = LocalDate.of(1895, 12, 28);

    private Long id;

    @NotBlank
    private String name;

    @Size(min = 1, max = 200, message = "Описание должно содержать от 1 до 200 символов.")
    private String description;

    private LocalDate releaseDate;

    @Positive
    private Long duration;

    @JsonIgnore
    private final Set<Long> likeUserId = new HashSet<>();

    private List<GenreDto> genres = new ArrayList<>();

    private MpaDto mpa;

    public boolean validDate() {
        return releaseDate.isAfter(MOVIE_BIRTHDAY);
    }

    public boolean addLike(long userId) {
        return likeUserId.add(userId);
    }

    public boolean deleteLike(long userId) {
        return likeUserId.remove(userId);
    }
}
