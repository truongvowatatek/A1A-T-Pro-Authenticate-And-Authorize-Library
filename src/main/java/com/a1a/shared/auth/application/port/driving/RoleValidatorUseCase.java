package com.a1a.shared.auth.application.port.driving;

/**
 * Use case for validating user roles
 *
 * <p>This interface provides methods to check if a user has specific roles. It's designed for
 * programmatic role checking within service/business logic.
 */
public interface RoleValidatorUseCase {

    /**
     * Check if user has a specific role
     *
     * @param userId User account ID
     * @param roleCode Role code to check
     * @return true if user has the role
     */
    boolean hasRole(Long userId, String roleCode);

    /**
     * Check if user has ANY of the specified roles (OR logic)
     *
     * @param userId User account ID
     * @param roleCodes Role codes to check
     * @return true if user has at least one of the roles
     */
    boolean hasAnyRole(Long userId, String... roleCodes);

    /**
     * Check if user has ALL of the specified roles (AND logic)
     *
     * @param userId User account ID
     * @param roleCodes Role codes to check
     * @return true if user has all the roles
     */
    boolean hasAllRoles(Long userId, String... roleCodes);
}



