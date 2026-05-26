package com.food.ordering.system.restaurant.service.domain.ports.outputs.repository

import com.food.ordering.system.outbox.OutboxStatus
import com.food.ordering.system.restaurant.service.domain.outbox.model.OrderOutboxMessage
import java.util.*

interface OrderOutboxRepository {
    fun save(orderOutboxMessage: OrderOutboxMessage): OrderOutboxMessage
    fun findByTypeAndOutboxStatus(type: String, outboxStatus: OutboxStatus): Optional<List<OrderOutboxMessage>>
    fun findByTypeAndSagaIdAndOutboxStatus(
        type: String,
        sagaId: UUID,
        outboxStatus: OutboxStatus
    ): Optional<OrderOutboxMessage>

    fun deleteByTypeAndOutboxStatus(type: String, outboxStatus: OutboxStatus)
}