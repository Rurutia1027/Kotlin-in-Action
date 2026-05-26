package com.food.ordering.system.order.service.domain.outbox.scheduler.payment

import com.food.ordering.system.order.service.domain.outbox.model.payment.OrderPaymentOutboxMessage
import com.food.ordering.system.order.service.domain.ports.output.message.publisher.payment.PaymentRequestMessagePublisher
import com.food.ordering.system.outbox.OutboxScheduler
import com.food.ordering.system.outbox.OutboxStatus
import com.food.ordering.system.saga.SagaStatus
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class PaymentOutboxScheduler(
    private val paymentOutboxHelper: PaymentOutboxHelper,
    private val paymentRequestMessagePublisher: PaymentRequestMessagePublisher,
) : OutboxScheduler {

    private val log = LoggerFactory.getLogger(PaymentOutboxScheduler::class.java)

    @Transactional
    @Scheduled(
        fixedDelayString = "\${order-service.outbox-scheduler-fixed-rate}",
        initialDelayString = "\${order-service.outbox-scheduler-initial-delay}",
    )
    override fun processOutboxMessage() {
        val outboxMessagesResponse = paymentOutboxHelper.getPaymentOutboxMessageByOutboxStatusAndSagaStatus(
            OutboxStatus.STARTED,
            SagaStatus.STARTED,
            SagaStatus.COMPENSATING,
        )
        if (outboxMessagesResponse.isPresent && outboxMessagesResponse.get().isNotEmpty()) {
            val outboxMessages = outboxMessagesResponse.get()
            log.info(
                "Received {} OrderPaymentOutboxMessage with ids: {}, sending to message bus!",
                outboxMessages.size,
                outboxMessages.joinToString(",") { it.id.toString() },
            )
            outboxMessages.forEach { outboxMessage ->
                paymentRequestMessagePublisher.publish(outboxMessage, this::updateOutboxStatus)
            }
            log.info("{} OrderPaymentOutboxMessage sent to message bus!", outboxMessages.size)
        }
    }

    private fun updateOutboxStatus(orderPaymentOutboxMessage: OrderPaymentOutboxMessage, outboxStatus: OutboxStatus) {
        orderPaymentOutboxMessage.outboxStatus = outboxStatus
        paymentOutboxHelper.save(orderPaymentOutboxMessage)
        log.info("OrderPaymentOutboxMessage is updated with outbox status: {}", outboxStatus.name)
    }
}
