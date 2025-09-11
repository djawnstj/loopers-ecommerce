package com.loopers.interfaces.consumer.order

import com.fasterxml.jackson.databind.ObjectMapper
import com.loopers.config.kafka.KafkaConfig
import com.loopers.application.product.ProductFacade
import com.loopers.interfaces.consumer.order.dto.OrderCompletedEvent
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component

@Component
class OrderCompletedConsumer(
    private val objectMapper: ObjectMapper,
    private val productFacade: ProductFacade,
) {
    @KafkaListener(
        topics = [ORDER_COMPLETED_TOPIC],
        containerFactory = KafkaConfig.BATCH_LISTENER,
    )
    fun handleOrderCompleted(
        messages: List<ConsumerRecord<Any, Any>>,
        acknowledgment: Acknowledgment,
    ) {
        messages.forEach { message ->
            val event = objectMapper.readValue(message.value().toString(), OrderCompletedEvent::class.java)
            productFacade.updateOrderRankingByOrderId(event.orderId, event.eventDate)
        }
        acknowledgment.acknowledge()
    }

    companion object {
        private const val ORDER_COMPLETED_TOPIC = "order.completed"
    }
}
