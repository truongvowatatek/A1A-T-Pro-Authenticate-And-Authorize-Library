package com.a1a.shared.auth.domain.exception;

/**
 * Exception thrown when authentication fails (e.g., invalid token, expired
 * token)
 */
public class AuthenticationException extends IamDomainException {

    public AuthenticationException(String message) {
        super(message);
    }

    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}



