package com.a1a.shared.auth.infrastructure.config.autoconfigure;

import com.a1a.shared.auth.infrastructure.config.AuthProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

/**
 * Main auto-configuration for A1A Authentication & Authorization.
 *
 * <p>
 * Activated when a1a.auth.enabled=true (default) and Spring Security is on
 * classpath.
 *
 * <p>
 * This is the entry point that imports all sub-configurations:
 * <ul>
 * <li>{@link JwksConfiguration} - JWT token verification
 * <li>{@link PermissionConfiguration} - Permission and role validation
 * <li>{@link AspectConfiguration} - AOP aspects for annotations
 * </ul>
 *
 * <p>
 * {@link SecurityAutoConfiguration} and {@link AuditAutoConfiguration} are
 * loaded separately
 * via Spring Boot's auto-configuration mechanism.
 */
@AutoConfiguration
@ConditionalOnClass(name = "org.springframework.security.core.Authentication")
@ConditionalOnProperty(prefix = "a1a.auth", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(AuthProperties.class)
@Import({ JwksConfiguration.class, PermissionConfiguration.class, AspectConfiguration.class })
public class AuthAutoConfiguration {
    // Main auto-configuration entry point
    // Bean definitions are in imported configurations
}

