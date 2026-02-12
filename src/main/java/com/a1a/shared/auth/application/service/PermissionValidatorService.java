package com.a1a.shared.auth.application.service;

import com.a1a.shared.auth.application.port.driving.PermissionLoaderUseCase;
import com.a1a.shared.auth.application.port.driving.PermissionValidatorUseCase;

import lombok.RequiredArgsConstructor;

import java.util.Set;

/**
 * Service for validating user permissions.
 *
 * <p>This service validates permissions by loading them from the Gateway API via
 * PermissionLoaderUseCase. No caching is implemented in this version.
 */
@RequiredArgsConstructor
public class PermissionValidatorService implements PermissionValidatorUseCase {
    private static final org.slf4j.Logger log =
            org.slf4j.LoggerFactory.getLogger(PermissionValidatorService.class);

    private final PermissionLoaderUseCase permissionLoader;

    @Override
    public boolean hasPermission(Long userId, String permissionCode) {
        if (userId == null || permissionCode == null) {
            log.warn("Null userId or permissionCode provided");
            return false;
        }

        Set<String> permissions = permissionLoader.loadPermissions(userId);

        log.debug("User {} has {} permissions: {}", userId, permissions.size(), permissions);

        boolean result = permissions.contains(permissionCode);

        log.debug(
                "Permission check for user {} - permission {}: {}", userId, permissionCode, result);

        return result;
    }

    @Override
    public boolean hasAnyPermission(Long userId, String... permissionCodes) {
        if (userId == null || permissionCodes == null || permissionCodes.length == 0) {
            log.warn("Null or empty parameters provided");
            return false;
        }

        Set<String> permissions = permissionLoader.loadPermissions(userId);

        log.debug("User {} has {} permissions: {}", userId, permissions.size(), permissions);
        log.debug(
                "Checking if user {} has ANY of: {}",
                userId,
                java.util.Arrays.toString(permissionCodes));

        for (String permissionCode : permissionCodes) {
            if (permissions.contains(permissionCode)) {
                log.debug("✓ User {} has permission: {}", userId, permissionCode);
                return true;
            }
        }

        log.warn(
                "✗ User {} does not have any of the required permissions: {}",
                userId,
                java.util.Arrays.toString(permissionCodes));
        return false;
    }

    @Override
    public boolean hasAllPermissions(Long userId, String... permissionCodes) {
        if (userId == null || permissionCodes == null || permissionCodes.length == 0) {
            log.warn("Null or empty parameters provided");
            return false;
        }

        Set<String> permissions = permissionLoader.loadPermissions(userId);

        log.debug("User {} has {} permissions: {}", userId, permissions.size(), permissions);
        log.debug(
                "Checking if user {} has ALL of: {}",
                userId,
                java.util.Arrays.toString(permissionCodes));

        for (String permissionCode : permissionCodes) {
            if (!permissions.contains(permissionCode)) {
                log.warn("✗ User {} is missing permission: {}", userId, permissionCode);
                return false;
            } else {
                log.debug("✓ User {} has permission: {}", userId, permissionCode);
            }
        }

        log.debug("✓ User {} has all {} required permissions", userId, permissionCodes.length);
        return true;
    }
}



