package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.List;

@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final GenreStorage genreStorage;

    private final MpaStorage mpaStorage;

    public FilmService(FilmStorage filmStorage, GenreStorage genreStorage, MpaStorage mpaStorage) {
        this.filmStorage = filmStorage;
        this.genreStorage = genreStorage;
        this.mpaStorage = mpaStorage;
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

    public List<Film> getMostPopularFilms(Integer count, Integer genreId, Integer year) {
        return filmStorage.getMostPopularFilms(count, genreId, year);
    }

    public boolean likeFilm(Integer filmId, Integer userId) {
        return filmStorage.likeFilm(filmId, userId);
    }

    public boolean deleteLike(Integer filmId, Integer userId) {
        return filmStorage.deleteLike(filmId, userId);
    }

    public List<Genre> getGenres() {
        return genreStorage.getGenresList();
    }

    public Genre getGenreById(Integer genreId) {
        return genreStorage.getGenreById(genreId);
    }

    public List<Mpa> getMpaList() {
        return mpaStorage.getMpaList();
    }

    public Mpa getMpaById(Integer mpaId) {
        return mpaStorage.getMpaById(mpaId);
    }

    public List<Film> getFilmByTitleOrDirector(String queryName, String options) {
        return filmStorage.getFilmByTitleOrDirector(queryName, options);
    }

    public List<Film> getFilmByDirector(Integer directorId, String sortBy) {
        return filmStorage.getFilmByDirector(directorId, sortBy);
    }

    public List<Film> commonFilms(Integer userId, Integer friendId) {
        return filmStorage.commonFilms(userId, friendId);
    }
}
