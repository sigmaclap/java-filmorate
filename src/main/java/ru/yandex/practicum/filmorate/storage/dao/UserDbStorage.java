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

    private static final String USER_ID_COLUMN = "USER_ID";
    private static final String FRIEND_ID_COLUMN = "FRIEND_ID";
    private static final String EMAIL_COLUMN = "EMAIL";
    private static final String LOGIN_COLUMN = "LOGIN";
    private static final String NAME_COLUMN = "NAME";
    private static final String BIRTHDAY_COLUMN = "BIRTHDAY";
    private static final String FRIENDSHIP_STATUS_COLUMN = "FRIENDSHIP_STATUS";

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
                .id(rs.getInt(USER_ID_COLUMN))
                .email(rs.getString(EMAIL_COLUMN))
                .login(rs.getString(LOGIN_COLUMN))
                .name(rs.getString(NAME_COLUMN))
                .birthday(rs.getDate(BIRTHDAY_COLUMN).toLocalDate())
                .build();

        SqlRowSet rset = jdbcTemplate.queryForRowSet(SQLScripts.GET_USER_WITH_FRIENDSHIP, rs.getInt(USER_ID_COLUMN));
        while (rset.next()) {
            int id = rset.getInt(FRIEND_ID_COLUMN);
            if (id != 0) {
                boolean isFriendship = rset.getBoolean(FRIENDSHIP_STATUS_COLUMN);
                user.getFriendsList().put((long) id, isFriendship);
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
                    userRow.getInt(USER_ID_COLUMN),
                    userRow.getString(EMAIL_COLUMN),
                    userRow.getString(LOGIN_COLUMN),
                    userRow.getString(NAME_COLUMN),
                    Objects.requireNonNull(userRow.getDate(BIRTHDAY_COLUMN)).toLocalDate());
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
                    .id(userRow.getInt(USER_ID_COLUMN))
                    .email(userRow.getString(EMAIL_COLUMN))
                    .login(userRow.getString(LOGIN_COLUMN))
                    .name(userRow.getString(NAME_COLUMN))
                    .birthday(Objects.requireNonNull(userRow.getDate(BIRTHDAY_COLUMN).toLocalDate()))
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
        boolean isUserExists = listUsers.stream().noneMatch(user -> user.getId().equals(userId));
        if (isUserExists) {
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
    public boolean addFriend(Integer userId, Integer friendId, boolean isFriendship) {
        String sqlAdd = SQLScripts.INSERT_FRIEND_ON_USER;
        if (isFriendship) {
            String sql = SQLScripts.UPDATE_USER_FRIENDSHIP;
            jdbcTemplate.update(sql, friendId, userId, true);
            return jdbcTemplate.update(sqlAdd, userId, friendId, true) > 0;
        }
        return jdbcTemplate.update(sqlAdd, userId, friendId, false) > 0;
    }

    @Override
    public boolean removeFriendToUser(Integer userId, Integer friendId, boolean isFriendship) {
        String sqlDelete = SQLScripts.DELETE_FRIEND_ON_USER;
        if (isFriendship) {
            String sql = SQLScripts.UPDATE_USER_FRIENDSHIP;
            return jdbcTemplate.update(sql, friendId, userId, false) > 0;
        }
        return jdbcTemplate.update(sqlDelete, userId, friendId) > 0;
    }
}
