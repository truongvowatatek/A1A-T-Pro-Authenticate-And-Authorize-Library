package com.a1a.shared.auth.application.port.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom annotation to declare permission requirement on controller methods
 *
 * <p>Usage: @RequirePermission("FAB_PRD_DBD_INBOUND_VIEW") public ResponseEntity<?> getInbound() {
 * ... }
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequirePermission {

    /** Permission code string Should use constants from permission enums */
    String value();

    /** Optional: Custom error message when permission check fails */
    String errorMessage() default "Access denied: Insufficient permissions";
}