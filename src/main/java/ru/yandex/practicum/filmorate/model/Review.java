package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class Review {
    private int reviewId;
    private String content;
    private boolean isPositive;
    @NotNull
    private int userId;
    @NotNull
    private int filmId;
    private int useful;
}
