package ru.yandex.practicum.filmorate.exceptions;

import org.springframework.http.HttpStatus;

public class InvalidDataException extends RuntimeException {
    private HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;

    public InvalidDataException() {
    }


    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public InvalidDataException(String message) {
        super(message);
    }

    public InvalidDataException(HttpStatus httpStatus, String message) {
        super(message);
        this.httpStatus = httpStatus;
    }
}
