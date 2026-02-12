package com.a1a.shared.auth.domain.exception;

/**
 * Exception thrown when role validation fails.
 *
 * <p>
 * This occurs when a user attempts to access functionality requiring a specific
 * role or set of
 * roles that they do not possess.
 */
public class RoleException extends IamDomainException {

    public RoleException(String message) {
        super(message);
    }

    public RoleException(String message, Throwable cause) {
        super(message, cause);
    }
}



