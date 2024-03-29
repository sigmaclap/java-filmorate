package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.restrictions.LocalDateRestrictions;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
public class Film {
    private Integer id;

    @NotBlank(message = "Name cannot be empty")
    private String name;

    @Size(max = 200, message = "Maximum length description 200 characters")
    private String description;

    @LocalDateRestrictions
    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING)
    private LocalDate releaseDate;

    @Positive(message = "должно быть больше 0")
    private long duration;


    private Mpa mpa;

    private Set<Genre> genres;

    private List<Director> directors;
}
