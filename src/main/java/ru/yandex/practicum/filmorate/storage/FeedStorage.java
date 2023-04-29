package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.service.enums.EventType;
import ru.yandex.practicum.filmorate.service.enums.OperationType;

public interface FeedStorage {

    void addFeed(Integer userId, Integer entityId, EventType eventType, OperationType operationType);

}
