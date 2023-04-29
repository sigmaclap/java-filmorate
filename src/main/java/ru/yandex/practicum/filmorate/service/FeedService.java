package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@Service
@Slf4j
public class FeedService {

    private final UserStorage userStorage;

    public FeedService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public List<Feed> getFeed(Integer userId) {
        return userStorage.getFeed(userId);
    }
}
