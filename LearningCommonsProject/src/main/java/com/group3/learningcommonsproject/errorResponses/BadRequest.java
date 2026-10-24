package com.group3.learningcommonsproject.errorResponses;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Bad Request")
public class BadRequest extends RuntimeException {
    //For methods that don't reuturn ResponseEntity
    public BadRequest() {
        super();
    }
}