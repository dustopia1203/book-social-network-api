package com.dustopia.book_social_network_api.exception;

import com.dustopia.book_social_network_api.model.response.ExceptionData;
import com.dustopia.book_social_network_api.model.response.ResponseObject;
import jakarta.mail.MessagingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDate;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseObject> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception, WebRequest request) {
        ExceptionData exceptionData = new ExceptionData(LocalDate.now(),
                "Validation failed! " + exception.getMessage(),
                request.getDescription(false)
        );
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ResponseObject("failed", exceptionData));
    }

    @ExceptionHandler(MessagingException.class)
    public ResponseEntity<ResponseObject> handleException(MessagingException exception, WebRequest request) {
        ExceptionData exceptionData = new ExceptionData(LocalDate.now(),
                "Mail sending error. " + exception.getMessage(),
                request.getDescription(false)
        );
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ResponseObject("failed", exceptionData));
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ResponseObject> handleUsernameNotFoundException(UsernameNotFoundException exception, WebRequest request) {
        ExceptionData exceptionData = new ExceptionData(LocalDate.now(),
                exception.getMessage() + ". Email not exist",
                request.getDescription(false)
        );
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ResponseObject("failed", exceptionData));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ResponseObject> handleException(RuntimeException exception, WebRequest request) {
        ExceptionData exceptionData = new ExceptionData(LocalDate.now(),
                exception.getMessage(),
                request.getDescription(false)
        );
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ResponseObject("failed", exceptionData));
    }

}
