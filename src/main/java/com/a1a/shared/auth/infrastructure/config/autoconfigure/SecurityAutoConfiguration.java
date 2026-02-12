package com.a1a.shared.auth.infrastructure.config.autoconfigure;

import com.a1a.shared.auth.infrastructure.config.AuthProperties;
import com.a1a.shared.auth.infrastructure.security.JwtAuthFilter;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Auto-configuration for Spring Security.
 *
 * <p>Activated when a1a.auth.security.enabled=true (default) and Spring Security is on classpath.
 *
 * <p>Configures:
 *
 * <ul>
 *   <li>SecurityFilterChain with JWT authentication
 *   <li>CORS configuration
 *   <li>Session management (stateless)
 *   <li>White-list URLs
 * </ul>
 */
@AutoConfiguration(after = AuthAutoConfiguration.class)
@ConditionalOnClass(SecurityFilterChain.class)
@ConditionalOnProperty(
        prefix = "a1a.auth.security",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true)
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public SecurityFilterChain authSecurityFilterChain(
            HttpSecurity http, JwtAuthFilter jwtAuthFilter, AuthProperties authProperties)
            throws Exception {

        // Get white-list URLs from Auth properties
        List<String> whiteListUrls = authProperties.getSecurity().getWhiteListUrls();
        String[] whiteList =
                whiteListUrls != null ? whiteListUrls.toArray(new String[0]) : new String[0];

        http
                // Disable CSRF using the new lambda DSL
                .csrf(AbstractHttpConfigurer::disable)

                // CORS Configuration
                .cors(cors -> cors.configurationSource(corsConfigurationSource(authProperties)))

                // Stateless session management
                .sessionManagement(
                        session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Public endpoints
                .authorizeHttpRequests(
                        auth ->
                                auth.requestMatchers(whiteList)
                                        .permitAll()
                                        // All other requests require authentication
                                        .anyRequest()
                                        .authenticated())

                // Add JWT filter before UsernamePasswordAuthenticationFilter
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /** CORS Configuration Source. Reads values from application properties via AuthProperties. */
    @Bean
    @ConditionalOnMissingBean
    public CorsConfigurationSource corsConfigurationSource(AuthProperties authProperties) {
        CorsConfiguration configuration = new CorsConfiguration();

        var corsProps = authProperties.getCors();
        if (corsProps.isEnabled()) {
            configuration.setAllowedOrigins(corsProps.getAllowedOrigins());
            configuration.setAllowedMethods(corsProps.getAllowedMethods());
            configuration.setAllowedHeaders(corsProps.getAllowedHeaders());
            configuration.setAllowCredentials(corsProps.isAllowCredentials());
            if (corsProps.getMaxAge() != null) {
                configuration.setMaxAge(corsProps.getMaxAge().toSeconds());
            }
        }

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
