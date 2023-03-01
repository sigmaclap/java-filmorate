package ru.yandex.practicum.filmorate.exceptions;

import org.springframework.http.HttpStatus;

public class FilmAlreadyExistException extends RuntimeException {
    private HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;

    public FilmAlreadyExistException() {
    }

    public FilmAlreadyExistException(String message) {
        super(message);
    }

    public FilmAlreadyExistException(HttpStatus httpStatus, String message) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
