package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewStorage {
    Review createReview(Review review);

    Review updateReview(Review review);

    boolean deleteReview(Integer reviewId);

    Review getReviewById(Integer reviewId);

    List<Review> getReviewsByFilmId(Integer filmId, Integer count);

    List<Review> getAllReviews();

    boolean addLike(Integer reviewId, Integer userId);

    boolean addDislike(Integer reviewId, Integer userId);

    boolean deleteLike(Integer reviewId, Integer userId);

    boolean deleteDislike(Integer reviewId, Integer userId);
}
