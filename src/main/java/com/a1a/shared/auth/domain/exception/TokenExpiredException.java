package com.a1a.shared.auth.domain.exception;

/**
 * Exception thrown when a token has expired.
 *
 * <p>This occurs when the current time is after the token's expiration time (exp claim).
 */
public class TokenExpiredException extends IamDomainException {
    public TokenExpiredException(String message) {
        super(message);
    }

    public TokenExpiredException(String message, Throwable cause) {
        super(message, cause);
    }
}



