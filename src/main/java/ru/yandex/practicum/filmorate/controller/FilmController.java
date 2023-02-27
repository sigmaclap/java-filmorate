package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.FilmAlreadyExistException;
import ru.yandex.practicum.filmorate.exceptions.InvalidDataException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
public class FilmController {
    private int idxFilms = 0;
    private final Map<Integer, Film> films = new HashMap<>();

    @GetMapping("/films")
    public List<Film> getFilms() {
        log.debug("Количество фильмов: {}", films.size());
        log.debug("Cписок фильмов: {}", films);
        return new ArrayList<>(films.values());
    }

    @PostMapping(value = "/films")
    public Film createFilm(@Valid @RequestBody Film film) {
        int id = ++idxFilms;
        if (films.containsValue(film)) {
            log.error("Get ERROR {}, request /POST", FilmAlreadyExistException.class);
            throw new FilmAlreadyExistException("FilmAlreadyExistException");
        } else if (film == null || film.toString().isEmpty()) {
            log.error("Get ERROR {}, request /POST", InvalidDataException.class);
            throw new InvalidDataException("InvalidDataException");
        } else {
            film.setId(id);
            films.put(id, film);
            log.debug("Film created: {}", film);
            return film;
        }
    }

    @PutMapping(value = "/films")
    public Film updateFilms(@Valid @RequestBody Film film) {
        if (films.containsKey(film.getId())) {
            films.remove(film.getId());
            films.put(film.getId(), film);
            return film;
        } else {
            log.error("Get ERROR {}, request /PUT", InvalidDataException.class);
            throw new InvalidDataException("Invalid Data");
        }
    }
}
