package com.group3.learningcommonsproject.errorResponses;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

// Turns BadRequest into a 400 with the validation message, e.g. {"error": "Name is required."}.
// Scoped to @RestController classes so the HTML pages keep handling errors on the form.
@RestControllerAdvice(annotations = org.springframework.web.bind.annotation.RestController.class)
public class ApiExceptionHandler {

    @ExceptionHandler(BadRequest.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleBadRequest(BadRequest ex) {
        return Map.of("error", ex.getMessage());
    }
}
