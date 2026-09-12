package com.sorsix.pocetna.common

import java.time.Duration
import java.time.Instant

/**
 * Thread-safe cache that recomputes [loader] only after [ttl] has elapsed, so widgets backed
 * by slow/rate-limited third-party APIs stay fast and polite. If [loader] throws, the previous
 * good value (if any) keeps being served — with a short [retryDelay] before trying again —
 * instead of a transient upstream hiccup getting cached as "no data" for the full [ttl].
 */
class TtlCache<T>(
    private val ttl: Duration,
    private val retryDelay: Duration = Duration.ofSeconds(20),
    private val loader: () -> T,
) {
    @Volatile private var value: T? = null
    @Volatile private var expiresAt: Instant = Instant.MIN
    @Volatile private var lastFailure: Exception? = null
    @Volatile private var retryAt: Instant = Instant.MIN
    private val lock = Any()

    fun get(): T {
        val cached = value
        if (cached != null && Instant.now().isBefore(expiresAt)) {
            return cached
        }
        synchronized(lock) {
            val stillCached = value
            if (stillCached != null && Instant.now().isBefore(expiresAt)) {
                return stillCached
            }
            // With no value yet, a failing upstream must not be re-asked on every single
            // call: that turns one refusal into a request per visitor, which is how a
            // rate-limited or bot-protected source keeps refusing.
            if (stillCached == null) {
                val failure = lastFailure
                if (failure != null && Instant.now().isBefore(retryAt)) {
                    throw failure
                }
            }
            return try {
                val fresh = loader()
                value = fresh
                expiresAt = Instant.now().plus(ttl)
                lastFailure = null
                fresh
            } catch (ex: Exception) {
                if (stillCached != null) {
                    expiresAt = Instant.now().plus(retryDelay)
                    stillCached
                } else {
                    lastFailure = ex
                    retryAt = Instant.now().plus(retryDelay)
                    throw ex
                }
            }
        }
    }
}

class TtlCacheMap<K, T>(
    private val ttl: Duration,
    private val retryDelay: Duration = Duration.ofSeconds(20),
    private val loader: (K) -> T,
) {
    private val caches = java.util.concurrent.ConcurrentHashMap<K, TtlCache<T>>()

    fun get(key: K): T = caches.computeIfAbsent(key) { k -> TtlCache(ttl, retryDelay) { loader(k) } }.get()
}
