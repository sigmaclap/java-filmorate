package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.time.LocalDate;

@Data
@Builder
public class User {
    private Integer id;
    @NotNull
    @Email
    private final String email;
    @NotBlank
    private String login;
    private String name;
    @Past
    private final LocalDate birthday;
}
