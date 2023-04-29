package ru.yandex.practicum.filmorate.storage.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.service.enums.EventType;
import ru.yandex.practicum.filmorate.service.enums.OperationType;
import ru.yandex.practicum.filmorate.storage.FeedStorage;
import ru.yandex.practicum.filmorate.storage.dao.constants.SQLScripts;

@Component
@Slf4j
public class FeedDbStorage implements FeedStorage {
    private final JdbcTemplate jdbcTemplate;

    public FeedDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addFeed(Integer userId, Integer entityId, EventType eventType, OperationType operationType) {
        String sqlAddFeed = SQLScripts.ADD_FEED;
        long timestamp = System.currentTimeMillis();
        jdbcTemplate.update(sqlAddFeed, userId, timestamp,
                eventType.name(), operationType.name(), entityId);
    }
}
