package com.a1a.shared.auth.application.port.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Require ANY of the specified roles. User needs at least one role to access the resource
 *
 * <p>Usage: @RequireAnyRole({"ACC_MGR", "FAB_MGR"}) public ResponseEntity<?>
 * getInventoryDashboard() { ... }
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireAnyRole {

    /** List of role codes. User needs at least one of these roles */
    String[] value();

    /** Optional: Custom error message */
    String errorMessage() default "Access denied: None of the required roles found";
}
