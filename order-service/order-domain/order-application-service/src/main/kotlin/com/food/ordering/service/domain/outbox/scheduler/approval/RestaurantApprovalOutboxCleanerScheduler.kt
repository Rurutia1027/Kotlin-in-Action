package com.food.ordering.system.order.service.domain.outbox.scheduler.approval

import com.food.ordering.system.outbox.OutboxScheduler
import com.food.ordering.system.outbox.OutboxStatus
import com.food.ordering.system.saga.SagaStatus
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class RestaurantApprovalOutboxCleanerScheduler(
    private val approvalOutboxHelper: ApprovalOutboxHelper,
) : OutboxScheduler {

    private val log = LoggerFactory.getLogger(RestaurantApprovalOutboxCleanerScheduler::class.java)

    @Scheduled(cron = "@midnight")
    override fun processOutboxMessage() {
        val outboxMessagesResponse = approvalOutboxHelper.getApprovalOutboxMessageByOutboxStatusAndSagaStatus(
            OutboxStatus.COMPLETED,
            SagaStatus.SUCCEEDED,
            SagaStatus.FAILED,
            SagaStatus.COMPENSATED,
        )
        if (outboxMessagesResponse.isPresent) {
            val outboxMessages = outboxMessagesResponse.get()
            log.info(
                "Received {} OrderApprovalOutboxMessage for clean-up. The payloads: {}",
                outboxMessages.size,
                outboxMessages.joinToString("\n") { it.payload },
            )
            approvalOutboxHelper.deleteApprovalOutboxMessageByOutboxStatusAndSagaStatus(
                OutboxStatus.COMPLETED,
                SagaStatus.SUCCEEDED,
                SagaStatus.FAILED,
                SagaStatus.COMPENSATED,
            )
            log.info("{} OrderApprovalOutboxMessage deleted!", outboxMessages.size)
        }
    }
}
