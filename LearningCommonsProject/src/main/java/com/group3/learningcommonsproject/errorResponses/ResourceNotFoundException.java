package com.group3.learningcommonsproject.errorResponses;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Resource not found")
public class ResourceNotFoundException extends RuntimeException {
    //For methods that don't reuturn ResponseEntity
    public ResourceNotFoundException() {
        super();
    }
}