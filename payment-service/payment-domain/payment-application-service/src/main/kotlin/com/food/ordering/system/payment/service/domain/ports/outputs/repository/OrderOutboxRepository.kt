package com.food.ordering.system.payment.service.domain.ports.outputs.repository

import OrderOutboxMessage
import com.food.ordering.system.domain.valueobject.PaymentStatus
import com.food.ordering.system.outbox.OutboxStatus
import java.util.*

interface OrderOutboxRepository {
    fun save(orderOutboxMessage: OrderOutboxMessage): OrderOutboxMessage
    fun findByTypeAndOutboxStatus(type: String, status: OutboxStatus): Optional<List<OrderOutboxMessage>>
    fun findByTypeAndSagaIdPaymentStatusAndOutboxStatus(
        type: String,
        sagaId: UUID,
        paymentStatus: PaymentStatus,
        outboxStatus: OutboxStatus,
    ): Optional<OrderOutboxMessage>

    fun deleteByTypeAndOutboxStatus(type: String, status: OutboxStatus)
}