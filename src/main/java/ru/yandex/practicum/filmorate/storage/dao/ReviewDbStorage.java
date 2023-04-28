package ru.yandex.practicum.filmorate.storage.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.ReviewNotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Repository
@Slf4j
public class ReviewDbStorage implements ReviewStorage {
    private final JdbcTemplate jdbcTemplate;

    public ReviewDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Review createReview(Review review) {
        String sql = "INSERT INTO REVIEW (content, isPositive, userId, filmId) VALUES (?, ?, ?, ?)";
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
        String sql = "UPDATE REVIEWS SET content = ? is_positive = ? WHERE review_id = ?";
        jdbcTemplate.update(sql, review.getContent(), review.getIsPositive(), review.getReviewId());
        return getReviewById(review.getReviewId());
    }

    @Override
    public boolean deleteReview(Integer reviewId) {
        return false;
    }

    @Override
    public List<Review> getAllReviews() {
        SqlRowSet reviewRows = jdbcTemplate.queryForRowSet("SELECT r.id, r.content, r.is_positive, r.user_id, r.film_id,\n" +
                "SUM(CASE\n" +
                "WHEN rl.is_like IS TRUE THEN 1 \n" +
                "WHEN rl.is_like IS FALSE THEN -1 " +
                "ELSE 0 END) AS useful\n" +
                "FROM review r  \n" +
                "LEFT JOIN REVIEW_LIKES rl ON r.ID = rl.REVIEW_ID \n" +
                "GROUP BY r.ID\n" +
                "ORDER BY useful DESC ");
        List<Review> reviews = new ArrayList<>();
        while (reviewRows.next()) {
            Review review = getReviewFromSql(reviewRows);
            reviews.add(review);
        }
        return reviews;
    }

    @Override
    public Review getReviewById(Integer reviewId) {
        String sql = "SELECT review.review_id, review.content, review.is_positive, review.film_id, review.user_id, " +
                "SUM (CASE WHEN rl.review_like IS TRUE THEN 1 WHEN rl.review_like IS FALSE THEN -1 ELSE 0 END) AS useful FROM review" +
                "LEFT JOIN review_likes rl ON review.id = rl.review_id WHERE review.review_id = ? GROUP BY review.review_id";
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql, reviewId);
        if (sqlRowSet.next())
            return getReviewFromSql(sqlRowSet);
        else
            throw new ReviewNotFoundException("Отзыв не найден");
    }

    @Override
    public List<Review> getReviewsByFilmId(Integer filmId, Integer count) {
        return null;
    }

    @Override
    public Review addLike(Integer reviewId, Integer userId) {
        return getReviewById(reviewId);
    }

    @Override
    public Review addDislike(Integer reviewId, Integer userId) {
        return getReviewById(reviewId);
    }

    @Override
    public Review deleteLike(Integer reviewId, Integer userId) {
        String sql = "DELETE FROM review_likes WHERE review_id = ? AND user_id = ? AND review_like IS TRUE ";
        jdbcTemplate.update(sql, reviewId, userId);
        return getReviewById(reviewId);
    }

    @Override
    public Review deleteDislike(Integer reviewId, Integer userId) {
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
