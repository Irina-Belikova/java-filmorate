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
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private Long id;

    @NotBlank(groups = OnCreate.class) //поле не пустое только при создании объекта, при обновлении не участвует
    @Email(groups = {OnCreate.class, OnUpdate.class})
    //проверка формату email и при создании, и при обновлении; при обновлении пропускает пустое поле
    private String email;

    @NotBlank
    private String login;

    private String name;

    @PastOrPresent(groups = {OnCreate.class, OnUpdate.class})
    private LocalDate birthday;

    private Set<Long> friendsId;

    public void validName() {
        if (this.name == null) {
            this.name = this.login;
        }
    }

    public boolean addFriend(User friend) {
        if (friendsId == null) {
            friendsId = new HashSet<>();
        }
        return this.friendsId.add(friend.getId());
    }

    public boolean deleteFriend(User friend) {
        return this.friendsId.remove(friend.getId());
    }
}
