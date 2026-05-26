package com.food.ordering.system.order.service.domain.outbox.scheduler.approval

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.food.ordering.system.domain.valueobject.OrderStatus
import com.food.ordering.system.order.service.domain.exception.OrderDomainException
import com.food.ordering.system.order.service.domain.outbox.model.approval.OrderApprovalEventPayload
import com.food.ordering.system.order.service.domain.outbox.model.approval.OrderApprovalOutboxMessage
import com.food.ordering.system.order.service.domain.ports.output.repository.ApprovalOutboxRepository
import com.food.ordering.system.outbox.OutboxStatus
import com.food.ordering.system.saga.SagaStatus
import com.food.ordering.system.saga.order.SagaConstants.ORDER_SAGA_NAME
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.util.Optional
import java.util.UUID

@Component
class ApprovalOutboxHelper(
    private val approvalOutboxRepository: ApprovalOutboxRepository,
    private val objectMapper: ObjectMapper,
) {
    private val log = LoggerFactory.getLogger(ApprovalOutboxHelper::class.java)

    @Transactional(readOnly = true)
    fun getApprovalOutboxMessageByOutboxStatusAndSagaStatus(
        outboxStatus: OutboxStatus,
        vararg sagaStatus: SagaStatus,
    ): Optional<List<OrderApprovalOutboxMessage>> =
        approvalOutboxRepository.findByTypeAndOutboxStatusAndSagaStatus(ORDER_SAGA_NAME, outboxStatus, *sagaStatus)

    @Transactional(readOnly = true)
    fun getApprovalOutboxMessageBySagaIdAndSagaStatus(
        sagaId: UUID,
        vararg sagaStatus: SagaStatus,
    ): Optional<OrderApprovalOutboxMessage> =
        approvalOutboxRepository.findByTypeAndSagaIdAndSagaStatus(ORDER_SAGA_NAME, sagaId, *sagaStatus)

    @Transactional
    fun save(orderApprovalOutboxMessage: OrderApprovalOutboxMessage) {
        val response = approvalOutboxRepository.save(orderApprovalOutboxMessage)
            ?: throw OrderDomainException(
                "Could not save OrderApprovalOutboxMessage with outbox id: ${orderApprovalOutboxMessage.id}"
            )
        log.info("OrderApprovalOutboxMessage saved with outbox id: {}", orderApprovalOutboxMessage.id)
    }

    @Transactional
    fun saveApprovalOutboxMessage(
        orderApprovalEventPayload: OrderApprovalEventPayload,
        orderStatus: OrderStatus,
        sagaStatus: SagaStatus,
        outboxStatus: OutboxStatus,
        sagaId: UUID,
    ) {
        save(
            OrderApprovalOutboxMessage.builder()
                .id(UUID.randomUUID())
                .sagaId(sagaId)
                .createdAt(orderApprovalEventPayload.createdAt!!)
                .type(ORDER_SAGA_NAME)
                .payload(createPayload(orderApprovalEventPayload))
                .orderStatus(orderStatus)
                .sagaStatus(sagaStatus)
                .outboxStatus(outboxStatus)
                .build()
        )
    }

    @Transactional
    fun deleteApprovalOutboxMessageByOutboxStatusAndSagaStatus(
        outboxStatus: OutboxStatus,
        vararg sagaStatus: SagaStatus,
    ) {
        approvalOutboxRepository.deleteByTypeAndOutboxStatusAndSagaStatus(ORDER_SAGA_NAME, outboxStatus, *sagaStatus)
    }

    private fun createPayload(orderApprovalEventPayload: OrderApprovalEventPayload): String =
        try {
            objectMapper.writeValueAsString(orderApprovalEventPayload)
        } catch (e: JsonProcessingException) {
            log.error("Could not create OrderApprovalEventPayload for order id: {}", orderApprovalEventPayload.orderId, e)
            throw OrderDomainException(
                "Could not create OrderApprovalEventPayload for order id: ${orderApprovalEventPayload.orderId}",
                e,
            )
        }
}
