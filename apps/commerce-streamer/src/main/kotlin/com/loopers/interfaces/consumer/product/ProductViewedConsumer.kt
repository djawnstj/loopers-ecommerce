package com.loopers.interfaces.consumer.product

import com.fasterxml.jackson.databind.ObjectMapper
import com.loopers.config.kafka.KafkaConfig
import com.loopers.application.product.ProductFacade
import com.loopers.interfaces.consumer.product.dto.ProductViewedEvent
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component

@Component
class ProductViewedConsumer(
    private val objectMapper: ObjectMapper,
    private val productFacade: ProductFacade,
) {
    @KafkaListener(
        topics = [PRODUCT_VIEWED_TOPIC],
        containerFactory = KafkaConfig.BATCH_LISTENER,
    )
    fun handleProductViewed(
        messages: List<ConsumerRecord<Any, Any>>,
        acknowledgment: Acknowledgment,
    ) {
        messages.forEach { message ->
            val event = objectMapper.readValue(message.value().toString(), ProductViewedEvent::class.java)
            productFacade.updateViewRanking(event.productId, event.eventDate)
        }
        acknowledgment.acknowledge()
    }

    companion object {
        private const val PRODUCT_VIEWED_TOPIC = "product.viewed"
    }
}
