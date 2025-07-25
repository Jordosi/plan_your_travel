package ru.jordosi.travel_planner.advice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.jordosi.travel_planner.dto.error.ErrorResponse;
import ru.jordosi.travel_planner.exception.EmailAlreadyExistsException;
import ru.jordosi.travel_planner.exception.InvalidNationalityException;
import ru.jordosi.travel_planner.exception.UserNotFoundException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalControllerAdvice {
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorResponse> handleBindException(BindException ex) {
        String errorMessage = "Binding exception";
        Map<String, Object> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach((fieldError) -> {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        });

        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message(errorMessage)
                .errors(errors)
                .build();
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleEmailAlreadyExistsException(EmailAlreadyExistsException ex) {
        String errorMessage = ex.getMessage();
        Map<String, Object> errors = new HashMap<>();
        errors.put(errorMessage, ex.getCause());

        ErrorResponse response = ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message(errorMessage)
                .errors(errors)
                .build();

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(InvalidNationalityException.class)
    public ResponseEntity<ErrorResponse> handleInvalidNationalityException(InvalidNationalityException ex) {
        String errorMessage = ex.getMessage();
        Map<String, Object> errors = new HashMap<>();
        errors.put(errorMessage, ex.getCause());

        ErrorResponse response = ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message(errorMessage)
                .errors(errors)
                .build();

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public  ResponseEntity<ErrorResponse> handleUserNotFoundException(UserNotFoundException ex) {
        String errorMessage = ex.getMessage();
        Map<String, Object> errors = new HashMap<>();
        errors.put(errorMessage, ex.getCause());

        ErrorResponse response = ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message(errorMessage)
                .errors(errors)
                .build();

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleOtherException(Exception ex) {
        String errorMessage = ex.getMessage();
        Map<String, Object> errors = new HashMap<>();
        errors.put(errorMessage, ex.getCause());

        ErrorResponse response = ErrorResponse.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message(errorMessage)
                .errors(errors)
                .build();

        return ResponseEntity.internalServerError().body(response);
    }
}
