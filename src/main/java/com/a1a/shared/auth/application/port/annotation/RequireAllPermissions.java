package com.a1a.shared.auth.application.port.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Require ALL of the specified permissions User needs all permissions to access the resource
 *
 * <p>Usage: @RequireAllPermissions({"FAB_PRD_INV_FABRIC_DELETE", "FAB_PRD_INV_FABRIC_TRANSFER"})
 * public ResponseEntity<?> migrateFabricInventory() { ... }
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireAllPermissions {

    /** List of permission codes User needs all of these permissions */
    String[] value();

    /** Optional: Custom error message */
    String errorMessage() default "Access denied: Missing required permissions";
}
