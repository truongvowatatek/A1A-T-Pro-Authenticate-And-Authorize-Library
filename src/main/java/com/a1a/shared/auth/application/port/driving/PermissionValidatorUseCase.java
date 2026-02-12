package com.a1a.shared.auth.application.port.driving;

/** SPI for validating user permissions Coordinates between cache and loader */
public interface PermissionValidatorUseCase {

    /**
     * Check if user has a specific permission
     *
     * @param userId User identifier
     * @param permissionCode Permission code to check
     * @return true if user has the permission
     */
    boolean hasPermission(Long userId, String permissionCode);

    /**
     * Check if user has any of the specified permissions
     *
     * @param userId User identifier
     * @param permissionCodes Array of permission codes
     * @return true if user has at least one permission
     */
    boolean hasAnyPermission(Long userId, String... permissionCodes);

    /**
     * Check if user has all of the specified permissions
     *
     * @param userId User identifier
     * @param permissionCodes Array of permission codes
     * @return true if user has all permissions
     */
    boolean hasAllPermissions(Long userId, String... permissionCodes);
}



