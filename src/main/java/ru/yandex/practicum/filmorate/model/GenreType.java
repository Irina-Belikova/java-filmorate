package ru.yandex.practicum.filmorate.model;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"id"})
public class GenreType {
    private int id;
    private String name;
}
