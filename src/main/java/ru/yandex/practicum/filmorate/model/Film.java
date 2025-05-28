package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Film {

    private Long id;

    @NotBlank
    private String name;

    @Size(min = 1, max = 200, message = "Описание должно содержать от 1 до 200 символов.")
    private String description;

    private LocalDate releaseDate;

    @Positive
    private Long duration;

    public boolean validDate() {
        LocalDate movieBirthday = LocalDate.of(1895, 12, 28);
        return this.releaseDate.isAfter(movieBirthday);
    }
}
