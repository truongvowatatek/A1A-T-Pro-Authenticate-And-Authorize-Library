package com.a1a.shared.auth.domain.exception;

/**
 * Exception thrown when token verification fails.
 *
 * <p>This occurs when: - Token signature is invalid - Token is malformed - Token cannot be parsed
 */
public class TokenVerificationException extends IamDomainException {
    public TokenVerificationException(String message) {
        super(message);
    }

    public TokenVerificationException(String message, Throwable cause) {
        super(message, cause);
    }
}



