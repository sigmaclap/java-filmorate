package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.FilmAlreadyExistException;
import ru.yandex.practicum.filmorate.exceptions.InvalidDataException;
import ru.yandex.practicum.filmorate.json.ErrorJson;
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
        return new ArrayList<>(films.values());
    }

    @PostMapping(value = "/films")
    public Film createFilm(@Valid @RequestBody Film film) {
        return create(film);
    }

    @PutMapping(value = "/films")
    public Film updateFilms(@Valid @RequestBody Film film) {
        return update(film);
    }

    private Film create(Film film) {
        int id = ++idxFilms;
        if (films.containsValue(film)) {
            log.error("Get ERROR {{}}, request /POST", "Такой фильм уже есть в коллекции");
            throw new FilmAlreadyExistException(HttpStatus.BAD_REQUEST,
                    ErrorJson.Response("Такой фильм уже есть в коллекции"));
        } else if (film == null || film.toString().isEmpty()) {
            log.error("Get ERROR {{}}, request /POST", "Пустое значение Film");
            throw new InvalidDataException(HttpStatus.BAD_REQUEST,
                    ErrorJson.Response("Пустое значение Film"));
        } else {
            film.setId(id);
            films.put(id, film);
            log.debug("Film created: {}", film);
            return film;
        }
    }

    private Film update(Film film) {
        if (film.getId() == null || !films.containsKey(film.getId())) {
            log.error("Get ERROR {{}}, request /PUT", "Невозможно обновить фильм, не найден ID");
            throw new InvalidDataException(HttpStatus.NOT_FOUND,
                    ErrorJson.Response("Невозможно обновить фильм, не найден ID"));
        }
        films.remove(film.getId());
        films.put(film.getId(), film);
        return film;
    }

    @ExceptionHandler(FilmAlreadyExistException.class)
    public ResponseEntity handleException(FilmAlreadyExistException e) {
        return ResponseEntity.status(e.getHttpStatus()).body(e.getMessage());
    }

    @ExceptionHandler(InvalidDataException.class)
    public ResponseEntity handleException(InvalidDataException e) {
        return ResponseEntity.status(e.getHttpStatus()).body(e.getMessage());
    }
}
