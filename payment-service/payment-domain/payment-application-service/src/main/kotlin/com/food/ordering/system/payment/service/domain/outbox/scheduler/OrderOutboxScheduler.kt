package com.food.ordering.system.payment.service.domain.outbox.scheduler

import com.food.ordering.system.outbox.OutboxScheduler
import com.food.ordering.system.outbox.OutboxStatus
import com.food.ordering.system.payment.service.domain.ports.outputs.message.publisher.PaymentResponseMessagePublisher
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class OrderOutboxScheduler(
    private val orderOutboxHelper: OrderOutboxHelper,
    private val paymentResponseMessagePublisher: PaymentResponseMessagePublisher
) : OutboxScheduler {
    private val log = LoggerFactory.getLogger(OrderOutboxScheduler::class.java)

    @Transactional
    @Scheduled(
        fixedRateString = "\${payment-service.outbox-scheduler-fixed-rate}",
        initialDelayString = "\${payment-service.outbox-scheduler-initial-delay}",
    )
    override fun processOutboxMessage() {
        val outboxMessages = orderOutboxHelper.getOrderOutboxMessageByOutboxStatus(OutboxStatus.STARTED)
        if (outboxMessages.isPresent && outboxMessages.get().isNotEmpty()) {
            val messages = outboxMessages.get()
            log.info(
                "Received {} OrderOutboxMessage with ids {}, sending to message bus!",
                messages.size,
                messages.joinToString(",") { it.id.toString() },
            )
            messages.forEach { orderOutboxMessage ->
                paymentResponseMessagePublisher.publish(
                    orderOutboxMessage,
                    orderOutboxHelper::updateOutboxMessage
                )
            }
            log.info("{} OrderOutboxMessage sent to message bus!", messages.size)
        }
    }
}