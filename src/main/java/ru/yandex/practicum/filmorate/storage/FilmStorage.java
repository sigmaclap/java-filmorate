package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

public interface FilmStorage {
    List<Film> getFilms();

    Film findFilmById(Integer filmId);

    Film create(Film film);

    Film update(Film film);

    List<Film> getFilmsWithCountLikes(Integer count);

    boolean likeFilm(Integer filmId, Integer userId);

    boolean deleteLike(Integer filmId, Integer userId);

    Genre getGenreById(Integer genreId);

    List<Genre> getGenresList();

    List<Mpa> getMpaList();

    Mpa getMpaById(Integer ratingId);

}
