package com.a1a.shared.auth.domain.exception;

/**
 * Base exception for all IAM domain errors.
 *
 * <p>This is the parent exception for authentication and authorization failures.
 */
public class IamDomainException extends RuntimeException {
    public IamDomainException(String message) {
        super(message);
    }

    public IamDomainException(String message, Throwable cause) {
        super(message, cause);
    }
}



