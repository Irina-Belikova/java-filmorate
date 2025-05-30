package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.validation.OnCreate;
import ru.yandex.practicum.filmorate.validation.OnUpdate;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private Long id;

    @Email(groups = OnCreate.class)
    private String email;

    @NotBlank
    private String login;

    private String name;

    @PastOrPresent(groups = {OnCreate.class, OnUpdate.class})
    private LocalDate birthday;

    public void validName() {
        if (this.name == null) {
            this.name = this.login;
        }
    }
}
