package com.loopers.application.common

import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean

interface LockManager {
    fun tryLock(key: String, vararg keys: String): Boolean
    fun unlock(key: String, vararg keys: String)

    fun generateKey(vararg keys: String) = keys.joinToString(DELIMITER)

    companion object {
        private const val DELIMITER = ":"
    }
}

@Component
class SimpleLockManager : LockManager {
    private val locks = ConcurrentHashMap<String, AtomicBoolean>()

    override fun tryLock(key: String, vararg keys: String): Boolean {
        val lockKey = generateKey(key, *keys)
        val lock = locks.computeIfAbsent(lockKey) { AtomicBoolean(false) }
        return lock.compareAndSet(false, true)
    }

    override fun unlock(key: String, vararg keys: String) {
        val lockKey = generateKey(key, *keys)
        locks[lockKey]?.set(false)
    }
}
