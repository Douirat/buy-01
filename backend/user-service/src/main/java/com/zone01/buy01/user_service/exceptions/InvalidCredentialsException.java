package com.zone01.buy01.user_service.exceptions;

import org.springframework.http.HttpStatus;

/** Login failed — wrong email or password. -> 401 */
public class InvalidCredentialsException extends ApiException {
    public InvalidCredentialsException() {
        super(HttpStatus.UNAUTHORIZED, "Invalid email or password");
    }
}