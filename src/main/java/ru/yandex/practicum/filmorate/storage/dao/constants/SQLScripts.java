package ru.yandex.practicum.filmorate.storage.dao.constants;

public class SQLScripts {
    public static final String GET_ALL_FILMS = "SELECT DISTINCT f.FILM_ID , f.RATING_ID ," +
            " f.NAME , f.DESCRIPTION , f.RELEASE_DATE ," +
            " f.DURATION , fr.NAME as R_NAME \n" +
            "FROM FILMS f \n" +
            "JOIN FILMS_RATINGS fr ON f.RATING_ID = fr.RATING_ID\n" +
            "LEFT JOIN FILMS_CATEGORY fc ON f.FILM_ID = fc.FILM_ID";

    public static final String GET_FILM_WITH_ID = "SELECT f.FILM_ID , f.RATING_ID , f.NAME ," +
            "f.DESCRIPTION , f.RELEASE_DATE , f.DURATION , fr.NAME as R_NAME \n" +
            "FROM FILMS f JOIN FILMS_RATINGS fr ON fr.RATING_ID = f.RATING_ID WHERE f.FILM_ID = ?";

    public static final String ADD_NEW_FILM = "INSERT INTO PUBLIC.FILMS\n" +
            "(RATING_ID, NAME, DESCRIPTION, RELEASE_DATE, DURATION)\n" +
            "VALUES(?, ?, ?, ?, ?)";

    public static final String DELETE_FILMS_CATEGORY = "DELETE FROM FILMS_CATEGORY WHERE FILM_ID = ?";

    public static final String INSERT_GENRE_ID = "INSERT INTO FILMS_CATEGORY (film_id, genre_id) VALUES";

    public static final String GET_GENRE_ID_WITH_SORT = "select DISTINCT g.GENRE_ID as GENRE_ID ," +
            " g.NAME as NAME, fc.FILM_ID \n" +
            "from GENRE g  \n" +
            "join FILMS_CATEGORY fc ON g.GENRE_ID  = fc.GENRE_ID \n" +
            "WHERE fc.FILM_ID = ? \n" +
            "ORDER BY g.GENRE_ID ASC";

    public static final String GET_FILM_WITH_RATING_ID = "SELECT *, fr.NAME as R_NAME FROM FILMS f " +
            "JOIN FILMS_RATINGS fr ON f.RATING_ID = fr.RATING_ID" +
            " WHERE f.FILM_ID = ?";
    public static final String UPDATE_FILM_SET = "UPDATE PUBLIC.FILMS SET RATING_ID=?, " +
            "NAME=?, DESCRIPTION=?, RELEASE_DATE=?, DURATION=? WHERE FILM_ID=?";

    public static final String GET_FILM_WITH_FILM_ID = "SELECT *, fr.NAME as R_NAME FROM FILMS f " +
            "JOIN FILMS_RATINGS fr ON fr.RATING_ID = f.RATING_ID WHERE f.FILM_ID = ?";

    public static final String GET_MOST_POPULAR_FILMS_WITHOUT_GENRES_AND_YEAR = "SELECT f.FILM_ID , f.NAME , f.DESCRIPTION ," +
            " f.RELEASE_DATE, f.DURATION , fr.RATING_ID , fr.NAME as R_NAME ," +
            " g.GENRE_ID , g.NAME AS G_NAME , COUNT(ulff.FILM_ID) \n" +
            "FROM FILMS f \n" +
            "LEFT JOIN USER_LIKES_FOR_FILMS ulff ON f.FILM_ID = ulff.FILM_ID \n" +
            "JOIN FILMS_RATINGS fr ON f.RATING_ID  = fr.RATING_ID \n" +
            "LEFT JOIN FILMS_CATEGORY fc ON fc.FILM_ID = f.FILM_ID \n" +
            "LEFT JOIN GENRE g ON g.GENRE_ID = fc.GENRE_ID \n" +
            "GROUP BY f.FILM_ID, g.GENRE_ID \n" +
            "ORDER BY COUNT(ulff.FILM_ID) DESC \n" +
            "LIMIT ?";

