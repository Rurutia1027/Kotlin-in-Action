package com.food.ordering.system.order.service.domain.outbox.scheduler.payment

import com.food.ordering.system.outbox.OutboxScheduler
import com.food.ordering.system.outbox.OutboxStatus
import com.food.ordering.system.saga.SagaStatus
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class PaymentOutboxCleanerScheduler(
    private val paymentOutboxHelper: PaymentOutboxHelper,
) : OutboxScheduler {

    private val log = LoggerFactory.getLogger(PaymentOutboxCleanerScheduler::class.java)

    @Scheduled(cron = "@midnight")
    override fun processOutboxMessage() {
        val outboxMessagesResponse = paymentOutboxHelper.getPaymentOutboxMessageByOutboxStatusAndSagaStatus(
            OutboxStatus.COMPLETED,
            SagaStatus.SUCCEEDED,
            SagaStatus.FAILED,
            SagaStatus.COMPENSATED,
        )
        if (outboxMessagesResponse.isPresent) {
            val outboxMessages = outboxMessagesResponse.get()
            log.info(
                "Received {} OrderPaymentOutboxMessage for clean-up. The payloads: {}",
                outboxMessages.size,
                outboxMessages.joinToString("\n") { it.payload },
            )
            paymentOutboxHelper.deletePaymentOutboxMessageByOutboxStatusAndSagaStatus(
                OutboxStatus.COMPLETED,
                SagaStatus.SUCCEEDED,
                SagaStatus.FAILED,
                SagaStatus.COMPENSATED,
            )
            log.info("{} OrderPaymentOutboxMessage deleted!", outboxMessages.size)
        }
    }
}
