package com.a1a.shared.auth.infrastructure.config;

import jakarta.validation.constraints.NotBlank;

import lombok.Data;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;
import java.util.List;

/**
 * Configuration properties for IAM domain.
 *
 * <p>Maps to the 'iam' prefix in application.yml files.
 */
@ConfigurationProperties(prefix = "app.auth")
@Data
@Validated
public class AuthProperties {

    private JwksConfig jwks = new JwksConfig();
    private PermissionConfig permission = new PermissionConfig();
    private CacheConfig cache = new CacheConfig();
    private ValidationConfig validation = new ValidationConfig();
    private CorsConfig cors = new CorsConfig();
    private SecurityConfig security = new SecurityConfig();

    // Convenience methods
    public String getJwksUrl() {
        return jwks.getUrl();
    }

    /** JWKS endpoint configuration */
    @Data
    public static class JwksConfig {
        /** Enable JWKS verification. Set to false for local dev without Auth Service. */
        private boolean enabled;

        @NotBlank(message = "iam.jwks.url is required")
        private String url;

        private Duration connectTimeout;
        private Duration readTimeout;
    }

    @Data
    public static class PermissionConfig {
        @NotBlank(message = "iam.permission.url is required")
        private String url;

        private Duration connectTimeout;
        private Duration readTimeout;
    }

    /** Cache configuration */
    @Data
    public static class CacheConfig {
        /** Cache type: caffeine (in-memory) or redis (distributed) */
        private String type;

        /** Time-to-live for cached keys */
        private Duration ttl;

        /** Maximum cache size (for Caffeine only) */
        private int maxSize = 100;
    }

    /** Token validation configuration */
    @Data
    public static class ValidationConfig {
        /** Whether to validate token expiration */
        private boolean validateExpiration;

        /** Clock skew tolerance for exp/iat validation */
        private Duration clockSkew;
    }

    /** CORS configuration */
    @Data
    public static class CorsConfig {
        /** Enable CORS */
        private boolean enabled;

        /** Allowed origins (default: *) */
        private List<String> allowedOrigins;

        /** Allowed methods (default: GET, POST, PUT, DELETE, OPTIONS) */
        private List<String> allowedMethods;

        /** Allowed headers (default: *) */
        private List<String> allowedHeaders;

        /** Allow credentials */
        private boolean allowCredentials;

        /** Max age */
        private Duration maxAge;
    }

    /** Security configuration */
    @Data
    public static class SecurityConfig {
        /** List of URL patterns that bypass authentication */
        private List<String> whiteListUrls;
    }
}
