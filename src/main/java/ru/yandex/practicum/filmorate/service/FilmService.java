package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.List;

@Service
public class FilmService {
    private final FilmStorage filmStorage;

    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public List<Film> getFilms() {
        return filmStorage.getFilms();
    }

    public Film findFilmById(Integer filmId) {
        return filmStorage.findFilmById(filmId);
    }

    public Film create(Film film) {
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        return filmStorage.update(film);
    }

    public List<Film> getFilmsWithCountLikes(Integer count) {
        return filmStorage.getFilmsWithCountLikes(count);
    }

    public boolean likeFilm(Integer filmId, Integer userId) {
        return filmStorage.likeFilm(filmId, userId);
    }

    public boolean deleteLike(Integer filmId, Integer userId) {
        return filmStorage.deleteLike(filmId, userId);
    }

    public List<Genre> getGenres() {
        return filmStorage.getGenresList();
    }

    public Genre getGenreById(Integer genreId) {
        return filmStorage.getGenreById(genreId);
    }

    public List<Mpa> getMpaList() {
        return filmStorage.getMpaList();
    }

    public Mpa getMpaById(Integer mpaId) {
        return filmStorage.getMpaById(mpaId);
    }

}
