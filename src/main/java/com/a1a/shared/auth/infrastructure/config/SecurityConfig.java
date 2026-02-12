package com.a1a.shared.auth.infrastructure.config;

import com.a1a.shared.auth.infrastructure.security.JwtAuthFilter;

import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final AuthProperties AuthProperties;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Get white-list URLs from IAM properties
        List<String> whiteListUrls = AuthProperties.getSecurity().getWhiteListUrls();
        String[] whiteList = whiteListUrls.toArray(new String[0]);

        http
                // Disable CSRF using the new lambda DSL
                .csrf(AbstractHttpConfigurer::disable)

                // CORS Configuration
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

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
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        var corsProps = AuthProperties.getCors();
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
