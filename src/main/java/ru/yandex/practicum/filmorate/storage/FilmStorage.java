package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    List<Film> getFilms();

    Film findFilmById(Integer filmId);

    Film create(Film film);

    Film update(Film film);

    List<Film> getMostPopularFilms(Integer count, Integer genreId, Integer year);

    boolean likeFilm(Integer filmId, Integer userId);

    boolean deleteLike(Integer filmId, Integer userId);

    boolean deleteFilm(Integer filmId);

    List<Film> getFilmByTitleOrDirector(String queryName, String options);

    List<Film> getFilmByDirector(Integer directorId, String sortBy);

    List<Film> commonFilms(Integer userId, Integer friendId);

}
