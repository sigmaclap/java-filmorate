package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserDbStorageTest {
    private final UserStorage userStorage;

    private User user_1;
    private User user_2;
    private User user_3;

    @BeforeEach
    void setUp() {
        user_1 = User.builder()
                .email("box@gmail.com")
                .login("boxbox")
                .name("Саня")
                .birthday(LocalDate.of(2000, Month.APRIL, 12))
                .build();

        user_2 = User.builder()
                .email("box2@gmail.com")
                .login("boxbox2")
                .name("Саня2")
                .birthday(LocalDate.of(2002, Month.APRIL, 12))
                .build();

        user_3 = User.builder()
                .email("box3@gmail.com")
                .login("boxbox3")
                .name("Саня3")
                .birthday(LocalDate.of(2003, Month.APRIL, 12))
                .build();
    }

    @Test
    void findUsers() {
        User user = userStorage.create(user_1);
        User user2 = userStorage.create(user_2);
        List<User> usersEx = userStorage.findUsers();

        assertThat(usersEx).hasSize(2).contains(user, user2);
    }

    @Test
    void create() {
        User user = userStorage.create(user_1);
        User exUser = userStorage.findUserById(user.getId());

        assertThat(exUser).hasFieldOrPropertyWithValue("name", user.getName());
    }

    @Test
    void update() {
        User user = userStorage.create(user_1);
        user.setName("Дима");
        user.setLogin("vovain");
        User exUser = userStorage.update(user);

        assertThat(exUser).hasFieldOrPropertyWithValue("name", "Дима")
                .hasFieldOrPropertyWithValue("login", "vovain");

    }

    @Test
    void findUserById() {
        User user = userStorage.create(user_1);
        User exUser = userStorage.findUserById(user.getId());

        assertThat(exUser).isEqualTo(user);
    }

    @Test
    void getFriendsUser() {
        User user = userStorage.create(user_1);
        User user1 = userStorage.create(user_2);
        userStorage.addFriend(user.getId(), user1.getId(), false);

        List<User> friends = userStorage.getFriendsUser(user.getId());

        assertThat(friends).hasSize(1)
                .contains(user1);
    }

    @Test
    void commonFriends() {
        User user1 = userStorage.create(user_1);
        User user2 = userStorage.create(user_2);
        User user3 = userStorage.create(user_3);
        userStorage.addFriend(user1.getId(), user3.getId(), false);
        userStorage.addFriend(user2.getId(), user3.getId(), false);
        List<User> commonFriends = userStorage.commonFriends(user1.getId(), user2.getId());

        assertThat(commonFriends).hasSize(1).contains(user3);
    }

    @Test
    void addFriend() {
        User user = userStorage.create(user_1);
        User user1 = userStorage.create(user_2);
        userStorage.addFriend(user.getId(), user1.getId(), false);

        List<User> friends = userStorage.getFriendsUser(user.getId());

        assertThat(friends).hasSize(1)
                .contains(user1);
    }

    @Test
    void removeFriendToUser() {
        User user = userStorage.create(user_1);
        User user1 = userStorage.create(user_2);
        userStorage.addFriend(user.getId(), user1.getId(), false);
        userStorage.removeFriendToUser(user.getId(), user1.getId(), false);

        List<User> friends = userStorage.getFriendsUser(user.getId());

        assertThat(friends).isEmpty();
    }
}