package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.FilmAlreadyExistException;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.InvalidDataException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    private int idxFilms = 0;
    private final Map<Integer, Film> films = new HashMap<>();
    private final InMemoryUserStorage userStorage;

    @Override
    public List<Film> getFilms() {
        log.debug("Количество фильмов: {}", films.size());
        return new ArrayList<>(films.values());
    }

    @Override
    public Film findFilmById(Integer filmId) {
        List<Film> filmList = new ArrayList<>(films.values());
        return filmList.stream()
                .filter(p -> p.getId().equals(filmId))
                .findFirst()
                .orElseThrow(() -> new FilmNotFoundException(String.format("Пост № %d не найден", filmId)));
    }

    @Override
    public Film create(Film film) {
        int id = ++idxFilms;
        if (films.containsValue(film)) {
            log.error("Get ERROR {{}}, request /POST", "Такой фильм уже есть в коллекции");
            throw new FilmAlreadyExistException("Такой фильм уже есть в коллекции");
        } else if (film == null || film.toString().isEmpty()) {
            log.error("Get ERROR {{}}, request /POST", "Пустое значение Film");
            throw new InvalidDataException("Пустое значение Film");
        } else {
            film.setId(id);
            films.put(id, film);
            log.debug("Film created: {}", film);
            return film;
        }
    }

    @Override
    public Film update(Film film) {
        if (film.getId() == null || !films.containsKey(film.getId())) {
            log.error("Get ERROR {{}}, request /PUT", "Невозможно обновить фильм, не найден ID");
            throw new FilmNotFoundException("Невозможно обновить фильм, не найден ID.");
        }
        films.remove(film.getId());
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public List<Film> getFilmsWithCountLikes(Integer count) {
        return films.values().stream()
                .sorted((o1, o2) -> o2.getLikes().size() - o1.getLikes().size())
                .limit(count)
                .collect(Collectors.toList());
    }

    @Override
    public Film likeFilm(Integer filmId, Integer userId) {
        if (filmId == null || userId == null) {
            throw new InvalidDataException("Некорректно переданы значения, невозможно поставить лайк");
        }
        Film film = films.get(filmId);
        if (film.getLikes().contains(userId.longValue())) {
            throw new InvalidDataException("Данный пользователь уже ставил лайк");
        } else if (films.containsKey(film.getId()) && userStorage.getMapUsers().containsKey(userId)) {
            log.info("Лайк от пользователя с {} успешно добавлен", userId);
            film.getLikes().add(userId.longValue());
            return film;
        } else {
            throw new FilmNotFoundException("Фильм не найден");
        }
    }

    @Override
    public Film deleteLike(Integer filmId, Integer userId) {
        if (filmId == null || userId == null) {
            throw new InvalidDataException("Некорректно переданы значения, невозможно удалить лайк");
        }
        Film film = films.get(filmId);
        if (films.containsKey(filmId) && userStorage.getMapUsers().containsKey(userId)
                && film.getLikes().contains(userId.longValue())) {
            film.getLikes().remove(userId.longValue());
            log.info("Лайк с фильма - {} успешно удален", film.getName());
            return film;
        } else {
            throw new UserNotFoundException("Некорректно переданы данные");
        }
    }
}
