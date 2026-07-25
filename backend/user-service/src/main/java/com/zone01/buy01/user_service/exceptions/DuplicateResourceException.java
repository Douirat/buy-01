package com.zone01.buy01.user_service.exceptions;

import org.springframework.http.HttpStatus;

/** Attempting to create a resource that violates a uniqueness constraint. -> 409 */
public class DuplicateResourceException extends ApiException {
    public DuplicateResourceException(String message) {
        super(HttpStatus.CONFLICT, message);
    }

    public static DuplicateResourceException emailAlreadyInUse(String email) {
        return new DuplicateResourceException("Email already in use: " + email);
    }
}