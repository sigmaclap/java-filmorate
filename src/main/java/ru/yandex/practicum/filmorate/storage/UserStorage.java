package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    List<User> findUsers();

    User create(User user);

    User update(User user);

    User findUserById(Integer userId);

    boolean addFriend(Integer userId, Integer friendId, boolean isFriendship);

    List<User> getFriendsUser(Integer userId);

    boolean removeFriendToUser(Integer userId, Integer friendId, boolean isFriendship);

    boolean removeUserById(Integer userId);

    List<User> commonFriends(Integer userId, Integer otherId);

    List<Film> getFilmRecommended(Integer userId);

    List<Feed> getFeed(Integer userId);

}
