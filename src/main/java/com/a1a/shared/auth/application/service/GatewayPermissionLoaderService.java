package com.a1a.shared.auth.application.service;

import com.a1a.shared.auth.application.port.driving.GatewayPermissionClientUseCase;
import com.a1a.shared.auth.application.port.driving.GetAuthenticatedUserUseCase;
import com.a1a.shared.auth.application.port.driving.PermissionLoaderUseCase;
import com.a1a.shared.auth.domain.exception.AuthenticationException;
import com.a1a.shared.auth.domain.model.UserContext;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.Set;

/**
 * Production PermissionLoader that fetches permissions from Gateway API
 *
 * <p>
 * To use, set property: a1a.security.permission.loader=gateway (default)
 */
@Slf4j
@RequiredArgsConstructor
public class GatewayPermissionLoaderService implements PermissionLoaderUseCase {

    private final GatewayPermissionClientUseCase gatewayPermissionClientUseCase;
    private final GetAuthenticatedUserUseCase getAuthenticatedUserUseCase;

    @Override
    public Set<String> loadPermissions(Long userId) {
        try {
            log.debug("Loading permissions from Gateway API for userId: {}", userId);

            // Get access token from current user context
            UserContext currentUser = getAuthenticatedUserUseCase.getCurrentUser();
            if (currentUser == null || currentUser.getRawToken() == null) {
                log.warn("No authenticated user or token found for userId: {}", userId);
                return Collections.emptySet();
            }

            String accessToken = currentUser.getRawToken();
            Set<String> permissions = gatewayPermissionClientUseCase.fetchUserPermissions(accessToken);

            log.info(
                    "Successfully loaded {} permissions for userId: {}",
                    permissions.size(),
                    userId);
            log.debug("Permissions for userId {}: {}", userId, permissions);

            return permissions;

        } catch (AuthenticationException e) {
            // Rethrow authentication errors (401) - these should not be degraded
            log.error("Authentication failed for userId {}: {}", userId, e.getMessage());
            throw e;

        } catch (Exception e) {
            // For other errors, degrade gracefully
            log.error("Failed to load permissions for userId {}: {}", userId, e.getMessage(), e);
            return Collections.emptySet();
        }
    }

    @Override
    public void invalidate(Long userId) {
        log.debug("Invalidating permissions for userId: {} (cache invalidation)", userId);
        // Cache invalidation is handled by PermissionCacheService
        // This method is just a notification
    }
}



