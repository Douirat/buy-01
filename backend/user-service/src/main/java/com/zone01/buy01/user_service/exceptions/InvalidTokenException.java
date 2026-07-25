package com.zone01.buy01.user_service.exceptions;

import org.springframework.http.HttpStatus;

/** JWT is missing, malformed, expired, or fails signature validation. -> 401 */
public class InvalidTokenException extends ApiException {
    public InvalidTokenException(String message) {
        super(HttpStatus.UNAUTHORIZED, message);
    }

    public static InvalidTokenException expired() {
        return new InvalidTokenException("Token has expired");
    }

    public static InvalidTokenException malformed() {
        return new InvalidTokenException("Token is malformed or invalid");
    }
}