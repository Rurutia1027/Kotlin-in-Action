package com.food.ordering.system.restaurant.service.domain.outbox.scheduler

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.food.ordering.system.domain.constants.DomainConstants.UTC
import com.food.ordering.system.domain.valueobject.OrderApprovalStatus
import com.food.ordering.system.outbox.OutboxStatus
import com.food.ordering.system.restaurant.service.domain.exception.RestaurantDomainException
import com.food.ordering.system.restaurant.service.domain.outbox.model.OrderEventPayload
import com.food.ordering.system.restaurant.service.domain.outbox.model.OrderOutboxMessage
import com.food.ordering.system.restaurant.service.domain.ports.outputs.repository.OrderOutboxRepository
import com.food.ordering.system.saga.order.SagaConstants.ORDER_SAGA_NAME
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*

@Component
class OrderOutboxHelper(
    private val orderOutboxRepository: OrderOutboxRepository,
    private val objectMapper: ObjectMapper,
) {
    private val log = LoggerFactory.getLogger(OrderOutboxHelper::class.java)

    @Transactional(readOnly = true)
    fun getCompletedOrderOutboxMessageBySagaIdAndOutboxStatus(
        sagaId: UUID,
        outboxStatus: OutboxStatus,
    ): Optional<OrderOutboxMessage> =
        orderOutboxRepository.findByTypeAndSagaIdAndOutboxStatus(ORDER_SAGA_NAME, sagaId, outboxStatus)


    @Transactional(readOnly = true)
    fun getOrderOutboxMessageByOutboxStatus(outboxStatus: OutboxStatus): Optional<List<OrderOutboxMessage>> =
        orderOutboxRepository.findByTypeAndOutboxStatus(ORDER_SAGA_NAME, outboxStatus)

    @Transactional
    fun deleteOrderOutboxMessageByOutboxStatus(outboxStatus: OutboxStatus) {
        orderOutboxRepository.deleteByTypeAndOutboxStatus(ORDER_SAGA_NAME, outboxStatus)
    }

    @Transactional
    fun saveOrderOutboxMessage(
        orderEventPayload: OrderEventPayload,
        approvalStatus: OrderApprovalStatus,
        outboxStatus: OutboxStatus,
        sagaId: UUID
    ) {
        save(
            OrderOutboxMessage.builder()
                .id(UUID.randomUUID())
                .sagaId(sagaId)
                .createdAt(orderEventPayload.createdAt)
                .processedAt(ZonedDateTime.now(ZoneId.of(UTC)))
                .type(ORDER_SAGA_NAME)
                .payload(createPayload(orderEventPayload))
                .approvalStatus(approvalStatus)
                .outboxStatus(outboxStatus)
                .version(0)
                .build(),
        )
    }

    @Transactional
    fun updateOutboxStatus(orderOutboxMessage: OrderOutboxMessage, outboxStatus: OutboxStatus) {
        orderOutboxMessage.outboxStatus = outboxStatus
        save(orderOutboxMessage)
        log.info("Order outbox table status is updated as: {}", outboxStatus.name)
    }

    private fun createPayload(orderEventPayload: OrderEventPayload): String =
        try {
            objectMapper.writeValueAsString(orderEventPayload)
        } catch (e: JsonProcessingException) {
            log.error("Could not create OrderEventPayload json!", e)
            throw RestaurantDomainException("Could not create OrderEventPayload json!", e)
        }

    private fun save(orderOutboxMessage: OrderOutboxMessage) {
        val response = orderOutboxRepository.save(orderOutboxMessage)
            ?: throw RestaurantDomainException("Could not save OrderOutboxMessage!")
        log.info("OrderOutboxMessage saved with id: {}", orderOutboxMessage.id)
    }
}