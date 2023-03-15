package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.InvalidDataException;
import ru.yandex.practicum.filmorate.exceptions.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private int idxUsers = 0;
    private final Map<Integer, User> users = new HashMap<>();

    public Map<Integer, User> getMapUsers() {
        return users;
    }

    @Override
    public List<User> findUsers() {
        log.debug("Количество пользователей: {}", users.size());
        return new ArrayList<>(users.values());
    }

    @Override
    public User create(User user) {
        int id = ++idxUsers;
        if (users.containsValue(user)) {
            log.error("Такой пользователь уже существует");
            throw new UserAlreadyExistException("Такой пользователь уже существует");
        } else if (user == null || user.toString().isEmpty()) {
            log.error("Пустое значение User");
            throw new InvalidDataException("Пустое значение User");
        } else {
            if (user.getName() == null || user.getName().isBlank()) {
                user.setName(user.getLogin());
                user.setId(id);
                users.put(id, user);
                log.debug("Пользователь успешно создан - Заданно имя = Логин: {}", user);
            } else {
                user.setId(id);
                users.put(id, user);
                log.debug("Пользователь успешно создан: {}", user);
            }
            return user;
        }
    }

    @Override
    public User update(User user) {
        if (user.getId() == null || !users.containsKey(user.getId())) {
            log.error("Невозможно обновить пользователя, не найден ID",
                    user.getId());
            throw new UserNotFoundException("Невозможно обновить пользователя, не найден ID");
        }
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
        users.remove(user.getId());
        users.put(user.getId(), user);
        log.debug("User updated: {}", user);
        return user;
    }

    @Override
    public User findUserById(Integer userId) {
        List<User> userList = new ArrayList<>(users.values());
        return userList.stream()
                .filter(p -> p.getId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new UserNotFoundException(String.format("Пользователь № %d не найден", userId)));
    }


    @Override
    public User addFriend(Integer userId, Integer friendId) {
        User user = users.get(userId);
        User userFriend = users.get(friendId);
        Long id = friendId.longValue();
        if (user.getFriendsList().contains(id)) {
            throw new UserAlreadyExistException("Данный пользователь уже есть у вас в друзьях");

        } else if (users.containsKey(userId) && users.containsKey(friendId)) {
            user.getFriendsList().add(id);
            userFriend.getFriendsList().add(userId.longValue());
            log.info("Пользователь с {} успешно добавлен", id);
            return user;
        } else {
            throw new UserNotFoundException("Неверно переданные данные");
        }
    }

    @Override
    public List<User> getFriendsUser(Integer id) {
        if (!users.containsKey(id)) {
            throw new UserNotFoundException("Пользователь не найден");
        }
        User user = users.get(id);
        if (user == null) {
            throw new UserNotFoundException("Пользователь с данным ID не найден");
        }
        return user.getFriendsList().stream()
                .mapToInt(Long::intValue)
                .filter(users::containsKey)
                .mapToObj(users::get)
                .collect(Collectors.toList());
    }

    @Override
    public User removeFriendToUser(Integer userId, Integer friendId) {
        User user = users.get(userId);
        if (user == null) {
            throw new UserNotFoundException("Пользователь с данным ID не найден");
        }
        if (user.getFriendsList().contains(friendId.longValue())) {
            user.getFriendsList().remove(friendId.longValue());
            log.info("Пользователь с {} успешно удален из списка ваших друзей", friendId);
            return user;
        } else {
            throw new UserNotFoundException(
                    String.format("Данный пользователь с ID-%s не найден, удаление невозможно", friendId));
        }
    }

    @Override
    public List<User> commonFriends(Integer userId, Integer otherId) {
        User user = users.get(userId);
        User otherUser = users.get(otherId);
        Set<Long> finalList = new HashSet<>(user.getFriendsList());
        finalList.retainAll(otherUser.getFriendsList());
        return finalList.stream()
                .mapToInt(Long::intValue)
                .filter(users::containsKey)
                .mapToObj(users::get)
                .collect(Collectors.toList());
    }
}
