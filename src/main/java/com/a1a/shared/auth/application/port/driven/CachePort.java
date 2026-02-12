package com.a1a.shared.auth.application.port.driven;


import java.time.Duration;
import java.util.Optional;

/**
 * Driven Port for generic caching functionality.
 *
 * <p>This port allows pluggable cache implementations (Caffeine, Redis, etc.).
 *
 * @param <K> Key type
 * @param <V> Value type
 */
public interface CachePort<K, V> {
    /**
     * Get a value from the cache.
     *
     * @param key the cache key
     * @return Optional containing the value if present
     */
    Optional<V> get(K key);

    /**
     * Put a value into the cache with a TTL.
     *
     * @param key the cache key
     * @param value the value to cache
     * @param ttl time-to-live for the entry
     */
    void put(K key, V value, Duration ttl);

    /**
     * Evict a specific key from the cache.
     *
     * @param key the cache key to evict
     */
    void evict(K key);

    /** Clear all entries from the cache. */
    void clear();
}

