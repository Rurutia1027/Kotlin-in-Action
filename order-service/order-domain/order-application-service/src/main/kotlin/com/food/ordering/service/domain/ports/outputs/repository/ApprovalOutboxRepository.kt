package com.food.ordering.system.order.service.domain.ports.output.repository

import com.food.ordering.system.order.service.domain.outbox.model.approval.OrderApprovalOutboxMessage
import com.food.ordering.system.outbox.OutboxStatus
import com.food.ordering.system.saga.SagaStatus
import java.util.Optional
import java.util.UUID

interface ApprovalOutboxRepository {
    fun save(orderApprovalOutboxMessage: OrderApprovalOutboxMessage): OrderApprovalOutboxMessage

    fun findByTypeAndOutboxStatusAndSagaStatus(
        type: String,
        outboxStatus: OutboxStatus,
        vararg sagaStatus: SagaStatus,
    ): Optional<List<OrderApprovalOutboxMessage>>

    fun findByTypeAndSagaIdAndSagaStatus(
        type: String,
        sagaId: UUID,
        vararg sagaStatus: SagaStatus,
    ): Optional<OrderApprovalOutboxMessage>

    fun deleteByTypeAndOutboxStatusAndSagaStatus(
        type: String,
        outboxStatus: OutboxStatus,
        vararg sagaStatus: SagaStatus,
    )
}
