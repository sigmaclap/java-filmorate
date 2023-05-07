package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewStorage reviewStorage;

    public Review createReview(Review review) {
        return reviewStorage.createReview(review);
    }

    public Review updateReview(Review review) {
        return reviewStorage.updateReview(review);
    }

    public boolean deleteReview(Integer reviewId) {
        return reviewStorage.deleteReview(reviewId);
    }

    public Review getReviewById(Integer reviewId) {
        return reviewStorage.getReviewById(reviewId);
    }

    public List<Review> getReviewsByFilmId(Integer filmId, Integer count) {
        return reviewStorage.getReviewsByFilmId(filmId, count);
    }

    public boolean addLike(Integer reviewId, Integer userId) {
        return reviewStorage.addLike(reviewId, userId);
    }

    public boolean addDislike(Integer reviewId, Integer userId) {
        return reviewStorage.addDislike(reviewId, userId);
    }

    public boolean deleteLike(Integer reviewId, Integer userId) {
        return reviewStorage.deleteLike(reviewId, userId);
    }

    public boolean deleteDislike(Integer reviewId, Integer userId) {
        return reviewStorage.deleteDislike(reviewId, userId);
    }
}
