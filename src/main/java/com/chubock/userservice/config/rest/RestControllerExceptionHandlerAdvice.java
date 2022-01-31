package com.chubock.userservice.config.rest;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class RestControllerExceptionHandlerAdvice {

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<MessagesResponse> handleRuntimeExceptions(ConstraintViolationException ex) {

        MessagesResponse response = new MessagesResponse();

        ex.getConstraintViolations().forEach(error -> {
            String fieldName = error.getPropertyPath().toString();
            String errorMessage = error.getMessage();
            response.addMessage(new Message(fieldName, errorMessage));
        });

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);

    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex) {

        MessagesResponse response = new MessagesResponse();

        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            response.addMessage(new Message(fieldName, errorMessage));
        });

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);

    }

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<MessagesResponse> handleRuntimeExceptions(Exception ex) {
        MessagesResponse response = new MessagesResponse();
        response.addMessage(new Message(ex.getMessage()));
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @Data
    @NoArgsConstructor
    public static class Message {

        private String message;
        private String field;

        public Message(String message) {
            this.message = message;
        }

        public Message(String field, String message) {
            this.field = field;
            this.message = message;
        }

    }

    @Data
    @NoArgsConstructor
    public static class MessagesResponse {
        private List<Message> messages = new ArrayList<>();

        public void addMessage(Message message) {
            getMessages().add(message);
        }
    }

}
