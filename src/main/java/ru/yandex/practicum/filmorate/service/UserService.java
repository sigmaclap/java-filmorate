package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.enums.EventType;
import ru.yandex.practicum.filmorate.service.enums.OperationType;
import ru.yandex.practicum.filmorate.storage.FeedStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class UserService {

    private final UserStorage userStorage;
    private final FeedStorage feedStorage;

    public UserService(UserStorage userStorage, FeedStorage feedStorage) {
        this.userStorage = userStorage;
        this.feedStorage = feedStorage;
    }

    public List<User> findUsers() {
        return userStorage.findUsers();
    }

    public User create(User user) {
        return userStorage.create(user);
    }

    public User update(User user) {
        return userStorage.update(user);
    }

    public User findUserById(Integer userId) {
        return userStorage.findUserById(userId);
    }

    public List<User> getFriendsUser(Integer userId) {
        return userStorage.getFriendsUser(userId);
    }

    public User addFriend(Integer userId, Integer friendId) {
        User friend = findUserById(friendId);
        if (friend != null) {
            Map<Long, Boolean> userFriend = friend.getFriendsList();
            userStorage.addFriend(userId, friendId, userFriend.containsKey(userId.longValue()));
            feedStorage.addFeed(userId, friendId, EventType.FRIEND, OperationType.ADD);
            return userStorage.findUserById(userId);
        } else {
            log.info("Пользователь с идентификатором {} не найден, лайк не добавлен.", userId);
            throw new UserNotFoundException("Пользователь не найден");
        }
    }

    public User removeFriendToUser(Integer userId, Integer friendId) {
        User friend = findUserById(friendId);
        if (friend != null) {
            Map<Long, Boolean> userFriend = friend.getFriendsList();
            userStorage.removeFriendToUser(userId, friendId, userFriend.containsKey(userId.longValue()));
            feedStorage.addFeed(userId, friendId, EventType.FRIEND, OperationType.REMOVE);
            return userStorage.findUserById(userId);
        } else {
            log.info("Пользователь с идентификатором {} не найден, удаление не выполнено.", userId);
            throw new UserNotFoundException("Пользователь не найден");
        }
    }

    public boolean removeUserById(Integer userId) {
        return userStorage.removeUserById(userId);
    }

    public List<User> commonFriends(Integer userId, Integer otherId) {
        return userStorage.commonFriends(userId, otherId);
    }

    public List<Film> getFilmRecommended(Integer userId) {
        return userStorage.getFilmRecommended(userId);
    }
}