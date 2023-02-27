package ru.yandex.practicum.filmorate.exceptions;

public class FilmAlreadyExistException extends RuntimeException {
    public FilmAlreadyExistException() {
    }

    public FilmAlreadyExistException(String message) {
        super(message);
    }
}
