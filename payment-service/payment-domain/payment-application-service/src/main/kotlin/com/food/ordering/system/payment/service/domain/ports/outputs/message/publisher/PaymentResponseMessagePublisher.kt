package com.food.ordering.system.payment.service.domain.ports.outputs.message.publisher

import OrderOutboxMessage
import com.food.ordering.system.outbox.OutboxStatus
import java.util.function.BiConsumer

interface PaymentResponseMessagePublisher {
    fun publish(
        orderOutboxMessage: OrderOutboxMessage,
        outboxCallback: BiConsumer<OrderOutboxMessage, OutboxStatus>
    )
}