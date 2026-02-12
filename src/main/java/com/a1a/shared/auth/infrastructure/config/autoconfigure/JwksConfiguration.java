package com.a1a.shared.auth.infrastructure.config.autoconfigure;

import com.a1a.shared.auth.application.port.driven.CachePort;
import com.a1a.shared.auth.application.port.driven.JwksPort;
import com.a1a.shared.auth.application.port.driving.TokenVerificationUseCase;
import com.a1a.shared.auth.application.service.TokenVerificationService;
import com.a1a.shared.auth.infrastructure.adapter.JwksAdapter;
import com.a1a.shared.auth.infrastructure.adapter.cache.CaffeineAdapter;
import com.a1a.shared.auth.infrastructure.config.AuthProperties;
import com.a1a.shared.auth.infrastructure.security.JwtAuthFilter;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import java.security.interfaces.RSAPublicKey;

/**
 * Auto-configuration for JWT/JWKS token verification.
 *
 * <p>
 * Activated when a1a.auth.jwks.enabled=true (default).
 */
@Configuration
@ConditionalOnClass(name = "com.nimbusds.jose.jwk.JWKSet")
@ConditionalOnProperty(prefix = "a1a.auth.jwks", name = "enabled", havingValue = "true", matchIfMissing = true)
public class JwksConfiguration {

    /**
     * Creates a Caffeine-based cache for storing RSAPublicKey instances.
     *
     * @param properties Auth configuration properties
     * @return CachePort for caching RSAPublicKey objects
     */
    @Bean
    @ConditionalOnMissingBean(name = "publicKeyCache")
    public CachePort<String, RSAPublicKey> publicKeyCache(AuthProperties properties) {
        Cache<String, RSAPublicKey> cache = Caffeine.newBuilder()
                .maximumSize(properties.getCache().getMaxSize())
                .expireAfterWrite(properties.getCache().getTtl())
                .build();

        return new CaffeineAdapter<>(cache);
    }

    /** JWKS adapter for fetching public/public keys for token verification */
    @Bean
    @ConditionalOnMissingBean
    public JwksPort jwksPort(
            WebClient.Builder webClientBuilder,
            CachePort<String, RSAPublicKey> publicKeyCache,
            AuthProperties properties) {
        return new JwksAdapter(webClientBuilder, publicKeyCache, properties);
    }

    /** Token verification service - main use case for JWT authentication */
    @Bean
    @ConditionalOnMissingBean
    public TokenVerificationUseCase tokenVerificationService(
            JwksPort jwksPort, AuthProperties properties) {
        return new TokenVerificationService(jwksPort, properties);
    }

    /** JWT authentication filter for Spring Security */
    @Bean
    @ConditionalOnMissingBean
    public JwtAuthFilter jwtAuthFilter(TokenVerificationUseCase tokenVerificationService) {
        return new JwtAuthFilter(tokenVerificationService);
    }
}

