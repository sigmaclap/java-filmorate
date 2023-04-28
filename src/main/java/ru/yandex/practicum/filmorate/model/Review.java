package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
public class Review {

    private Integer reviewId;
    @NotBlank
    private String content;
    @NotNull
    private Boolean isPositive;

    @NotNull
    private Integer filmId;

    @NotNull
    private Integer userId;

    private int useful;
}
