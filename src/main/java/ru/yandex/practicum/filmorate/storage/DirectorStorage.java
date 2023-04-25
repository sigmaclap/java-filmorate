package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

public interface DirectorStorage {

    List<Director> getDirectorsList();

    Director getDirectorById(Integer directorId);

    Director createDirector(Director director);

    Director updateDirector(Director director);

    boolean deleteDirector(Integer directorId);

    List<Director> getDirectorsByFilmId(Integer filmId);

}
