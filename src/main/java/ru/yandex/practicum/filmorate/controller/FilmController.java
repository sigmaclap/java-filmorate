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
    public List<Film> getMostPopularFilms(@RequestParam(defaultValue = "10") Integer count,
                                          @RequestParam(defaultValue = "0") Integer genreId,
                                          @RequestParam(defaultValue = "0") Integer year) {
        return filmService.getMostPopularFilms(count, genreId, year);
    }

    @PutMapping("/{id}/like/{userId}")
    public boolean likeFilm(@PathVariable("id") Integer filmId, @PathVariable("userId") Integer userId) {
        return filmService.likeFilm(filmId, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public boolean deleteLike(@PathVariable("id") Integer filmId, @PathVariable("userId") Integer userId) {
        return filmService.deleteLike(filmId, userId);
    }

    @DeleteMapping("/{id}")
    public boolean deleteFilmById(@PathVariable("id") Integer filmId) {
        return filmService.deleteFilmById(filmId);
    }

    @GetMapping("/search")
    public List<Film> getFilmByTitleOrDirector(@RequestParam(name = "query") String queryName,
                                               @RequestParam(name = "by", required = false) String options) {
        return filmService.getFilmByTitleOrDirector(queryName, options);
    }

    @GetMapping("/director/{directorId}")
    public List<Film> getFilmByDirector(@PathVariable("directorId") Integer directorId,
                                        @RequestParam(name = "sortBy") String sortBy) {
        return filmService.getFilmByDirector(directorId, sortBy);
    }

    @GetMapping("/common")
    public List<Film> commonFilms(@RequestParam("userId") Integer userId,
                                  @RequestParam("friendId") Integer friendId) {
        return filmService.commonFilms(userId, friendId);
    }
}
