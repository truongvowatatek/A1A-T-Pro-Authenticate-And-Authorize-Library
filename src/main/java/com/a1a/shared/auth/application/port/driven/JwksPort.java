package com.a1a.shared.auth.application.port.driven;

import javax.crypto.SecretKey;

/**
 * Driven Port for fetching public/secret keys from JWKS endpoint.
 *
 * <p>This port abstracts the mechanism for obtaining cryptographic keys used for JWT verification.
 */
public interface JwksPort {
    /**
     * Get the secret key for HS512 token verification.
     *
     * <p>The implementation should cache the key to avoid frequent calls to the JWKS endpoint.
     *
     * @return SecretKey for HS512 algorithm
     * @throws RuntimeException if the key cannot be fetched
     */
    SecretKey getSecretKey();

    /**
     * Manually refresh keys from the JWKS endpoint.
     *
     * <p>This forces a refresh of the cached keys. Useful when keys are rotated.
     */
    void refreshKeys();
}
