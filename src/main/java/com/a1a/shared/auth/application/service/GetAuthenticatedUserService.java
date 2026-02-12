package com.a1a.shared.auth.application.service;

import com.a1a.shared.auth.application.port.driving.GetAuthenticatedUserUseCase;
import com.a1a.shared.auth.domain.model.UserContext;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Implementation of GetAuthenticatedUserUseCase driving port.
 *
 * <p>
 * This service provides access to the current authenticated user from Spring
 * Security context.
 */
public class GetAuthenticatedUserService implements GetAuthenticatedUserUseCase {

    @Override
    public UserContext getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null
                && authentication.isAuthenticated()
                && authentication.getPrincipal() instanceof UserContext) {
            return (UserContext) authentication.getPrincipal();
        }

        return null;
    }

    @Override
    public boolean isAuthenticated() {
        return getCurrentUser() != null;
    }
}



