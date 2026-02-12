package com.a1a.shared.auth.infrastructure.security;

import com.a1a.shared.auth.application.port.driving.TokenVerificationUseCase;
import com.a1a.shared.auth.domain.exception.TokenExpiredException;
import com.a1a.shared.auth.domain.exception.TokenVerificationException;
import com.a1a.shared.auth.domain.model.UserContext;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * JWT authentication filter for Spring Security.
 *
 * <p>This filter:
 *
 * <ul>
 *   <li>Extracts JWT token from Authorization header
 *   <li>Verifies token signature and expiration
 *   <li>Extracts UserContext and stores in thread-local
 *   <li>Returns 401 Unauthorized on authentication errors
 * </ul>
 */
@RequiredArgsConstructor
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {
    private final TokenVerificationUseCase tokenVerificationService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        // Extract token from Authorization header
        String authHeader = request.getHeader("Authorization");
        log.debug(
                "JwtAuthFilter processing request: {} {}, Header: {}",
                request.getMethod(),
                request.getRequestURI(),
                authHeader != null ? "Present" : "Missing");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            try {
                // Verify token and extract user
                UserContext user = tokenVerificationService.verifyAndExtract(token);

                // Set Spring Security Context
                List<SimpleGrantedAuthority> authorities =
                        user.getRoles().stream().map(SimpleGrantedAuthority::new).toList();

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(user, null, authorities);

                SecurityContextHolder.getContext().setAuthentication(authentication);

                log.debug("User authenticated: {}", user.getUsername());

            } catch (TokenExpiredException ex) {
                log.warn("Token expired: {}", ex.getMessage());
                handleAuthenticationError(response, "Token expired");
                return;
            } catch (TokenVerificationException ex) {
                log.warn("Token verification failed: {}", ex.getMessage());
                handleAuthenticationError(response, "Invalid token");
                return;
            }
        }

        // Continue filter chain
        filterChain.doFilter(request, response);
    }

    private void handleAuthenticationError(HttpServletResponse response, String message)
            throws IOException {
        response.setStatus(401);
        response.setContentType("application/json");
        response.getWriter().write(String.format("{\"error\": \"%s\"}", message));
    }
}

