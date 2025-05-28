package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private Long id;

    @Email
    private String email;

    @NotBlank
    private String login;

    private String name;

    @Past
    private LocalDate birthday;

    public void validName() {
        if (this.name == null) {
            this.name = this.login;
        }
    }
}
