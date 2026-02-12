package com.a1a.shared.auth.infrastructure.adapter;

import com.a1a.shared.auth.application.port.driven.CachePort;
import com.a1a.shared.auth.application.port.driven.JwksPort;

import com.a1a.shared.auth.infrastructure.config.AuthProperties;
import io.netty.channel.ChannelOption;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.Base64;
import java.util.Map;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * Adapter for fetching secret keys from JWKS endpoint.
 *
 * <p>
 * This adapter:
 *
 * <ul>
 * <li>Fetches keys from the configured JWKS URL
 * <li>Caches keys to minimize network calls
 * <li>Supports HS512 algorithm
 * </ul>
 */
@Slf4j
public class JwksAdapter implements JwksPort {
    private static final String CACHE_KEY = "hs512_secret_key";

    private final WebClient webClient;
    private final CachePort<String, SecretKey> keyCache;
    private final AuthProperties authProperties;

    public JwksAdapter(
            WebClient.Builder webClientBuilder,
            CachePort<String, SecretKey> keyCache,
            AuthProperties authProperties) {
        this.keyCache = keyCache;
        this.authProperties = authProperties;

        // Configure timeouts using Reactor Netty HttpClient
        HttpClient httpClient = HttpClient.create()
                .option(
                        ChannelOption.CONNECT_TIMEOUT_MILLIS,
                        (int) authProperties.getJwks().getConnectTimeout().toMillis())
                .responseTimeout(authProperties.getJwks().getReadTimeout());

        this.webClient = webClientBuilder.clientConnector(new ReactorClientHttpConnector(httpClient)).build();
    }

    @Override
    public SecretKey getSecretKey() {
        return keyCache.get(CACHE_KEY)
                .orElseGet(
                        () -> {
                            log.info("Secret key not in cache, fetching from JWKS endpoint");
                            refreshKeys();
                            return keyCache.get(CACHE_KEY)
                                    .orElseThrow(
                                            () -> new RuntimeException(
                                                    "Failed to fetch secret key from JWKS"));
                        });
    }

    @Override
    @SuppressWarnings("unchecked")
    public void refreshKeys() {
        try {
            String jwksUrl = authProperties.getJwksUrl();
            log.info("Fetching keys from JWKS endpoint: {}", jwksUrl);

            // Fetch JWKS response using WebClient
            Map<String, Object> response = webClient
                    .get()
                    .uri(jwksUrl)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block(); // Block to maintain synchronous behavior

            if (response == null) {
                throw new RuntimeException("JWKS endpoint returned null response");
            }

            // Extract secret key
            String secretBase64 = extractSecretKey(response);

            // Decode and create SecretKey
            // Add null check purely for robust type safety, ensuring secretBase64 is
            // treated as NonNull string
            if (secretBase64 == null) {
                throw new RuntimeException("Extracted secret key is null");
            }

            byte[] keyBytes = Base64.getDecoder().decode(secretBase64);
            SecretKey secretKey = new SecretKeySpec(keyBytes, "HmacSHA512");

            // Cache the key
            Duration ttl = authProperties.getCache().getTtl();
            keyCache.put(CACHE_KEY, secretKey, ttl);

            log.info("Secret key cached successfully with TTL: {}", ttl);

        } catch (Exception ex) {
            log.error("Failed to refresh keys from JWKS endpoint", ex);
            throw new RuntimeException("Failed to fetch JWKS", ex);
        }
    }

    /** Extract secret key from JWKS response. */
    @SuppressWarnings("unchecked")
    private String extractSecretKey(Map<String, Object> response) {
        // Example format 1: Direct secret field
        if (response.containsKey("secret")) {
            Object secret = response.get("secret");
            if (secret instanceof String) {
                return (String) secret;
            }
        }

        // Example format 2: Keys array with HS512 key
        if (response.containsKey("keys")) {
            Object keysObj = response.get("keys");
            if (keysObj instanceof java.util.List) {
                var keys = (java.util.List<Map<String, Object>>) keysObj;
                for (Map<String, Object> key : keys) {
                    if ("HS512".equals(key.get("alg"))) {
                        Object k = key.get("k");
                        if (k instanceof String) {
                            return (String) k; // 'k' is the key value in JWK format
                        }
                    }
                }
            }
        }

        throw new RuntimeException(
                "Could not find HS512 secret key in JWKS response. Response: " + response);
    }
}


