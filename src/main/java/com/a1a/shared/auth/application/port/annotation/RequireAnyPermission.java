package com.a1a.shared.auth.application.port.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Require ANY of the specified permissions User needs at least one permission to access the
 * resource
 *
 * <p>Usage: @RequireAnyPermission({"FAB_PRD_INV_FABRIC_VIEW", "FAB_PRD_INV_FABRIC_EXPORT"}) public
 * ResponseEntity<?> getFabricInventory() { ... }
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireAnyPermission {

    /** List of permission codes User needs at least one of these permissions */
    String[] value();

    /** Optional: Custom error message */
    String errorMessage() default "Access denied: None of the required permissions found";
}

