package com.loopers.infrastructure.order

import com.fasterxml.jackson.databind.ObjectMapper
import com.loopers.domain.order.event.OrderCompletedEvent
import com.loopers.domain.order.event.OrderEventPublisher
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class OrderKafkaEventPublisher(
    private val kafkaTemplate: KafkaTemplate<Any, Any>,
    private val objectMapper: ObjectMapper,
) : OrderEventPublisher {

    override fun publish(event: OrderCompletedEvent) {
        val data = objectMapper.writeValueAsString(event)

        kafkaTemplate.send(ORDER_COMPLETED_TOPIC, event.orderId.toString(), data)
            .whenComplete { result, ex ->
                if (ex != null) {
                    kafkaTemplate.send(
                        ORDER_COMPLETED_DLT,
                        event.orderId.toString(),
                        data,
                    )
                }
            }
    }

    companion object {
        private const val ORDER_COMPLETED_TOPIC = "order.completed"
        private const val ORDER_COMPLETED_DLT = "order.completed.dlt"
    }
}