package com.group3.learningcommonsproject.errorResponses;

// Thrown when a request breaks a business rule. The message is safe to show to the user:
// the page controller puts it on the form, and ApiExceptionHandler returns it as JSON.
public class BadRequest extends RuntimeException {
    public BadRequest(String message) {
        super(message);
    }
}
