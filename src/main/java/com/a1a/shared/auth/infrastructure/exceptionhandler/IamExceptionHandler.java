package com.a1a.shared.auth.infrastructure.exceptionhandler;

import com.a1a.shared.auth.domain.exception.AuthenticationException;
import com.a1a.shared.auth.domain.exception.IamDomainException;
import com.a1a.shared.auth.domain.exception.PermissionException;
import com.a1a.shared.auth.domain.exception.RoleException;
import com.a1a.shared.auth.domain.exception.TokenExpiredException;
import com.a1a.shared.auth.domain.exception.TokenVerificationException;
import com.a1a.shared.auth.application.dto.ApiError;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Global exception handler for IAM domain exceptions.
 *
 * <p>
 * Handles authentication and authorization errors, returning appropriate HTTP
 * status codes with
 * standardized ApiError responses.
 * 
 * <p>
 * Uses {@link Order} with {@link Ordered#HIGHEST_PRECEDENCE} to ensure IAM
 * exceptions are
 * handled before generic exception handlers.
 */
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class IamExceptionHandler {

    /**
     * Handle token expiration errors.
     *
     * @param ex the exception
     * @return 401 Unauthorized response
     */
    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<ApiError> handleTokenExpired(TokenExpiredException ex) {
        log.warn("Token expired: {}", ex.getMessage());

        ApiError apiError = new ApiError(
                HttpStatus.UNAUTHORIZED,
                4011L, // TOKEN_EXPIRED
                ex.getMessage());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiError);
    }

    /**
     * Handle token verification errors.
     *
     * @param ex the exception
     * @return 401 Unauthorized response
     */
    @ExceptionHandler(TokenVerificationException.class)
    public ResponseEntity<ApiError> handleTokenVerificationFailed(TokenVerificationException ex) {
        log.warn("Token verification failed: {}", ex.getMessage());

        ApiError apiError = new ApiError(
                HttpStatus.UNAUTHORIZED,
                4012L, // TOKEN_INVALID
                "Invalid token",
                ex);

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiError);
    }

    /**
     * Handle authentication errors (e.g., invalid token from Gateway).
     *
     * @param ex the exception
     * @return 401 Unauthorized response
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiError> handleAuthenticationFailed(AuthenticationException ex) {
        log.warn("Authentication failed: {}", ex.getMessage());

        ApiError apiError = new ApiError(
                HttpStatus.UNAUTHORIZED,
                4013L, // AUTHENTICATION_FAILED
                ex.getMessage());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiError);
    }

    /**
     * Handle permission errors.
     *
     * @param ex the exception
     * @return 403 Forbidden response
     */
    @ExceptionHandler(PermissionException.class)
    public ResponseEntity<ApiError> handlePermissionDenied(PermissionException ex) {
        log.warn("Permission denied: {}", ex.getMessage());

        ApiError apiError = new ApiError(
                HttpStatus.FORBIDDEN,
                4031L, // PERMISSION_REQUIRED
                ex.getMessage());

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(apiError);
    }

    /**
     * Handle role errors.
     *
     * @param ex the exception
     * @return 403 Forbidden response
     */
    @ExceptionHandler(RoleException.class)
    public ResponseEntity<ApiError> handleRoleDenied(RoleException ex) {
        log.warn("Role denied: {}", ex.getMessage());

        ApiError apiError = new ApiError(
                HttpStatus.FORBIDDEN,
                4032L, // ROLE_REQUIRED
                ex.getMessage());

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(apiError);
    }

    /**
     * Handle generic IAM domain exceptions.
     *
     * @param ex the exception
     * @return 500 Internal Server Error response
     */
    @ExceptionHandler(IamDomainException.class)
    public ResponseEntity<ApiError> handleIamDomainException(IamDomainException ex) {
        log.error("Unhandled IAM exception: {}", ex.getMessage(), ex);

        ApiError apiError = new ApiError(
                HttpStatus.INTERNAL_SERVER_ERROR,
                5001L, // IAM_ERROR
                "An authentication/authorization error occurred",
                ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiError);
    }
}

