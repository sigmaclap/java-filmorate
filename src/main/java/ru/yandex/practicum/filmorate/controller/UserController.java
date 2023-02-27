package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.InvalidDataException;
import ru.yandex.practicum.filmorate.exceptions.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
public class UserController {
    int idxUsers = 0;
    private final Map<Integer, User> users = new HashMap<>();

    @GetMapping("/users")
    public List<User> findUsers() {
        log.debug("Количество пользователей: {}", users.size());
        log.debug("Cписок фильмов: {}", users);
        return new ArrayList<>(users.values());
    }

    @PostMapping(value = "/users")
    public User createUser(@Valid @RequestBody User user) {
        int id = ++idxUsers;
        if (users.containsValue(user)) {
            log.error("Get ERROR {}, request /POST", UserAlreadyExistException.class);
            throw new UserAlreadyExistException("UserAlreadyExistException");
        } else if (user == null || user.toString().isEmpty()) {
            log.error("Get ERROR {}, request /POST", InvalidDataException.class);
            throw new InvalidDataException("InvalidDataException");
        } else {
            if (user.getName() == null) {
                user.setName(user.getLogin());
                user.setId(id);
                users.put(id, user);
                log.debug("User created - SetName = UserLogin: {}", user);
                return user;
            } else {
                user.setId(id);
                users.put(id, user);
                log.debug("User created: {}", user);
                return user;
            }
        }
    }

    @PutMapping(value = "/users")
    public User updateUsers(@Valid @RequestBody User user) {
        if (users.containsKey(user.getId())) {
            if (user.getName() == null) {
                user.setName(user.getLogin());
            }
            users.remove(user.getId());
            users.put(user.getId(), user);
            return user;
        } else {
            log.error("Get ERROR {}, request /PUT", InvalidDataException.class);
            throw new InvalidDataException("Invalid Data");
        }
    }
}