    public static final String GET_MOST_POPULAR_FILMS_WITH_GENRES_AND_YEAR = "SELECT f.FILM_ID , f.NAME , f.DESCRIPTION ," +
            " f.RELEASE_DATE, f.DURATION , fr.RATING_ID , fr.NAME as R_NAME ," +
            " g.GENRE_ID , g.NAME AS G_NAME , COUNT(ulff.FILM_ID) \n" +
            "FROM FILMS f \n" +
            "LEFT JOIN USER_LIKES_FOR_FILMS ulff ON f.FILM_ID = ulff.FILM_ID \n" +
            "JOIN FILMS_RATINGS fr ON f.RATING_ID  = fr.RATING_ID \n" +
            "LEFT JOIN FILMS_CATEGORY fc ON fc.FILM_ID = f.FILM_ID \n" +
            "LEFT JOIN GENRE g ON g.GENRE_ID = fc.GENRE_ID \n" +
            "WHERE g.GENRE_ID = ? AND EXTRACT(YEAR FROM f.RELEASE_DATE) = ? \n" +
            "GROUP BY f.FILM_ID, g.GENRE_ID  \n" +
            "ORDER BY COUNT(ulff.FILM_ID) DESC \n" +
            "LIMIT ?";

    public static final String GET_MOST_POPULAR_FILMS_WITH_GENRES_AND_WITHOUT_YEAR = "SELECT f.FILM_ID , f.NAME , f.DESCRIPTION ," +
            " f.RELEASE_DATE, f.DURATION , fr.RATING_ID , fr.NAME as R_NAME ," +
            " g.GENRE_ID , g.NAME AS G_NAME , COUNT(ulff.FILM_ID) \n" +
            "FROM FILMS f \n" +
            "LEFT JOIN USER_LIKES_FOR_FILMS ulff ON f.FILM_ID = ulff.FILM_ID \n" +
            "JOIN FILMS_RATINGS fr ON f.RATING_ID  = fr.RATING_ID \n" +
            "LEFT JOIN FILMS_CATEGORY fc ON fc.FILM_ID = f.FILM_ID \n" +
            "LEFT JOIN GENRE g ON g.GENRE_ID = fc.GENRE_ID \n" +
            "WHERE g.GENRE_ID = ? \n" +
            "GROUP BY f.FILM_ID, g.GENRE_ID  \n" +
            "ORDER BY COUNT(ulff.FILM_ID) DESC \n" +
            "LIMIT ?";

    public static final String GET_MOST_POPULAR_FILMS_WITHOUT_GENRES_AND_WITH_YEAR = "SELECT f.FILM_ID , f.NAME , f.DESCRIPTION ," +
            " f.RELEASE_DATE, f.DURATION , fr.RATING_ID , fr.NAME as R_NAME ," +
            " g.GENRE_ID , g.NAME AS G_NAME , COUNT(ulff.FILM_ID) \n" +
            "FROM FILMS f \n" +
            "LEFT JOIN USER_LIKES_FOR_FILMS ulff ON f.FILM_ID = ulff.FILM_ID \n" +
            "JOIN FILMS_RATINGS fr ON f.RATING_ID  = fr.RATING_ID \n" +
            "LEFT JOIN FILMS_CATEGORY fc ON fc.FILM_ID = f.FILM_ID \n" +
            "LEFT JOIN GENRE g ON g.GENRE_ID = fc.GENRE_ID \n" +
            "WHERE EXTRACT(YEAR FROM f.RELEASE_DATE) = ? \n" +
            "GROUP BY f.FILM_ID, g.GENRE_ID \n" +
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

    public static final String GET_LIKE_TITLE_PROPERTY = "SELECT f.FILM_ID , f.NAME , f.DESCRIPTION ," +
            " f.RELEASE_DATE, f.DURATION , fr.RATING_ID , fr.NAME as R_NAME ," +
            " g.GENRE_ID , g.NAME AS G_NAME , COUNT(ulff.FILM_ID) \n" +
            "FROM FILMS f \n" +
            "LEFT JOIN USER_LIKES_FOR_FILMS ulff ON f.FILM_ID = ulff.FILM_ID \n" +
            "JOIN FILMS_RATINGS fr ON f.RATING_ID  = fr.RATING_ID \n" +
            "LEFT JOIN FILMS_CATEGORY fc ON fc.FILM_ID = f.FILM_ID \n" +
            "LEFT JOIN GENRE g ON g.GENRE_ID = fc.GENRE_ID \n" +
            "WHERE LOWER(f.NAME) LIKE '%' || (?) || '%' GROUP BY f.FILM_ID, g.GENRE_ID\n" +
            "ORDER BY COUNT(ulff.FILM_ID) DESC";

