package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/reviews")
public class ReviewController {
    private final ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    public Review createReview(@Valid @RequestBody Review review) {
        return reviewService.createReview(review);
    }

    @PutMapping
    public Review updateReview(@Valid @RequestBody Review review) {
        return reviewService.updateReview(review);
    }

    @DeleteMapping(value = "/{id}")
    public boolean deleteReview(@PathVariable("id") Integer reviewId) {
        return reviewService.deleteReview(reviewId);
    }

    @GetMapping(value = "/{id}")
    public Review getReviewById(@PathVariable("id") Integer reviewId) {
        return reviewService.getReviewById(reviewId);
    }

    @GetMapping()
    public List<Review> getReviewsByFilmId(@RequestParam(required = false) Integer filmId,
                                           @RequestParam(defaultValue = "10") Integer count) {
        return reviewService.getReviewsByFilmId(filmId, count);
    }

    @PutMapping(value = "/{id}/like/{userId}")
    public boolean addLike(@PathVariable("id") Integer reviewId, @PathVariable Integer userId) {
        return reviewService.addLike(reviewId, userId);
    }

    @PutMapping(value = "/{id}/dislike/{userId}")
    public boolean addDislike(@PathVariable("id") Integer reviewId, @PathVariable Integer userId) {
        return reviewService.addDislike(reviewId, userId);
    }

    @DeleteMapping(value = "/{id}/like/{userId}")
    public boolean deleteLike(@PathVariable("id") Integer reviewId, @PathVariable Integer userId) {
        return reviewService.deleteLike(reviewId, userId);
    }

    @DeleteMapping(value = "/{id}/dislike/{userId}")
    public boolean deleteDislike(@PathVariable("id") Integer reviewId, @PathVariable Integer userId) {
        return reviewService.deleteDislike(reviewId, userId);
    }
}
