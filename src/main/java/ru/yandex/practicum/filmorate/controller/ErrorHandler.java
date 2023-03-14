package ru.yandex.practicum.filmorate.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exceptions.*;
import ru.yandex.practicum.filmorate.json.ErrorResponse;

@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInvalidDataException(final InvalidDataException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleFilmAlreadyExistException(final FilmAlreadyExistException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleUserAlreadyExistException(final UserAlreadyExistException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleFilmNotFoundException(final FilmNotFoundException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUserNotFoundException(final UserNotFoundException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleThrowable(final Throwable e) {
        return new ErrorResponse("Произошла непредвиденная ошибка.");
    }

    //    @ExceptionHandler(FilmAlreadyExistException.class)
//    public ResponseEntity handleException(FilmAlreadyExistException e) {
//        return ResponseEntity.status(e.getHttpStatus()).body(e.getMessage());
//    }
//
//    @ExceptionHandler(InvalidDataException.class)
//    public ResponseEntity handleException(InvalidDataException e) {
//        return ResponseEntity.status(e.getHttpStatus()).body(e.getMessage());
//    }

//    @ExceptionHandler({FilmAlreadyExistException.class, InvalidDataException.class})
//    public ResponseEntity handleException(RuntimeException e) {
//        return ResponseEntity.status(e.getHttpStatus()).body(e.getMessage());
//    }

//    @ExceptionHandler
//    public Map<String, String> handleNegativeCount(final InvalidDataException e) {
//        return Map.of("error", "Невозможно обновить фильм, не найден ID.");
//    }

    //    @ExceptionHandler(UserAlreadyExistException.class)
//    public ResponseEntity handleException(UserAlreadyExistException e) {
//        return ResponseEntity.status(e.getHttpStatus()).body(e.getMessage());
//    }
//
//    @ExceptionHandler(InvalidDataException.class)
//    public ResponseEntity handleException(InvalidDataException e) {
//        return ResponseEntity.status(e.getHttpStatus()).body(e.getMessage());
//    }
}
