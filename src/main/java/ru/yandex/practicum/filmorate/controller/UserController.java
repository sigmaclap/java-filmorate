package ru.yandex.practicum.filmorate.controller;

import jdk.jfr.Description;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {

    private final UserService userService;


    @GetMapping()
    public List<User> findUsers() {
        return userService.findUsers();
    }

    @PostMapping()
    public User createUser(@Valid @RequestBody User user) {
        return userService.create(user);
    }

    @PutMapping()
    public User updateUsers(@Valid @RequestBody User user) {
        return userService.update(user);
    }

    @GetMapping("/{id}")
    @Description("Поиск пользователя по ID")
    public User findUser(@PathVariable("id") Integer userId) {
        return userService.findUserById(userId);
    }

    @GetMapping("/{id}/friends")
    @Description("Возвращаем список пользователей, являющихся его друзьями.")
    public List<User> getFriendsUser(@PathVariable("id") Integer id) {
        return userService.getFriendsUser(id);
    }

    @PutMapping("/{id}/friends/{friendId}")
    @Description("Добавление в друзья пользователя")
    public User addFriend(@PathVariable("id") Integer userId, @PathVariable("friendId") Integer friendId) {
        return userService.addFriend(userId, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    @Description("Удаление друга из списка друзей пользователя")
    public User removeFriendToUser(@PathVariable("id") Integer userId, @PathVariable("friendId") Integer friendId) {
        return userService.removeFriendToUser(userId, friendId);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    @Description("Отображение общего списка друзей пользователей")
    public List<User> commonFriends(@PathVariable("id") Integer userId, @PathVariable("otherId") Integer otherId) {
        return userService.commonFriends(userId, otherId);
    }
}
