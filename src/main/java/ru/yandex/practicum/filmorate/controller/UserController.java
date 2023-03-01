package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.InvalidDataException;
import ru.yandex.practicum.filmorate.exceptions.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.json.ErrorJson;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
public class UserController {
    private int idxUsers = 0;
    private final Map<Integer, User> users = new HashMap<>();

    @GetMapping("/users")
    public List<User> findUsers() {
        log.debug("Количество пользователей: {}", users.size());
        return new ArrayList<>(users.values());
    }

    @PostMapping(value = "/users")
    public User createUser(@Valid @RequestBody User user) {
        return create(user);
    }

    @PutMapping(value = "/users")
    public User updateUsers(@Valid @RequestBody User user) {
        return update(user);
    }

    private User create(User user) {
        int id = ++idxUsers;
        if (users.containsValue(user)) {
            log.error("Get ERROR {}, request /POST",
                    "Такой пользователь уже существует");
            throw new UserAlreadyExistException(HttpStatus.BAD_REQUEST,
                    ErrorJson.Response("Такой пользователь уже существует"));
        } else if (user == null || user.toString().isEmpty()) {
            log.error("Get ERROR {}, request /POST", "Пустое значение User");
            throw new InvalidDataException(HttpStatus.BAD_REQUEST,
                    ErrorJson.Response("Пустое значение User"));
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

    private User update(User user) {
        if (user.getId() == null || !users.containsKey(user.getId())) {
            log.error("Get ERROR {}, id - {}, request /PUT", "Невозможно обновить пользователя, не найден ID",
                    user.getId());
            throw new InvalidDataException(HttpStatus.NOT_FOUND,
                    ErrorJson.Response("Невозможно обновить пользователя, не найден ID"));
        }
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
        users.remove(user.getId());
        users.put(user.getId(), user);
        return user;
    }

    @ExceptionHandler(UserAlreadyExistException.class)
    public ResponseEntity handleException(UserAlreadyExistException e) {
        return ResponseEntity.status(e.getHttpStatus()).body(e.getMessage());
    }

    @ExceptionHandler(InvalidDataException.class)
    public ResponseEntity handleException(InvalidDataException e) {
        return ResponseEntity.status(e.getHttpStatus()).body(e.getMessage());
    }
}
