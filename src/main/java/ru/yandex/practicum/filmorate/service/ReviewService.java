package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class ReviewService {
    private final ReviewStorage reviewStorage;

    public ReviewService(ReviewStorage reviewStorage) {
        this.reviewStorage = reviewStorage;
    }

    public Review createReview(Review review) {
        return reviewStorage.createReview(review);
    }

    public Review updateReview(Review review) {
        return reviewStorage.updateReview(review);
    }

    public boolean deleteReview(Integer reviewId) {
        return reviewStorage.deleteReview(reviewId);
    }

    public List<Review> getAllReviews() {
        return reviewStorage.getAllReviews();
    }

    public Review getReviewById(Integer reviewId) {
        return reviewStorage.getReviewById(reviewId);
    }

    public List<Review> getReviewsByFilmId(Integer filmId, Integer count) {
        return reviewStorage.getReviewsByFilmId(filmId, count);
    }

    public Review addLike(Integer reviewId, Integer userId) {
        return reviewStorage.addLike(reviewId, userId);
    }

    public Review addDislike (Integer reviewId, Integer userId) {
        return reviewStorage.addDislike(reviewId, userId);
    }

    public Review deleteLike(Integer reviewId, Integer userId) {
        return reviewStorage.deleteLike(reviewId, userId);
    }

    public Review deleteDislike(Integer reviewId, Integer userId) {
        return reviewStorage.deleteDislike(reviewId, userId);
    }
}
