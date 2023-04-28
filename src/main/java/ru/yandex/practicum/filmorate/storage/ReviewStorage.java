package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewStorage {
    Review createReview(Review review);
    Review updateReview(Review review);
    boolean deleteReview(Integer reviewId);
    Review getReviewById(Integer reviewId);
    List<Review> getReviewsByFilmId(Integer filmId, Integer count);
    List<Review> getAllReviews();
    Review addLike(Integer reviewId, Integer userId);
    Review addDislike(Integer reviewId, Integer userId);
    Review deleteLike(Integer reviewId, Integer userId);
    Review deleteDislike(Integer reviewId, Integer userId);
}
