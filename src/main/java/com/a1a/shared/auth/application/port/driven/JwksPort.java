package com.a1a.shared.auth.application.port.driven;

import java.security.interfaces.RSAPublicKey;

/**
 * Driven Port for fetching public keys from JWKS endpoint.
 *
 * <p>This port abstracts the mechanism for obtaining cryptographic keys used for JWT verification.
 */
public interface JwksPort {
    /**
     * Get the RSA public key for RS256 token verification.
     *
     * <p>The implementation should cache the key to avoid frequent calls to the JWKS endpoint.
     *
     * @return RSAPublicKey for RS256 algorithm
     * @throws RuntimeException if the key cannot be fetched
     */
    RSAPublicKey getPublicKey();

    /**
     * Manually refresh keys from the JWKS endpoint.
     *
     * <p>This forces a refresh of the cached keys. Useful when keys are rotated.
     */
    void refreshKeys();
}
