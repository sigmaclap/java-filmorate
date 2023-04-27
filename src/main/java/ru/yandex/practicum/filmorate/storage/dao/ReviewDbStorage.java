package ru.yandex.practicum.filmorate.storage.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;

import java.util.List;
import java.util.Objects;

@Repository
@Slf4j
public class ReviewDbStorage implements ReviewStorage {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public ReviewDbStorage(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    public Review createReview(Review review) {
        MapSqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("content", review.getContent())
                .addValue("isPositive", review.isPositive())
                .addValue("userId", review.getUserId())
                .addValue("filmId", review.getFilmId())
                .addValue("useful", review.getUseful());
        KeyHolder keyHolder = new GeneratedKeyHolder();
        namedParameterJdbcTemplate.update("INSERT INTO REVIEW (content, isPositive, userId, filmId, useful) " +
                "VALUES (:content, :isPositive, :userId, :filmId, :useful)", sqlParameterSource, keyHolder);
        review.setReviewId(Objects.requireNonNull(keyHolder.getKey()).intValue());
        return review;
    }

    @Override
    public Review updateReview(Review review) {
        return null;
    }

    @Override
    public boolean deleteReview(Integer reviewId) {
        return false;
    }

    @Override
    public Review getReviewById(Integer reviewId) {
        return null;
    }

    @Override
    public List<Review> getReviewsByFilmId(Integer filmId, Integer count) {
        return null;
    }
}
