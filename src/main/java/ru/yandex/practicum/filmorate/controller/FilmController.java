package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/films")
@AllArgsConstructor
public class FilmController {
    private final FilmService filmService;

    @GetMapping()
    public List<Film> getFilms() {
        return filmService.getFilms();
    }

    @GetMapping("/{id}")
    public Film findFilm(@PathVariable("id") Integer filmId) {
        return filmService.findFilmById(filmId);
    }

    @PostMapping()
    public Film createFilm(@Valid @RequestBody Film film) {
        return filmService.create(film);
    }

    @PutMapping()
    public Film updateFilms(@Valid @RequestBody Film film) {
        return filmService.update(film);
    }

    @GetMapping("/popular")
    public List<Film> getFilmsWithCountLikes(
            @RequestParam(defaultValue = "10") Integer count) {
        return filmService.getFilmsWithCountLikes(count);
    }

    @PutMapping("/{id}/like/{userId}")
    public Film likeFilm(@PathVariable("id") Integer filmId, @PathVariable("userId") Integer userId) {
        return filmService.likeFilm(filmId, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Film deleteLike(@PathVariable("id") Integer filmId, @PathVariable("userId") Integer userId) {
        return filmService.deleteLike(filmId, userId);
    }
}
