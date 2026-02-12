package com.a1a.shared.auth.application.port.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Require ALL of the specified roles. User needs all roles to access the resource
 *
 * <p>Usage: @RequireAllRoles({"ACC_MGR", "ADMIN"}) public ResponseEntity<?> deleteAllInventory() {
 * ... }
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireAllRoles {

    /** List of role codes. User needs all of these roles */
    String[] value();

    /** Optional: Custom error message */
    String errorMessage() default "Access denied: Missing required roles";
}



