package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
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
