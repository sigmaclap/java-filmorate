package ru.yandex.practicum.filmorate.storage.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ReviewNotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

import static ru.yandex.practicum.filmorate.storage.dao.constants.SQLScripts.ALL_REVIEWS;
import static ru.yandex.practicum.filmorate.storage.dao.constants.SQLScripts.USEFUL;

@Component
@Slf4j
public class ReviewDbStorage implements ReviewStorage {
    private final JdbcTemplate jdbcTemplate;

    public ReviewDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Review createReview(Review review) {
        if (review.getUserId() < 0 || review.getFilmId() < 0) {
            log.info("Отзыв не создан, отрицательные значения.");
            throw new ReviewNotFoundException("Отзыв не создан, отрицательные значения.");
        }
        String sql = "INSERT INTO REVIEW (content, is_positive, film_id, user_id) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(sql, new String[]{"review_id"});
            statement.setString(1, review.getContent());
            statement.setBoolean(2, review.getIsPositive());
            statement.setLong(3, review.getFilmId());
            statement.setLong(4, review.getUserId());
            return statement;
        }, keyHolder);
        review.setReviewId(Objects.requireNonNull(keyHolder.getKey()).intValue());
        return review;
    }

    @Override
    public Review updateReview(Review review) {
        String sql = "UPDATE review SET content = ?, is_positive = ? WHERE review_id = ?";
        jdbcTemplate.update(sql, review.getContent(), review.getIsPositive(), review.getReviewId());
        return getReviewById(review.getReviewId());
    }

    @Override
    public boolean deleteReview(Integer reviewId) {
        String sql = "DELETE FROM review WHERE review_id = ?";
        return jdbcTemplate.update(sql, reviewId) > 0;
    }


    @Override
    public List<Review> getAllReviews() {
        String sqlQuery = "SELECT r.review_id , r.content , r.is_positive , r.film_id , r.user_id , " + USEFUL +
                " FROM REVIEW r " +
                "LEFT JOIN REVIEW_LIKES rl ON rl.review_id = r.review_id " +
                "GROUP BY r.review_id ORDER BY USEFUL DESC";
        return jdbcTemplate.query(sqlQuery, this::mapRowToReview);
    }

    @Override
    public Review getReviewById(Integer reviewId) {
        String srs = (ALL_REVIEWS + " WHERE r.review_id = ? GROUP BY r.review_id");
        List<Review> listReview = getAllReviews();
        boolean isReviewExist = listReview.stream().noneMatch(review -> review.getReviewId().equals(reviewId));
        if (isReviewExist) {
            log.info("Отзыв с идентификатором {} не найден.", reviewId);
            throw new ReviewNotFoundException("Отзыв не найден");
        }
        return jdbcTemplate.queryForObject(srs, this::mapRowToReview, reviewId);
    }

    @Override
    public List<Review> getReviewsByFilmId(Integer filmId, Integer count) {
        String srs = ALL_REVIEWS +
                " WHERE r.FILM_ID = ? " +
                "GROUP BY r.REVIEW_ID " +
                "ORDER BY USEFUL DESC LIMIT ?";
        if (filmId == null) {
            return getAllReviews();
        }
        return jdbcTemplate.query(srs, this::mapRowToReview, filmId, count);
    }

    @Override
    public boolean addLike(Integer reviewId, Integer userId) {
        String sqlQuery = "INSERT INTO PUBLIC.REVIEW_LIKES(REVIEW_ID, USER_ID, REVIEW_LIKE)\n" +
                "VALUES(?, ?, true)";
        if (jdbcTemplate.update(sqlQuery, reviewId, userId) > 0) {
            log.info("Лайк от пользователя {} на отзыв {} успешно добавлен", userId, reviewId);
            return true;
        } else {
            log.info("Лайк на отзыв не добавлен");
            throw new ReviewNotFoundException("Лайк не добавлен");
        }
    }

    @Override
    public boolean addDislike(Integer reviewId, Integer userId) {
        String sqlQuery = "INSERT INTO PUBLIC.REVIEW_LIKES(REVIEW_ID, USER_ID, REVIEW_LIKE) " +
                "VALUES(?, ?, false)";
        if (jdbcTemplate.update(sqlQuery, reviewId, userId) > 0) {
            log.info("Дизлайк от пользователя {} на отзыв {} успешно добавлен", userId, reviewId);
            return true;
        } else {
            log.info("Дизлайк на отзыв не добавлен");
            throw new ReviewNotFoundException("Дизлайк не добавлен");
        }
    }

    @Override
    public boolean deleteLike(Integer reviewId, Integer userId) {
        String sql = "DELETE FROM review_likes WHERE review_id = ? AND user_id = ? AND review_like IS TRUE";
        if (jdbcTemplate.update(sql, reviewId, userId) > 0) {
            log.info("Лайк от пользователя {} на отзыв {} успешно удален", userId, reviewId);
            return true;
        } else {
            log.info("Лайк на отзыв не удален");
            throw new ReviewNotFoundException("Лайк не удален");
        }

    }

    @Override
    public boolean deleteDislike(Integer reviewId, Integer userId) {
        String sql = "DELETE FROM review_likes WHERE review_id = ? AND user_id = ? AND review_like IS FALSE ";
        if (jdbcTemplate.update(sql, reviewId, userId) > 0) {
            log.info("Дизлайк от пользователя {} на отзыв {} успешно удален", userId, reviewId);
            return true;
        } else {
            log.info("Дизлайк на отзыв не удален");
            throw new ReviewNotFoundException("Дизлайк не удален");
        }
    }

    private Review mapRowToReview(ResultSet rs, int rowNum) throws SQLException {
        return Review.builder()
                .reviewId(rs.getInt("review_id"))
                .content(rs.getString("content"))
                .isPositive(rs.getBoolean("is_positive"))
                .filmId(rs.getInt("film_id"))
                .userId(rs.getInt("user_id"))
                .useful(rs.getInt("useful"))
                .build();
    }
}
