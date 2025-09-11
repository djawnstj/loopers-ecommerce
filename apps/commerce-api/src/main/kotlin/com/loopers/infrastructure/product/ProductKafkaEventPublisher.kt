package com.loopers.infrastructure.product

import com.fasterxml.jackson.databind.ObjectMapper
import com.loopers.domain.product.event.ProductEventPublisher
import com.loopers.domain.product.event.ProductLikedEvent
import com.loopers.domain.product.event.ProductUnlikedEvent
import com.loopers.domain.product.event.ProductViewedEvent
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class ProductKafkaEventPublisher(
    private val kafkaTemplate: KafkaTemplate<Any, Any>,
    private val objectMapper: ObjectMapper,
) : ProductEventPublisher {
    override fun publish(event: ProductViewedEvent) {
        val data = objectMapper.writeValueAsString(event)

        kafkaTemplate.send(PRODUCT_VIEWED_TOPIC, event.productId.toString(), data)
            .whenComplete { result, ex ->
                if (ex != null) {
                    kafkaTemplate.send(
                        PRODUCT_VIEWED_DLT,
                        event.productId.toString(),
                        data,
                    )
                }
            }
    }

    override fun publish(event: ProductLikedEvent) {
        val data = objectMapper.writeValueAsString(event)

        kafkaTemplate.send(PRODUCT_LIKED_TOPIC, event.productId.toString(), data)
            .whenComplete { result, ex ->
                if (ex != null) {
                    kafkaTemplate.send(
                        PRODUCT_LIKED_DLT,
                        event.productId.toString(),
                        data,
                    )
                }
            }
    }

    override fun publish(event: ProductUnlikedEvent) {
        val data = objectMapper.writeValueAsString(event)

        kafkaTemplate.send(PRODUCT_UNLIKED_TOPIC, event.productId.toString(), data)
            .whenComplete { result, ex ->
                if (ex != null) {
                    kafkaTemplate.send(
                        PRODUCT_UNLIKED_DLT,
                        event.productId.toString(),
                        data,
                    )
                }
            }
    }

    companion object {
        private const val PRODUCT_VIEWED_TOPIC = "product.viewed"
        private const val PRODUCT_VIEWED_DLT = "product.viewed.dlt"
        private const val PRODUCT_LIKED_TOPIC = "product.liked"
        private const val PRODUCT_LIKED_DLT = "product.liked.dlt"
        private const val PRODUCT_UNLIKED_TOPIC = "product.unliked"
        private const val PRODUCT_UNLIKED_DLT = "product.unliked.dlt"
    }
}
