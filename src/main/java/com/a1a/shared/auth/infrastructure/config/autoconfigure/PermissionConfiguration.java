package com.a1a.shared.auth.infrastructure.config.autoconfigure;

import com.a1a.shared.auth.application.port.driving.GatewayPermissionClientUseCase;
import com.a1a.shared.auth.application.port.driving.GetAuthenticatedUserUseCase;
import com.a1a.shared.auth.application.port.driving.PermissionLoaderUseCase;
import com.a1a.shared.auth.application.port.driving.PermissionValidatorUseCase;
import com.a1a.shared.auth.application.port.driving.RoleValidatorUseCase;
import com.a1a.shared.auth.application.service.GatewayPermissionClientService;
import com.a1a.shared.auth.application.service.GatewayPermissionLoaderService;
import com.a1a.shared.auth.application.service.GetAuthenticatedUserService;
import com.a1a.shared.auth.application.service.PermissionValidatorService;
import com.a1a.shared.auth.application.service.RoleValidatorService;

import com.a1a.shared.auth.infrastructure.config.AuthProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Auto-configuration for permission and role validation.
 *
 * <p>
 * Activated when a1a.auth.permission.enabled=true (default).
 */
@Configuration
@ConditionalOnProperty(prefix = "a1a.auth.permission", name = "enabled", havingValue = "true", matchIfMissing = true)
public class PermissionConfiguration {

    /**
     * Get authenticated user service - retrieves current user from security context
     */
    @Bean
    @ConditionalOnMissingBean
    public GetAuthenticatedUserUseCase getAuthenticatedUser() {
        return new GetAuthenticatedUserService();
    }

    /** Gateway permission client - fetches permissions from external API */
    @Bean
    @ConditionalOnMissingBean
    public GatewayPermissionClientUseCase gatewayPermissionClient(
            WebClient.Builder webClientBuilder, AuthProperties properties) {
        return new GatewayPermissionClientService(webClientBuilder, properties);
    }

    /** Permission loader - loads permissions for users */
    @Bean
    @ConditionalOnMissingBean
    public PermissionLoaderUseCase permissionLoader(
            GatewayPermissionClientUseCase gatewayPermissionClient,
            GetAuthenticatedUserUseCase getAuthenticatedUser) {
        return new GatewayPermissionLoaderService(gatewayPermissionClient, getAuthenticatedUser);
    }

    /** Permission validator - validates user permissions */
    @Bean
    @ConditionalOnMissingBean
    public PermissionValidatorUseCase permissionValidator(
            PermissionLoaderUseCase permissionLoader) {
        return new PermissionValidatorService(permissionLoader);
    }

    /** Role validator - validates user roles */
    @Bean
    @ConditionalOnMissingBean
    public RoleValidatorUseCase roleValidator(GetAuthenticatedUserUseCase getAuthenticatedUser) {
        return new RoleValidatorService(getAuthenticatedUser);
    }
}

