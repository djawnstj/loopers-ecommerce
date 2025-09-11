package com.loopers.interfaces.consumer.product

import com.fasterxml.jackson.databind.ObjectMapper
import com.loopers.config.kafka.KafkaConfig
import com.loopers.interfaces.consumer.product.dto.ProductUnlikedEvent
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component

@Component
class ProductUnlikedConsumer(
    private val objectMapper: ObjectMapper,
) {
    @KafkaListener(
        topics = [PRODUCT_UNLIKED_TOPIC],
        containerFactory = KafkaConfig.BATCH_LISTENER,
    )
    fun handleProductUnliked(
        messages: List<ConsumerRecord<Any, Any>>,
        acknowledgment: Acknowledgment,
    ) {
        messages.forEach { message ->
            val event = objectMapper.readValue(message.value().toString(), ProductUnlikedEvent::class.java)
            // TODO 레디스 랭킹 + 파이프라이닝?
        }
        acknowledgment.acknowledge()
    }

    companion object {
        private const val PRODUCT_UNLIKED_TOPIC = "product.unliked"
    }
}
