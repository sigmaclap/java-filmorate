package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@Service
public class UserService {

    private final UserStorage userStorage;

    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
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
        return userStorage.addFriend(userId, friendId);
    }

    public User removeFriendToUser(Integer userId, Integer friendId) {
        return userStorage.removeFriendToUser(userId, friendId);
    }

    public List<User> commonFriends(Integer userId, Integer otherId) {
        return userStorage.commonFriends(userId, otherId);
    }
}
