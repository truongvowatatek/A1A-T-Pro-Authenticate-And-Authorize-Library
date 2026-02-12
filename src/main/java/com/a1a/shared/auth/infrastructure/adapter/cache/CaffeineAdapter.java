package com.a1a.shared.auth.infrastructure.adapter.cache;

import com.a1a.shared.auth.application.port.driven.CachePort;
import com.github.benmanes.caffeine.cache.Cache;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.Optional;

/**
 * Caffeine-based in-memory cache adapter.
 *
 * <p>Implements CachePort using Caffeine for fast in-memory caching.
 *
 * @param <K> Key type
 * @param <V> Value type
 */
@Slf4j
@RequiredArgsConstructor
public class CaffeineAdapter<K, V> implements CachePort<K, V> {
    private final Cache<K, V> cache;

    @Override
    public Optional<V> get(K key) {
        V value = cache.getIfPresent(key);
        if (value != null) {
            log.debug("Cache hit for key: {}", key);
        } else {
            log.debug("Cache miss for key: {}", key);
        }
        return Optional.ofNullable(value);
    }

    @Override
    public void put(K key, V value, Duration ttl) {
        cache.put(key, value);
        log.debug("Cached key: {} with TTL: {}", key, ttl);
    }

    @Override
    public void evict(K key) {
        cache.invalidate(key);
        log.debug("Evicted key: {}", key);
    }

    @Override
    public void clear() {
        cache.invalidateAll();
        log.debug("Cache cleared");
    }
}


