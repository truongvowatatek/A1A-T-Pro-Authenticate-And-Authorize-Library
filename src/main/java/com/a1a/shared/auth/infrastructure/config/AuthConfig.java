package com.a1a.shared.auth.infrastructure.config;

import com.a1a.shared.auth.application.port.driven.CachePort;
import com.a1a.shared.auth.application.port.driven.JwksPort;
import com.a1a.shared.auth.application.port.driving.GatewayPermissionClientUseCase;
import com.a1a.shared.auth.application.port.driving.GetAuthenticatedUserUseCase;
import com.a1a.shared.auth.application.port.driving.PermissionLoaderUseCase;
import com.a1a.shared.auth.application.port.driving.PermissionValidatorUseCase;
import com.a1a.shared.auth.application.port.driving.RoleValidatorUseCase;
import com.a1a.shared.auth.application.port.driving.TokenVerificationUseCase;
import com.a1a.shared.auth.application.service.GatewayPermissionClientService;
import com.a1a.shared.auth.application.service.GatewayPermissionLoaderService;
import com.a1a.shared.auth.application.service.GetAuthenticatedUserService;
import com.a1a.shared.auth.application.service.PermissionValidatorService;
import com.a1a.shared.auth.application.service.RoleValidatorService;
import com.a1a.shared.auth.application.service.TokenVerificationService;
import com.a1a.shared.auth.infrastructure.adapter.JwksAdapter;
import com.a1a.shared.auth.infrastructure.adapter.cache.CaffeineAdapter;
import com.a1a.shared.auth.infrastructure.aspect.PermissionCheckAspect;
import com.a1a.shared.auth.infrastructure.aspect.RoleCheckAspect;
import com.a1a.shared.auth.infrastructure.security.JwtAuthFilter;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import javax.crypto.SecretKey;
import java.security.interfaces.RSAPublicKey;

/**
 * Configuration class for IAM domain.
 *
 * <p>Enables configuration properties binding for AuthProperties and creates beans for all IAM
 * components.
 */
@Configuration
@EnableConfigurationProperties(AuthProperties.class)
public class AuthConfig {

    @Bean
    @ConditionalOnMissingBean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

    /**
     * Creates a Caffeine-based cache for storing SecretKey instances.
     *
     * @param properties IAM configuration properties
     * @return CachePort for caching publicKey objects
     */
    @Bean
    public CachePort<String, RSAPublicKey> publicKeyCache(AuthProperties properties) {
        Cache<String, RSAPublicKey> cache =
                Caffeine.newBuilder()
                        .maximumSize(properties.getCache().getMaxSize())
                        .expireAfterWrite(properties.getCache().getTtl())
                        .build();

        return new CaffeineAdapter<>(cache);
    }

    /** JWKS adapter for fetching public/public keys for token verification */
    @Bean
    public JwksPort jwksPort(
            WebClient.Builder webClientBuilder,
            CachePort<String, RSAPublicKey> publicKeyCache,
            AuthProperties properties) {
        return new JwksAdapter(webClientBuilder, publicKeyCache, properties);
    }

    /** Token verification service - main use case for JWT authentication */
    @Bean
    public TokenVerificationUseCase tokenVerificationService(
            JwksPort jwksPort, AuthProperties properties) {
        return new TokenVerificationService(jwksPort, properties);
    }

    /** Gateway permission client - fetches permissions from external API */
    @Bean
    public GatewayPermissionClientUseCase gatewayPermissionClient(
            WebClient.Builder webClientBuilder, AuthProperties properties) {
        return new GatewayPermissionClientService(webClientBuilder, properties);
    }

    /** Permission loader - loads permissions for users */
    @Bean
    public PermissionLoaderUseCase permissionLoader(
            GatewayPermissionClientUseCase gatewayPermissionClient,
            GetAuthenticatedUserUseCase getAuthenticatedUser) {
        return new GatewayPermissionLoaderService(gatewayPermissionClient, getAuthenticatedUser);
    }

    /** Permission validator - validates user permissions */
    @Bean
    public PermissionValidatorUseCase permissionValidator(
            PermissionLoaderUseCase permissionLoader) {
        return new PermissionValidatorService(permissionLoader);
    }

    /** Role validator - validates user roles */
    @Bean
    public RoleValidatorUseCase roleValidator(GetAuthenticatedUserUseCase getAuthenticatedUser) {
        return new RoleValidatorService(getAuthenticatedUser);
    }

    /** Get authenticated user service - retrieves current user from security context */
    @Bean
    public GetAuthenticatedUserUseCase getAuthenticatedUser() {
        return new GetAuthenticatedUserService();
    }

    /** JWT authentication filter for Spring Security */
    @Bean
    public JwtAuthFilter jwtAuthFilter(TokenVerificationUseCase tokenVerificationService) {
        return new JwtAuthFilter(tokenVerificationService);
    }

    /** Permission check aspect for @RequirePermission annotations */
    @Bean
    public PermissionCheckAspect permissionCheckAspect(
            GetAuthenticatedUserUseCase getAuthenticatedUser,
            PermissionValidatorUseCase permissionValidator) {
        return new PermissionCheckAspect(getAuthenticatedUser, permissionValidator);
    }

    /** Role check aspect for @RequireRole annotations */
    @Bean
    public RoleCheckAspect roleCheckAspect(GetAuthenticatedUserUseCase getAuthenticatedUser) {
        return new RoleCheckAspect(getAuthenticatedUser);
    }
}