    public static final String GET_LIKE_DIRECTOR_PROPERTY = "SELECT f.FILM_ID , f.NAME , f.DESCRIPTION ,f.RELEASE_DATE, f.DURATION , " +
            "fr.RATING_ID , fr.NAME as R_NAME, COUNT(ulff.FILM_ID) FROM FILMS f \n" +
            "LEFT JOIN USER_LIKES_FOR_FILMS ulff ON f.FILM_ID = ulff.FILM_ID \n" +
            "JOIN FILMS_RATINGS fr ON f.RATING_ID  = fr.RATING_ID \n" +
            "JOIN DIRECTOR_FILMS df ON df.FILM_ID = f.FILM_ID \n" +
            "JOIN DIRECTOR d ON d.DIRECTOR_ID = df.DIRECTOR_ID \n" +
            "WHERE LOWER(d.NAME) LIKE '%' || (?) || '%' GROUP BY f.FILM_ID \n" +
            "ORDER BY COUNT(ulff.FILM_ID) DESC";

    public static final String GET_LIKE_TITLE_AND_DIRECTOR_PROPERTY = "SELECT f.FILM_ID , f.NAME , f.DESCRIPTION ,f.RELEASE_DATE, f.DURATION , " +
            "fr.RATING_ID , fr.NAME as R_NAME , g.GENRE_ID , g.NAME AS G_NAME , COUNT(ulff.FILM_ID) \n" +
            "FROM FILMS f LEFT JOIN USER_LIKES_FOR_FILMS ulff ON f.FILM_ID = ulff.FILM_ID \n" +
            "JOIN FILMS_RATINGS fr ON f.RATING_ID  = fr.RATING_ID \n" +
            "LEFT JOIN FILMS_CATEGORY fc ON fc.FILM_ID = f.FILM_ID \n" +
            "LEFT JOIN GENRE g ON g.GENRE_ID = fc.GENRE_ID \n" +
            "LEFT JOIN DIRECTOR_FILMS df ON df.FILM_ID = f.FILM_ID \n" +
            "LEFT JOIN DIRECTOR d ON d.DIRECTOR_ID = df.DIRECTOR_ID \n" +
            "WHERE (LOWER(f.NAME) LIKE '%' || (?) || '%' OR LOWER(d.NAME) LIKE '%' || (?) || '%')\n" +
            "GROUP BY f.FILM_ID, g.GENRE_ID \n" +
            "ORDER BY COUNT(ulff.FILM_ID) DESC";

    public static final String GET_LIST_DIRECTOR_BY_YEAR = "SELECT f.FILM_ID , f.NAME , f.DESCRIPTION , f.RELEASE_DATE, f.DURATION , fr.RATING_ID , " +
            "fr.NAME as R_NAME , g.GENRE_ID , g.NAME AS G_NAME , YEAR(f.RELEASE_DATE) AS years \n" +
            "FROM FILMS f \n" +
            "LEFT JOIN USER_LIKES_FOR_FILMS ulff ON f.FILM_ID = ulff.FILM_ID \n" +
            "JOIN FILMS_RATINGS fr ON f.RATING_ID  = fr.RATING_ID \n" +
            "LEFT JOIN FILMS_CATEGORY fc ON fc.FILM_ID = f.FILM_ID \n" +
            "LEFT JOIN GENRE g ON g.GENRE_ID = fc.GENRE_ID \n" +
            "JOIN DIRECTOR_FILMS df ON df.FILM_ID = f.FILM_ID \n" +
            "WHERE df.DIRECTOR_ID = ?\n" +
            "GROUP BY f.FILM_ID \n" +
            "ORDER BY years ASC";

