package com.dustopia.book_social_network_api.exception;

import com.dustopia.book_social_network_api.model.response.ExceptionData;
import com.dustopia.book_social_network_api.model.response.ResponseObject;
import jakarta.mail.MessagingException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CloudinaryUploadException.class)
    public ResponseEntity<ResponseObject> handleCloudinaryUploadException(CloudinaryUploadException exception, WebRequest request) {
        ExceptionData exceptionData = new ExceptionData(
                LocalDate.now(),
                exception.getMessage(),
                request.getDescription(false)
        );
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ResponseObject("failed", exceptionData));
    }

    @ExceptionHandler(PermissionDeniedAccessException.class)
    public ResponseEntity<ResponseObject> handlePermissionDeniedAccessException(PermissionDeniedAccessException exception, WebRequest request) {
        ExceptionData exceptionData = new ExceptionData(
                LocalDate.now(),
                exception.getMessage(),
                request.getDescription(false)
        );
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new ResponseObject("failed", exceptionData));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseObject> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception, WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        exception.getBindingResult().getAllErrors().forEach(
                error -> {
                    String errorName = ((FieldError) error).getField();
                    String errorMessage = error.getDefaultMessage();
                    errors.put(errorName, errorMessage);
                }
        );
        StringBuilder exceptionMessage = new StringBuilder();
        errors.forEach((key, value) -> {
            exceptionMessage.append(key).append(": ").append(value).append("\n");
        });
        ExceptionData exceptionData = new ExceptionData(
                LocalDate.now(),
                "Validation failed! " + exceptionMessage.toString(),
                request.getDescription(false)
        );
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ResponseObject("failed", exceptionData));
    }

    @ExceptionHandler(MessagingException.class)
    public ResponseEntity<ResponseObject> handleMessageException(MessagingException exception, WebRequest request) {
        ExceptionData exceptionData = new ExceptionData(
                LocalDate.now(),
                "Mail sending error. " + exception.getMessage(),
                request.getDescription(false)
        );
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ResponseObject("failed", exceptionData));
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ResponseObject> handleUsernameNotFoundException(UsernameNotFoundException exception, WebRequest request) {
        ExceptionData exceptionData = new ExceptionData(
                LocalDate.now(),
                exception.getMessage(),
                request.getDescription(false)
        );
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ResponseObject("failed", exceptionData));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ResponseObject> handleEntityNotFoundException(EntityNotFoundException exception, WebRequest request) {
        ExceptionData exceptionData = new ExceptionData(
                LocalDate.now(),
                exception.getMessage(),
                request.getDescription(false)
        );
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ResponseObject("failed", exceptionData));
    }

    @ExceptionHandler(BookUnavailableException.class)
    public ResponseEntity<ResponseObject> handleBookUnavailableException(BookUnavailableException exception, WebRequest request) {
        ExceptionData exceptionData = new ExceptionData(
                LocalDate.now(),
                exception.getMessage(),
                request.getDescription(false)
        );
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ResponseObject("failed", exceptionData));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ResponseObject> handleException(RuntimeException exception, WebRequest request) {
        ExceptionData exceptionData = new ExceptionData(
                LocalDate.now(),
                exception.getMessage(),
                request.getDescription(false)
        );
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ResponseObject("failed", exceptionData));
    }

}
