package com.a1a.shared.auth.infrastructure.config.autoconfigure;

import com.a1a.shared.auth.application.port.driving.GetAuthenticatedUserUseCase;

import com.a1a.shared.auth.infrastructure.config.audit.SecurityAuditAware;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Auto-configuration for JPA Auditing.
 *
 * <p>Activated when a1a.auth.audit.enabled=true (default) and Spring Data JPA is on classpath.
 *
 * <p>Enables automatic population of audit fields (createdBy, modifiedBy) using the current
 * authenticated user.
 */
@AutoConfiguration
@ConditionalOnClass(name = "org.springframework.data.jpa.repository.config.EnableJpaAuditing")
@ConditionalOnProperty(
        prefix = "a1a.auth.config.audit",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true)
@EnableJpaAuditing(auditorAwareRef = "authAuditorAware")
public class AuditAutoConfiguration {

    @Bean(name = "authAuditorAware")
    @ConditionalOnMissingBean(name = "authAuditorAware")
    public AuditorAware<Long> authAuditorAware(GetAuthenticatedUserUseCase getAuthenticatedUser) {
        return new SecurityAuditAware(getAuthenticatedUser);
    }
}