    public static final String GET_LIST_DIRECTOR_BY_LIKE = "SELECT f.FILM_ID , f.NAME , f.DESCRIPTION , f.RELEASE_DATE, f.DURATION , fr.RATING_ID , " +
            "fr.NAME as R_NAME , g.GENRE_ID , g.NAME AS G_NAME , COUNT(ulff.USER_ID) AS likes \n" +
            "FROM FILMS f \n" +
            "LEFT JOIN USER_LIKES_FOR_FILMS ulff ON f.FILM_ID = ulff.FILM_ID \n" +
            "JOIN FILMS_RATINGS fr ON f.RATING_ID  = fr.RATING_ID \n" +
            "LEFT JOIN FILMS_CATEGORY fc ON fc.FILM_ID = f.FILM_ID \n" +
            "LEFT JOIN GENRE g ON g.GENRE_ID = fc.GENRE_ID \n" +
            "JOIN DIRECTOR_FILMS df ON df.FILM_ID = f.FILM_ID \n" +
            "WHERE df.DIRECTOR_ID = ?\n" +
            "GROUP BY f.FILM_ID \n" +
            "ORDER BY likes ASC";

    public static final String GET_DIRECTOR_ID_FOR_UPDATE = "select g.DIRECTOR_ID as DIRECTOR_ID ," +
            " g.NAME as NAME, fc.FILM_ID \n" +
            "from DIRECTOR g  \n" +
            "join DIRECTOR_FILMS fc ON g.DIRECTOR_ID  = fc.DIRECTOR_ID \n" +
            "WHERE fc.FILM_ID = ? \n" +
            "ORDER BY g.DIRECTOR_ID ASC";

    public static final String GET_COMMON_FILMS_TWO_USERS = "SELECT DISTINCT f.FILM_ID, f.RATING_ID , f.NAME, " +
            "f.DESCRIPTION , f.RELEASE_DATE , f.DURATION, fr.NAME as R_NAME, COUNT(ulff.USER_ID) \n" +
            "FROM FILMS f \n" +
            "JOIN USER_LIKES_FOR_FILMS ulff ON ulff.FILM_ID = f.FILM_ID\n" +
            "JOIN FILMS_RATINGS fr ON fr.RATING_ID = f.RATING_ID\n" +
            "WHERE f.FILM_ID = ANY (SELECT DISTINCT ulff.FILM_ID\n" +
            "FROM USER_LIKES_FOR_FILMS ulff \n" +
            "JOIN USER_LIKES_FOR_FILMS ulff2 ON ulff.FILM_ID = ulff2.FILM_ID \n" +
            "WHERE ulff.USER_ID = ? AND ulff2.USER_ID = ?)\n" +
            "GROUP BY f.FILM_ID\n" +
            "ORDER BY COUNT(ulff.USER_ID) DESC ";

    public static final String GET_RECOMMENDATION_USERS = "SELECT ulff2.FILM_ID \n" +
            "FROM USER_LIKES_FOR_FILMS ulff\n" +
            "JOIN USER_LIKES_FOR_FILMS ulff2 ON ulff2.USER_ID != ?\n" +
            "WHERE ulff.USER_ID != ? AND ulff.FILM_ID IN " +
            "(SELECT ul.FILM_ID  FROM USER_LIKES_FOR_FILMS ul WHERE ul.USER_ID = ?)";

    public static final String USEFUL = "SUM(CASE rl.review_like WHEN TRUE THEN +1 " +
            "WHEN FALSE THEN -1 ELSE 0 END) AS USEFUL";
    public static final String ALL_REVIEWS = "SELECT r.review_id , r.CONTENT , r.is_positive , r.film_id , r.user_id , " +
            USEFUL + " FROM REVIEW r LEFT JOIN REVIEW_LIKES rl ON r.review_id = rl.review_id";

    public static final String GET_USER_FEED = "SELECT EVENT_ID, USER_ID, ENTITY_ID, TIME_STAMP, EVENT_TYPE, OPERATION_TYPE\n" +
            "FROM USER_FEEDS\n" +
            "WHERE USER_ID = ?";

    public static final String ADD_FEED = "INSERT INTO USER_FEEDS\n" +
            "(USER_ID, TIME_STAMP , EVENT_TYPE, OPERATION_TYPE, ENTITY_ID)\n" +
            "VALUES(?, ?, ?, ?, ?)";

    public static final String GET_ALL_REVIEWS = "SELECT r.review_id , r.content , r.is_positive , r.film_id , r.user_id , " + USEFUL +
            " FROM REVIEW r " +
            "LEFT JOIN REVIEW_LIKES rl ON rl.review_id = r.review_id " +
            "GROUP BY r.review_id ORDER BY USEFUL DESC";
}
