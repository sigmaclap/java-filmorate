package ru.yandex.practicum.filmorate.storage.dao.constants;

public class SQLScripts {
    public static final String GET_ALL_FILMS = "SELECT f.FILM_ID , f.RATING_ID ," +
            " f.NAME , f.DESCRIPTION , f.RELEASE_DATE ," +
            " f.DURATION , fr.NAME as R_NAME, g.GENRE_ID , g.NAME AS G_NAME \n" +
            "FROM FILMS f \n" +
            "JOIN FILMS_RATINGS fr ON f.RATING_ID = fr.RATING_ID\n" +
            "LEFT JOIN FILMS_CATEGORY fc ON f.FILM_ID = fc.FILM_ID \n" +
            "LEFT JOIN GENRE g ON g.GENRE_ID = fc.GENRE_ID;";

    public static final String GET_FILM_WITH_ID = "SELECT f.FILM_ID , f.RATING_ID , f.NAME ," +
            "f.DESCRIPTION , f.RELEASE_DATE , f.DURATION , fr.NAME as R_NAME \n" +
            "FROM FILMS f JOIN FILMS_RATINGS fr ON fr.RATING_ID = f.RATING_ID WHERE f.FILM_ID = ?";

    public static final String ADD_NEW_FILM = "INSERT INTO PUBLIC.FILMS\n" +
            "(RATING_ID, NAME, DESCRIPTION, RELEASE_DATE, DURATION)\n" +
            "VALUES(?, ?, ?, ?, ?)";

    public static final String GET_FILM_WITH_RATING_NAME = "SELECT *, fr.NAME as R_NAME FROM FILMS f " +
            "JOIN FILMS_RATINGS fr ON f.RATING_ID = fr.RATING_ID" +
            " WHERE f.NAME = ?";

    public static final String DELETE_FILMS_CATEGORY = "DELETE FROM FILMS_CATEGORY WHERE FILM_ID = ?";

    public static final String INSERT_GENRE_ID = "INSERT INTO FILMS_CATEGORY (film_id, genre_id) VALUES";

    public static final String GET_GENRE_ID_WITH_SORT = "select DISTINCT g.GENRE_ID as GENRE_ID ," +
            " g.NAME as NAME, fc.FILM_ID \n" +
            "from GENRE g  \n" +
            "join FILMS_CATEGORY fc ON g.GENRE_ID  = fc.GENRE_ID \n" +
            "WHERE fc.FILM_ID = ? \n" +
            "ORDER BY g.GENRE_ID ASC";

    public static final String UPDATE_FILM_SET = "UPDATE PUBLIC.FILMS SET RATING_ID=?, " +
            "NAME=?, DESCRIPTION=?, RELEASE_DATE=?, DURATION=? WHERE FILM_ID=?";

    public static final String GET_FILM_WITH_FILM_ID = "SELECT *, fr.NAME as R_NAME FROM FILMS f " +
            "JOIN FILMS_RATINGS fr ON fr.RATING_ID = f.RATING_ID WHERE f.FILM_ID = ?";

    public static final String GET_FILMS_WITH_COUNT_LIKES = "SELECT f.FILM_ID , f.NAME , f.DESCRIPTION ," +
            " f.RELEASE_DATE, f.DURATION , fr.RATING_ID , fr.NAME as R_NAME ," +
            " g.GENRE_ID , g.NAME AS G_NAME , COUNT(ulff.FILM_ID) \n" +
            "FROM FILMS f \n" +
            "LEFT JOIN USER_LIKES_FOR_FILMS ulff ON f.FILM_ID = ulff.FILM_ID \n" +
            "JOIN FILMS_RATINGS fr ON f.RATING_ID  = fr.RATING_ID \n" +
            "LEFT JOIN FILMS_CATEGORY fc ON fc.FILM_ID = f.FILM_ID \n" +
            "LEFT JOIN GENRE g ON g.GENRE_ID = fc.GENRE_ID \n" +
            "GROUP BY f.FILM_ID \n" +
            "ORDER BY COUNT(ulff.FILM_ID) DESC \n" +
            "LIMIT ?";

    public static final String GET_USER_WITH_FRIENDSHIP = "SELECT USER_ID, FRIEND_ID, FRIENDSHIP_STATUS\n" +
            "FROM PUBLIC.USERS_FRIENDS_STATUS WHERE USER_ID = ?";

    public static final String INSERT_USER_LIKE_ON_FILM = "INSERT INTO PUBLIC.USER_LIKES_FOR_FILMS\n" +
            "(FILM_ID, USER_ID) VALUES(?, ?)";

    public static final String DELETE_USER_LIKE_ON_FILM = "DELETE FROM PUBLIC.USER_LIKES_FOR_FILMS\n" +
            "WHERE FILM_ID=? AND USER_ID=?";

    public static final String GET_FILM_ID_WITH_GENRE = "SELECT FILM_ID, fc.GENRE_ID," +
            " g.NAME  FROM PUBLIC.FILMS_CATEGORY fc " +
            "JOIN GENRE g ON g.GENRE_ID = fc.GENRE_ID WHERE FILM_ID = ? ORDER BY g.GENRE_ID ASC";

    public static final String INSERT_NEW_USER = "INSERT INTO PUBLIC.USERS\n" +
            "(EMAIL, LOGIN, NAME, BIRTHDAY)\n" +
            "VALUES(?, ?, ?, ?)";

    public static final String UPDATE_USER_SET = "UPDATE PUBLIC.USERS\n" +
            "SET EMAIL=?, LOGIN=?, NAME=?, BIRTHDAY=?\n" +
            "WHERE USER_ID= ?";

    public static final String GET_COMMON_FRIENDS_USER = "SELECT u.USER_ID , u.EMAIL , u.LOGIN , u.NAME , u.BIRTHDAY ," +
            " ufs3.FRIEND_ID " +
            ", ufs3.FRIENDSHIP_STATUS \n" +
            "FROM USERS u JOIN USERS_FRIENDS_STATUS ufs ON u.USER_ID = ufs.FRIEND_ID " +
            "AND ufs.USER_ID = ? JOIN USERS_FRIENDS_STATUS ufs2 ON u.USER_ID = ufs2.FRIEND_ID " +
            "AND ufs2.USER_ID = ?\n" +
            "LEFT JOIN USERS_FRIENDS_STATUS ufs3  ON u.USER_ID = ufs3.USER_ID";

    public static final String INSERT_FRIEND_ON_USER = "INSERT INTO PUBLIC.USERS_FRIENDS_STATUS\n" +
            "(USER_ID, FRIEND_ID, FRIENDSHIP_STATUS)\n" +
            "VALUES(?, ?, ?)";

    public static final String UPDATE_USER_FRIENDSHIP = "UPDATE PUBLIC.USERS_FRIENDS_STATUS\n" +
            "SET USER_ID=?, FRIEND_ID=?, FRIENDSHIP_STATUS=?\n" +
            "WHERE USER_ID=? AND FRIEND_ID=?";

    public static final String DELETE_FRIEND_ON_USER = "DELETE FROM PUBLIC.USERS_FRIENDS_STATUS\n" +
            "WHERE USER_ID=? AND FRIEND_ID=?";

    public static final String GET_USER = "SELECT * FROM USERS u WHERE USER_ID = ?";
}
