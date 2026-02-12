package com.a1a.shared.auth.application.service;

import com.a1a.shared.auth.application.port.driving.GetAuthenticatedUserUseCase;
import com.a1a.shared.auth.application.port.driving.RoleValidatorUseCase;
import com.a1a.shared.auth.domain.model.UserContext;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;

/**
 * Service implementation for role validation
 *
 * <p>Validates user roles by checking the UserContext.roles field. Roles are already loaded from
 * JWT token, so no external call or caching is needed.
 */
@Slf4j
@RequiredArgsConstructor
public class RoleValidatorService implements RoleValidatorUseCase {

    private final GetAuthenticatedUserUseCase getAuthenticatedUserUseCase;

    @Override
    public boolean hasRole(Long userId, String roleCode) {
        UserContext user = getAuthenticatedUserUseCase.getCurrentUser();

        // Verify userId matches
        if (!user.getAccountId().equals(userId)) {
            log.warn(
                    "Role check failed: userId mismatch. Expected {}, got {}",
                    user.getAccountId(),
                    userId);
            return false;
        }

        List<String> userRoles = user.getRoles();
        boolean hasRole = userRoles != null && userRoles.contains(roleCode);

        log.debug(
                "Role check: user {} {} role {}. User roles: {}",
                userId,
                hasRole ? "HAS" : "LACKS",
                roleCode,
                userRoles);

        return hasRole;
    }

    @Override
    public boolean hasAnyRole(Long userId, String... roleCodes) {
        UserContext user = getAuthenticatedUserUseCase.getCurrentUser();

        // Verify userId matches
        if (!user.getAccountId().equals(userId)) {
            log.warn(
                    "Role check failed: userId mismatch. Expected {}, got {}",
                    user.getAccountId(),
                    userId);
            return false;
        }

        List<String> userRoles = user.getRoles();
        boolean hasAnyRole =
                userRoles != null && Arrays.stream(roleCodes).anyMatch(userRoles::contains);

        log.debug(
                "Role check (ANY): user {} {} any of roles {}. User roles: {}",
                userId,
                hasAnyRole ? "HAS" : "LACKS",
                Arrays.toString(roleCodes),
                userRoles);

        return hasAnyRole;
    }

    @Override
    public boolean hasAllRoles(Long userId, String... roleCodes) {
        UserContext user = getAuthenticatedUserUseCase.getCurrentUser();

        // Verify userId matches
        if (!user.getAccountId().equals(userId)) {
            log.warn(
                    "Role check failed: userId mismatch. Expected {}, got {}",
                    user.getAccountId(),
                    userId);
            return false;
        }

        List<String> userRoles = user.getRoles();
        boolean hasAllRoles =
                userRoles != null && Arrays.stream(roleCodes).allMatch(userRoles::contains);

        log.debug(
                "Role check (ALL): user {} {} all roles {}. User roles: {}",
                userId,
                hasAllRoles ? "HAS" : "LACKS",
                Arrays.toString(roleCodes),
                userRoles);

        return hasAllRoles;
    }
}



