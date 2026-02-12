package com.a1a.shared.auth.application.port.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom annotation to declare role requirement on controller methods
 *
 * <p>Usage: @RequireRole("ACC_MGR") public ResponseEntity<?> getAccessoryDashboard() { ... }
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireRole {

    /** Role code string. Should use role codes from JWT groups (e.g., ACC_MGR, FAB_MGR) */
    String value();

    /** Optional: Custom error message when role check fails */
    String errorMessage() default "Access denied: Insufficient role";
}
