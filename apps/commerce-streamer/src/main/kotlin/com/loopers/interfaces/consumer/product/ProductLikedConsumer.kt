package com.loopers.interfaces.consumer.product

import com.fasterxml.jackson.databind.ObjectMapper
import com.loopers.config.kafka.KafkaConfig
import com.loopers.application.product.ProductFacade
import com.loopers.interfaces.consumer.product.dto.ProductLikedEvent
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component

@Component
class ProductLikedConsumer(
    private val objectMapper: ObjectMapper,
    private val productFacade: ProductFacade,
) {
    @KafkaListener(
        topics = [PRODUCT_LIKED_TOPIC],
        containerFactory = KafkaConfig.BATCH_LISTENER,
    )
    fun handleProductLiked(
        messages: List<ConsumerRecord<Any, Any>>,
        acknowledgment: Acknowledgment,
    ) {
        messages.forEach { message ->
            val event = objectMapper.readValue(message.value().toString(), ProductLikedEvent::class.java)
            productFacade.updateLikeRanking(event.productId, event.eventDate)
        }
        acknowledgment.acknowledge()
    }

    companion object {
        private const val PRODUCT_LIKED_TOPIC = "product.liked"
    }
}
