package ru.yandex.practicum.filmorate.storage.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.ReviewNotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.*;

@Repository
@Slf4j
public class ReviewDbStorage implements ReviewStorage {
    private static String USEFUL = "SUM(CASE WHEN rl.review_like IS TRUE THEN 1 WHEN rl.review_like IS FALSE THEN -1 ELSE 0 END)";
    private static String ALL = "SELECT r.review_id, r.content, r.is_positive, r.film_id, r.user_id, " + USEFUL +
            " AS useful FROM REVIEW r LEFT JOIN REVIEW_LIKES rl ON r.review_id = rl.review_id";
    private final JdbcTemplate jdbcTemplate;

    public ReviewDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Review createReview(Review review) {
        String sql = "INSERT INTO review(content, is_positive, film_id, user_id) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, review.getContent());
            statement.setBoolean(2, review.getIsPositive());
            statement.setLong(3, review.getUserId());
            statement.setLong(4, review.getFilmId());
            return statement;
        }, keyHolder);
        review.setReviewId(keyHolder.getKey().intValue());
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
        SqlRowSet srs = jdbcTemplate.queryForRowSet(ALL + " GROUP BY r.review_id ORDER BY useful DESC");
        List<Review> reviews = new ArrayList<>();
        while (srs.next()) {
            Review review = getReviewFromSql(srs);
            reviews.add(review);
        }
        return reviews;
    }

    @Override
    public Review getReviewById(Integer reviewId) {
        SqlRowSet srs = jdbcTemplate.queryForRowSet(ALL + " WHERE r.review_id = ? GROUP BY r.review_id", reviewId);
        if (srs.next())
            return getReviewFromSql(srs);
        else
            throw new ReviewNotFoundException("Отзыв не найден");
    }

    @Override
    public List<Review> getReviewsByFilmId(Integer filmId, Integer count) {
        SqlRowSet srs = jdbcTemplate.queryForRowSet(ALL +
                " WHERE r.film_id = ? GROUP BY r.review_id ORDER BY useful DESC LIMIT ?", filmId, count);
        List<Review> reviews = new ArrayList<>();
        while (srs.next()) {
            Review review = getReviewFromSql(srs);
            reviews.add(review);
        }
        return reviews;
    }

    @Override
    public Review addLike(Integer reviewId, Integer userId) {
        return null;
    }

    @Override
    public Review addDislike(Integer reviewId, Integer userId) {
        return null;
    }

    @Override
    public Review deleteLike(Integer reviewId, Integer userId) {
        String sql = "DELETE FROM review_likes WHERE review_id = ? AND user_id = ? AND review IS TRUE";
        jdbcTemplate.update(sql, reviewId, userId);
        return getReviewById(reviewId);
    }

    @Override
    public Review deleteDislike(Integer reviewId, Integer userId) {
        String sql = "DELETE FROM review_likes WHERE review_id = ? AND user_id = ? AND review_like IS FALSE ";
        jdbcTemplate.update(sql, reviewId, userId);
        return getReviewById(reviewId);
    }

    private Review getReviewFromSql(SqlRowSet sqlRowSet) {
        return Review.builder()
                .reviewId(sqlRowSet.getInt("review_id"))
                .content(sqlRowSet.getString("content"))
                .isPositive(sqlRowSet.getBoolean("is_positive"))
                .filmId(sqlRowSet.getInt("film_id"))
                .userId(sqlRowSet.getInt("user_id"))
                .useful(sqlRowSet.getInt("useful"))
                .build();
    }
}
