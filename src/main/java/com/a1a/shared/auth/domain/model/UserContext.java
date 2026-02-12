package com.a1a.shared.auth.domain.model;

import lombok.Builder;
import lombok.Value;

import java.util.List;

/**
 * Domain model representing the authenticated user context.
 *
 * <p>This model is extracted from the access_token provided by the Auth Service. It contains user
 * information and roles for authorization purposes.
 *
 * <p>Note: This is a pure domain model with no framework dependencies.
 */
@Value
@Builder
public class UserContext {
    /** Account ID from the auth system */
    Long accountId;

    /** Username for login */
    String username;

    /** Full name of the user */
    String fullName;

    /** Raw JWT token for propagating to downstream services */
    String rawToken;

    /** Employee code (nullable) */
    String employeeCode;

    /** Full employee code (nullable) */
    String employeeFullCode;

    /** Whether this is the user's first login */
    boolean firstLogin;

    /**
     * Roles assigned to the user.
     *
     * <p>Mapped from the "groups" field in the access_token (groups.groupCode)
     */
    @Builder.Default List<String> roles = List.of();

    /**
     * Checks if the user has a specific role.
     *
     * @param role the role code to check (e.g., "ACC_LEADER")
     * @return true if the user has the role
     */
    public boolean hasRole(String role) {
        return roles != null && roles.contains(role);
    }

    /**
     * Checks if the user has ANY of the specified roles (OR operation).
     *
     * @param rolesToCheck the roles to check
     * @return true if the user has at least one of the roles
     */
    public boolean hasAnyRole(String... rolesToCheck) {
        if (roles == null) {
            return false;
        }
        for (String role : rolesToCheck) {
            if (roles.contains(role)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the user has ALL of the specified roles (AND operation).
     *
     * @param rolesToCheck the roles to check
     * @return true if the user has all of the roles
     */
    public boolean hasAllRoles(String... rolesToCheck) {
        if (roles == null) {
            return false;
        }
        for (String role : rolesToCheck) {
            if (!roles.contains(role)) {
                return false;
            }
        }
        return true;
    }
}



