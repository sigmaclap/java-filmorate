package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Data
@Builder
@AllArgsConstructor
public class User {
    private Integer id;

    @NotNull(message = "не должно равняться null")
    @Email(message = "должно иметь формат адреса электронной почты")
    private String email;

    @NotBlank(message = "не должно быть пустым")
    private String login;

    private String name;

    @Past(message = "должно содержать прошедшую дату")
    private LocalDate birthday;

    private final Map<Long, Boolean> friendsList = new HashMap<>();


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(email, user.email)
                && Objects.equals(login, user.login)
                && Objects.equals(name, user.name)
                && Objects.equals(birthday, user.birthday);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, login, name, birthday);
    }
}
