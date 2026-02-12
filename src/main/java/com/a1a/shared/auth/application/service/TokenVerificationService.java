package com.a1a.shared.auth.application.service;

import com.a1a.shared.auth.application.port.driven.JwksPort;
import com.a1a.shared.auth.application.port.driving.TokenVerificationUseCase;
import com.a1a.shared.auth.domain.exception.TokenExpiredException;
import com.a1a.shared.auth.domain.exception.TokenVerificationException;
import com.a1a.shared.auth.domain.model.UserContext;
import com.a1a.shared.auth.infrastructure.config.AuthProperties;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

/**
 * Service for verifying and extracting user information from JWT tokens.
 *
 * <p>This service:
 *
 * <ul>
 *   <li>Parses JWT tokens
 *   <li>Verifies signatures using HS512 algorithm
 *   <li>Validates expiration
 *   <li>Maps claims to UserContext domain model
 * </ul>
 */
@RequiredArgsConstructor
@Slf4j
public class TokenVerificationService implements TokenVerificationUseCase {
    private final JwksPort jwksPort;
    private final AuthProperties AuthProperties;

    /**
     * Verify JWT token and extract UserContext.
     *
     * @param token JWT token string (without "Bearer " prefix)
     * @return UserContext extracted from token
     * @throws TokenVerificationException if verification fails
     * @throws TokenExpiredException if token is expired
     */
    @Override
    public UserContext verifyAndExtract(String token) {
        try {
            // Parse JWT
            SignedJWT signedJWT = SignedJWT.parse(token);

            // Verify signature
            verifySignature(signedJWT);

            // Extract claims
            JWTClaimsSet claims = signedJWT.getJWTClaimsSet();

            // Validate expiration
            validateExpiration(claims);

            // Map to UserContext
            return mapToUserContext(claims, token);

        } catch (TokenVerificationException | TokenExpiredException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Token verification failed", ex);
            throw new TokenVerificationException("Failed to verify token", ex);
        }
    }

    private void verifySignature(SignedJWT signedJWT) {
        if (!AuthProperties.getJwks().isEnabled()) {
            log.warn(
                    "JWKS verification is DISABLED. Skipping signature check. DO NOT USE IN PRODUCTION!");
            return;
        }

        try {
            SecretKey secretKey = jwksPort.getSecretKey();
            MACVerifier verifier = new MACVerifier(secretKey);

            if (!signedJWT.verify(verifier)) {
                throw new TokenVerificationException("Invalid token signature");
            }

            log.debug("Token signature verified successfully");

        } catch (JOSEException ex) {
            throw new TokenVerificationException("Failed to verify token signature", ex);
        }
    }

    private void validateExpiration(JWTClaimsSet claims) {
        if (!AuthProperties.getValidation().isValidateExpiration()) {
            return;
        }

        Date expirationTime = claims.getExpirationTime();
        if (expirationTime == null) {
            throw new TokenVerificationException("Token does not have expiration time");
        }

        Instant now = Instant.now();
        Instant expiration = expirationTime.toInstant();
        long clockSkewSeconds = AuthProperties.getValidation().getClockSkew().getSeconds();

        if (now.isAfter(expiration.plusSeconds(clockSkewSeconds))) {
            throw new TokenExpiredException(
                    "Token expired at " + expirationTime + ", current time: " + now);
        }

        log.debug("Token expiration validated successfully");
    }

    @SuppressWarnings("unchecked")
    private UserContext mapToUserContext(JWTClaimsSet claims, String token) {
        try {
            // Extract account object
            Map<String, Object> account = claims.getJSONObjectClaim("account");
            if (account == null) {
                throw new TokenVerificationException("Token does not contain 'account' claim");
            }

            // Extract groups and map to roles
            List<Map<String, Object>> groups = (List<Map<String, Object>>) account.get("groups");
            List<String> roles = List.of();
            if (groups != null) {
                roles =
                        groups.stream()
                                .map(group -> (String) group.get("groupCode"))
                                .filter(Objects::nonNull)
                                .collect(Collectors.toList());
            }

            // Build UserContext
            return UserContext.builder()
                    .accountId((Long) account.get("id"))
                    .username((String) account.get("username"))
                    .fullName((String) account.get("fullName"))
                    .employeeCode((String) account.get("employeeCode"))
                    .employeeFullCode((String) account.get("employeeFullCode"))
                    .firstLogin(Boolean.TRUE.equals(account.get("firstLogin")))
                    .rawToken(token)
                    .roles(roles)
                    .build();

        } catch (Exception ex) {
            log.error("Failed to map token claims to UserContext", ex);
            throw new TokenVerificationException("Failed to extract user from token", ex);
        }
    }
}



