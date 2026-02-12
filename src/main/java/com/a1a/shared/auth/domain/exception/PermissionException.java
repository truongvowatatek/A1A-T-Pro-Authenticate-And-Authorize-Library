package com.a1a.shared.auth.domain.exception;

/** Exception thrown when permission validation fails */
public class PermissionException extends IamDomainException {

    public PermissionException(String message) {
        super(message);
    }

    public PermissionException(String message, Throwable cause) {
        super(message, cause);
    }
}



