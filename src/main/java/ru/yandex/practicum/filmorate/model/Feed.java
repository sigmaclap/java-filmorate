package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.service.enums.EventType;
import ru.yandex.practicum.filmorate.service.enums.OperationType;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Builder
@Data
@AllArgsConstructor
public class Feed {
    private Integer eventId;
    @NotNull
    private Integer userId;
    @NotNull
    private Integer entityId;
    @NotNull
    private long timestamp;
    @NotBlank
    private EventType eventType;
    @NotBlank
    private OperationType operation;
}
