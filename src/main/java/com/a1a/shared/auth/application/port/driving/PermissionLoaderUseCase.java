package com.a1a.shared.auth.application.port.driving;

import java.util.Set;

/**
 * SPI for loading user permissions Each microservice implements this to fetch permissions from
 * their data source
 */
public interface PermissionLoaderUseCase {

    /**
     * Load permissions for a specific userPermissionLoaderUseCase
     *
     * @param userId User identifier
     * @return Set of permission codes the user has
     */
    Set<String> loadPermissions(Long userId);

    /**
     * Invalidate cached permissions for a user Called when permissions are updated
     *
     * @param userId User identifier
     */
    void invalidate(Long userId);

    /**
     * Refresh permissions for a user from source
     *
     * @param userId User identifier
     * @return Updated set of permission codes
     */
    default Set<String> refresh(Long userId) {
        invalidate(userId);
        return loadPermissions(userId);
    }
}
