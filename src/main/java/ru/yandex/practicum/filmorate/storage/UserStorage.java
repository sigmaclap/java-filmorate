package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Map;

public interface UserStorage {
    List<User> findUsers();

    User create(User user);

    Map<Integer, User> getMapUsers();

    User update(User user);

    User findUserById(Integer userId);

    User addFriend(Integer userId, Integer friendId);

    List<User> getFriendsUser(Integer userId);

    User removeFriendToUser(Integer userId, Integer friendId);

    List<User> commonFriends(Integer userId, Integer otherId);

}
