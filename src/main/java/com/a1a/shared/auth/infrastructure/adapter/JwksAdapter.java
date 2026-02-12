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

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.time.Duration;
import java.util.Base64;
import java.util.Map;

/**
 * Adapter for fetching RSA public keys from JWKS endpoint.
 *
 * <p>
 * This adapter:
 *
 * <ul>
 * <li>Fetches keys from the configured JWKS URL
 * <li>Caches keys to minimize network calls
 * <li>Supports RS256 algorithm (RSA with SHA-256)
 * </ul>
 */
@Slf4j
public class JwksAdapter implements JwksPort {
    private static final String CACHE_KEY = "rsa_public_key";

    private final WebClient webClient;
    private final CachePort<String, RSAPublicKey> keyCache;
    private final AuthProperties authProperties;

    public JwksAdapter(
            WebClient.Builder webClientBuilder,
            CachePort<String, RSAPublicKey> keyCache,
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
    public RSAPublicKey getPublicKey() {
        return keyCache.get(CACHE_KEY)
                .orElseGet(
                        () -> {
                            log.info("RSA public key not in cache, fetching from JWKS endpoint");
                            refreshKeys();
                            return keyCache.get(CACHE_KEY)
                                    .orElseThrow(
                                            () -> new RuntimeException(
                                                    "Failed to fetch RSA public key from JWKS"));
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

            // Extract RSA public key
            RSAPublicKey publicKey = extractRSAPublicKey(response);

            // Cache the key
            Duration ttl = authProperties.getCache().getTtl();
            keyCache.put(CACHE_KEY, publicKey, ttl);

            log.info("RSA public key cached successfully with TTL: {}", ttl);

        } catch (Exception ex) {
            log.error("Failed to refresh keys from JWKS endpoint", ex);
            throw new RuntimeException("Failed to fetch JWKS", ex);
        }
    }

    /** Extract RSA public key from JWKS response. */
    @SuppressWarnings("unchecked")
    private RSAPublicKey extractRSAPublicKey(Map<String, Object> response) {
        try {
            // JWKS format: { "keys": [ { "kty": "RSA", "alg": "RS256", "n": "...", "e": "..." } ] }
            if (!response.containsKey("keys")) {
                throw new RuntimeException("JWKS response does not contain 'keys' array");
            }

            Object keysObj = response.get("keys");
            if (!(keysObj instanceof java.util.List)) {
                throw new RuntimeException("JWKS 'keys' is not an array");
            }

            var keys = (java.util.List<Map<String, Object>>) keysObj;
            if (keys.isEmpty()) {
                throw new RuntimeException("JWKS 'keys' array is empty");
            }

            // Find RSA key (typically RS256)
            for (Map<String, Object> key : keys) {
                String kty = (String) key.get("kty");
                String alg = (String) key.get("alg");

                // Look for RSA keys with RS256 algorithm
                if ("RSA".equals(kty) && (alg == null || alg.startsWith("RS"))) {
                    String nBase64 = (String) key.get("n"); // Modulus
                    String eBase64 = (String) key.get("e"); // Exponent

                    if (nBase64 == null || eBase64 == null) {
                        log.warn("RSA key missing 'n' or 'e' parameter, skipping");
                        continue;
                    }

                    // Decode Base64URL-encoded modulus and exponent
                    BigInteger modulus = new BigInteger(1, Base64.getUrlDecoder().decode(nBase64));
                    BigInteger exponent = new BigInteger(1, Base64.getUrlDecoder().decode(eBase64));

                    // Build RSA public key
                    RSAPublicKeySpec spec = new RSAPublicKeySpec(modulus, exponent);
                    KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                    RSAPublicKey publicKey = (RSAPublicKey) keyFactory.generatePublic(spec);

                    log.info("Successfully extracted RSA public key with algorithm: {}", alg);
                    return publicKey;
                }
            }

            throw new RuntimeException(
                    "Could not find RSA public key in JWKS response. Response: " + response);

        } catch (Exception ex) {
            throw new RuntimeException("Failed to extract RSA public key from JWKS", ex);
        }
    }
}


