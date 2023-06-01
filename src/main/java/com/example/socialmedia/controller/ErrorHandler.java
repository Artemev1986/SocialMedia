package com.example.socialmedia.controller;

import com.example.socialmedia.exception.ApiError;
import com.example.socialmedia.exception.CurrencyNotValidException;
import com.example.socialmedia.exception.ForbiddenException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.persistence.EntityNotFoundException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {
    private final ApiError apiError = new ApiError();

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = {MethodArgumentNotValidException.class, CurrencyNotValidException.class})
    public ResponseEntity<?> handleMethodArgumentNotValid(final Throwable e) {
        apiError.setMessage(e.getMessage());
        apiError.setStatus("BAD_REQUEST");
        apiError.setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        log.warn(String.valueOf(e));
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler
    public ResponseEntity<?> handleNotFoundException(final EntityNotFoundException e) {
        apiError.setMessage(e.getMessage());
        apiError.setStatus("NOT_FOUND");
        apiError.setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        log.warn(String.valueOf(e));
        return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
    }


    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler
    public ResponseEntity<?> handleSqlException(final SQLException e) {
        apiError.setMessage(e.getMessage());
        apiError.setStatus("CONFLICT");
        apiError.setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        log.warn(String.valueOf(e));
        return new ResponseEntity<>(apiError, HttpStatus.CONFLICT);
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler
    public ResponseEntity<?> handleForbiddenExceptions(final ForbiddenException e) {
        apiError.setMessage(e.getMessage());
        apiError.setStatus("FORBIDDEN");
        apiError.setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        log.warn(String.valueOf(e));
        return new ResponseEntity<>(apiError, HttpStatus.FORBIDDEN);
    }
}