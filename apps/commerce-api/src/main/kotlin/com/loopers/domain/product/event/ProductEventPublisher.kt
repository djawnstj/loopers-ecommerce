package com.loopers.domain.product.event

interface ProductEventPublisher {
    fun publish(event: ProductViewedEvent)
    fun publish(event: ProductLikedEvent)
    fun publish(event: ProductUnlikedEvent)
}
