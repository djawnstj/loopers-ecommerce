package com.loopers.cache

import java.time.Duration

interface CacheKey {
	val key: String
	val ttl: Duration
}
