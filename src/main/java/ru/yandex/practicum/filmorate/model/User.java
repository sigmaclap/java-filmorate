package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Data
@Builder
public class User {
    private Integer id;
    private final Set<Long> friendsList = new HashSet<>();

    @NotNull(message = "не должно равняться null")
    @Email(message = "должно иметь формат адреса электронной почты")
    private final String email;
    @NotBlank(message = "не должно быть пустым")
    private String login;
    private String name;
    @Past(message = "должно содержать прошедшую дату")
    private final LocalDate birthday;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(friendsList, user.friendsList)
                && Objects.equals(email, user.email)
                && Objects.equals(login, user.login)
                && Objects.equals(name, user.name)
                && Objects.equals(birthday, user.birthday);
    }

    @Override
    public int hashCode() {
        return Objects.hash(friendsList, email, login, name, birthday);
    }
}
