package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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

    private List<GenreType> genres = new ArrayList<>();

    private Mpa mpa;

    public boolean validDate() {
        return releaseDate.isAfter(MOVIE_BIRTHDAY);
    }
}
