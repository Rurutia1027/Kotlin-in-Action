package com.food.ordering.system.order.service.domain.outbox.scheduler.payment

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.food.ordering.system.domain.valueobject.OrderStatus
import com.food.ordering.system.order.service.domain.exception.OrderDomainException
import com.food.ordering.system.order.service.domain.outbox.model.payment.OrderPaymentEventPayload
import com.food.ordering.system.order.service.domain.outbox.model.payment.OrderPaymentOutboxMessage
import com.food.ordering.system.order.service.domain.ports.output.repository.PaymentOutboxRepository
import com.food.ordering.system.outbox.OutboxStatus
import com.food.ordering.system.saga.SagaStatus
import com.food.ordering.system.saga.order.SagaConstants.ORDER_SAGA_NAME
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.util.Optional
import java.util.UUID

@Component
class PaymentOutboxHelper(
    private val paymentOutboxRepository: PaymentOutboxRepository,
    private val objectMapper: ObjectMapper,
) {
    private val log = LoggerFactory.getLogger(PaymentOutboxHelper::class.java)

    @Transactional(readOnly = true)
    fun getPaymentOutboxMessageByOutboxStatusAndSagaStatus(
        outboxStatus: OutboxStatus,
        vararg sagaStatus: SagaStatus,
    ): Optional<List<OrderPaymentOutboxMessage>> =
        paymentOutboxRepository.findByTypeAndOutboxStatusAndSagaStatus(ORDER_SAGA_NAME, outboxStatus, *sagaStatus)

    @Transactional(readOnly = true)
    fun getPaymentOutboxMessageBySagaIdAndSagaStatus(
        sagaId: UUID,
        vararg sagaStatus: SagaStatus,
    ): Optional<OrderPaymentOutboxMessage> =
        paymentOutboxRepository.findByTypeAndSagaIdAndSagaStatus(ORDER_SAGA_NAME, sagaId, *sagaStatus)

    @Transactional
    fun save(orderPaymentOutboxMessage: OrderPaymentOutboxMessage) {
        val response = paymentOutboxRepository.save(orderPaymentOutboxMessage)
            ?: throw OrderDomainException(
                "Could not save OrderPaymentOutboxMessage with outbox id: ${orderPaymentOutboxMessage.id}"
            )
        log.info("OrderPaymentOutboxMessage saved with outbox id: {}", orderPaymentOutboxMessage.id)
    }

    @Transactional
    fun savePaymentOutboxMessage(
        paymentEventPayload: OrderPaymentEventPayload,
        orderStatus: OrderStatus,
        sagaStatus: SagaStatus,
        outboxStatus: OutboxStatus,
        sagaId: UUID,
    ) {
        save(
            OrderPaymentOutboxMessage.builder()
                .id(UUID.randomUUID())
                .sagaId(sagaId)
                .createdAt(paymentEventPayload.createdAt!!)
                .type(ORDER_SAGA_NAME)
                .payload(createPayload(paymentEventPayload))
                .orderStatus(orderStatus)
                .sagaStatus(sagaStatus)
                .outboxStatus(outboxStatus)
                .build()
        )
    }

    @Transactional
    fun deletePaymentOutboxMessageByOutboxStatusAndSagaStatus(
        outboxStatus: OutboxStatus,
        vararg sagaStatus: SagaStatus,
    ) {
        paymentOutboxRepository.deleteByTypeAndOutboxStatusAndSagaStatus(ORDER_SAGA_NAME, outboxStatus, *sagaStatus)
    }

    private fun createPayload(paymentEventPayload: OrderPaymentEventPayload): String =
        try {
            objectMapper.writeValueAsString(paymentEventPayload)
        } catch (e: JsonProcessingException) {
            log.error("Could not create OrderPaymentEventPayload object for order id: {}", paymentEventPayload.orderId, e)
            throw OrderDomainException(
                "Could not create OrderPaymentEventPayload object for order id: ${paymentEventPayload.orderId}",
                e,
            )
        }
}
