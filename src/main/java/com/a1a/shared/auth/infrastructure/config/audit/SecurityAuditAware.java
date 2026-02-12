package com.a1a.shared.auth.infrastructure.config.audit;

import com.a1a.shared.auth.application.port.driving.GetAuthenticatedUserUseCase;
import com.a1a.shared.auth.domain.model.UserContext;

import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component("securityAuditAware")
public class SecurityAuditAware implements AuditorAware<Long> {

    private final GetAuthenticatedUserUseCase getAuthenticatedUserUseCase;

    public SecurityAuditAware(GetAuthenticatedUserUseCase getAuthenticatedUserUseCase) {
        this.getAuthenticatedUserUseCase = getAuthenticatedUserUseCase;
    }

    @Override
    public Optional<Long> getCurrentAuditor() {
        UserContext userContext = getAuthenticatedUserUseCase.getCurrentUser();

        if (!getAuthenticatedUserUseCase.isAuthenticated()) {
            return Optional.empty();
        }

        return Optional.of(userContext.getAccountId());
    }
}

