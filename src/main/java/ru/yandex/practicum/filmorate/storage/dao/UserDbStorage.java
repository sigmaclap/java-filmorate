package ru.yandex.practicum.filmorate.storage.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.InvalidDataException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.dao.constants.SQLScripts;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
@Slf4j
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<User> findUsers() {
        String sql = "SELECT * FROM USERS u";
        return jdbcTemplate.query(sql, this::makeUser);
    }

    private User makeUser(ResultSet rs, int rowNum) throws SQLException {
        User user = User.builder()
                .id(rs.getInt("user_id"))
                .email(rs.getString("email"))
                .login(rs.getString("login"))
                .name(rs.getString("name"))
                .birthday(rs.getDate("birthday").toLocalDate())
                .build();

        SqlRowSet rset = jdbcTemplate.queryForRowSet("SELECT USER_ID, FRIEND_ID, FRIENDSHIP_STATUS\n" +
                "FROM PUBLIC.USERS_FRIENDS_STATUS WHERE USER_ID = ?", rs.getInt("USER_ID"));
        while (rset.next()) {
            int id = rset.getInt("FRIEND_ID");
            if (id != 0) {
                boolean isFriendShip = rset.getBoolean("FRIENDSHIP_STATUS");
                user.getFriendsList().put((long) id, isFriendShip);
            }
        }
        return user;
    }

    @Override
    public User create(User user) {
        String sqlQuery = SQLScripts.INSERT_NEW_USER;
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        jdbcTemplate.update(sqlQuery, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());

        SqlRowSet userRow = jdbcTemplate.queryForRowSet("SELECT * FROM USERS u WHERE EMAIL = ?", user.getEmail());
        if (userRow.next()) {
            return new User(
                    userRow.getInt("USER_ID"),
                    userRow.getString("EMAIL"),
                    userRow.getString("LOGIN"),
                    userRow.getString("NAME"),
                    Objects.requireNonNull(userRow.getDate("BIRTHDAY")).toLocalDate());
        } else {
            log.info("Пользователь с email {} не найден.", user.getEmail());
            throw new InvalidDataException("Пустое значение User");
        }
    }


    @Override
    public User update(User user) {
        String sqlQuery = SQLScripts.UPDATE_USER_SET;
        jdbcTemplate.update(sqlQuery,
                user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());
        SqlRowSet userRow = jdbcTemplate.queryForRowSet(SQLScripts.GET_USER, user.getId());
        if (userRow.next()) {
            return User.builder()
                    .id(userRow.getInt("USER_ID"))
                    .email(userRow.getString("EMAIL"))
                    .login(userRow.getString("LOGIN"))
                    .name(userRow.getString("NAME"))
                    .birthday(userRow.getDate("BIRTHDAY").toLocalDate())
                    .build();
        } else {
            log.info("Пользователь с идентификатором {} не найден, обновление не удалось.", user.getId());
            throw new UserNotFoundException("Пользователь не найден");
        }
    }

    @Override
    public User findUserById(Integer userId) {
        String sqlQuery = SQLScripts.GET_USER;
        List<User> listUsers = findUsers();
        if (listUsers.stream().noneMatch(user -> user.getId().equals(userId))) {
            log.info("Пользователь с идентификатором {} не найден.", userId);
            throw new UserNotFoundException("Пользователь не найден");
        }
        return jdbcTemplate.queryForObject(sqlQuery, this::makeUser, userId);
    }


    @Override
    public List<User> getFriendsUser(Integer userId) {
        String sqlQuery = String.format("SELECT FRIEND_ID FROM USERS_FRIENDS_STATUS ufs WHERE USER_ID = %d", userId);
        List<Integer> idFriends = new ArrayList<>(jdbcTemplate.queryForList(sqlQuery, Integer.class));
        List<User> friendsList = new ArrayList<>();
        for (Integer id : idFriends) {
            User user = findUserById(id);
            friendsList.add(user);
        }
        return friendsList;
    }

    @Override
    public List<User> commonFriends(Integer userId, Integer otherId) {
        SqlRowSet userRow = jdbcTemplate.queryForRowSet(SQLScripts.GET_USER, userId);
        SqlRowSet otherRow = jdbcTemplate.queryForRowSet(SQLScripts.GET_USER, otherId);
        if (userRow.next() && otherRow.next()) {
            String sqlQuery = SQLScripts.GET_COMMON_FRIENDS_USER;
            return jdbcTemplate.query(sqlQuery, this::makeUser, userId, otherId);
        } else if (!otherRow.next()) {
            log.info("Другой пользователь с идентификатором {} не найден.", otherId);
            throw new UserNotFoundException("Друг не найден");
        } else {
            log.info("Пользователь с идентификатором {} не найден.", userId);
            throw new UserNotFoundException("Пользователь не найден");
        }
    }

    @Override
    public boolean addFriend(Integer userId, Integer friendId, boolean isFriendShip) {
        String sqlAdd = SQLScripts.INSERT_FRIEND_ON_USER;
        if (isFriendShip) {
            String sql = SQLScripts.UPDATE_USER_FRIENDSHIP;
            jdbcTemplate.update(sql, friendId, userId, true);
            return jdbcTemplate.update(sqlAdd, userId, friendId, true) > 0;
        }
        return jdbcTemplate.update(sqlAdd, userId, friendId, false) > 0;
    }

    @Override
    public boolean removeFriendToUser(Integer userId, Integer friendId, boolean isFriendShip) {
        String sqlDelete = SQLScripts.DELETE_FRIEND_ON_USER;
        if (isFriendShip) {
            String sql = SQLScripts.UPDATE_USER_FRIENDSHIP;
            return jdbcTemplate.update(sql, friendId, userId, false) > 0;
        }
        return jdbcTemplate.update(sqlDelete, userId, friendId) > 0;
    }
}
