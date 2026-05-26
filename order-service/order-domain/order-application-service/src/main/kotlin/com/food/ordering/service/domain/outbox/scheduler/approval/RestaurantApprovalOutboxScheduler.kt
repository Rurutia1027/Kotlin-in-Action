package com.food.ordering.system.order.service.domain.outbox.scheduler.approval

import com.food.ordering.system.order.service.domain.outbox.model.approval.OrderApprovalOutboxMessage
import com.food.ordering.system.order.service.domain.ports.output.message.publisher.restaurantapproval.RestaurantApprovalRequestMessagePublisher
import com.food.ordering.system.outbox.OutboxScheduler
import com.food.ordering.system.outbox.OutboxStatus
import com.food.ordering.system.saga.SagaStatus
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class RestaurantApprovalOutboxScheduler(
    private val approvalOutboxHelper: ApprovalOutboxHelper,
    private val restaurantApprovalRequestMessagePublisher: RestaurantApprovalRequestMessagePublisher,
) : OutboxScheduler {

    private val log = LoggerFactory.getLogger(RestaurantApprovalOutboxScheduler::class.java)

    @Transactional
    @Scheduled(
        fixedDelayString = "\${order-service.outbox-scheduler-fixed-rate}",
        initialDelayString = "\${order-service.outbox-scheduler-initial-delay}",
    )
    override fun processOutboxMessage() {
        val outboxMessagesResponse = approvalOutboxHelper.getApprovalOutboxMessageByOutboxStatusAndSagaStatus(
            OutboxStatus.STARTED,
            SagaStatus.PROCESSING,
        )
        if (outboxMessagesResponse.isPresent && outboxMessagesResponse.get().isNotEmpty()) {
            val outboxMessages = outboxMessagesResponse.get()
            log.info(
                "Received {} OrderApprovalOutboxMessage with ids: {}, sending to message bus!",
                outboxMessages.size,
                outboxMessages.joinToString(",") { it.id.toString() },
            )
            outboxMessages.forEach { outboxMessage ->
                restaurantApprovalRequestMessagePublisher.publish(outboxMessage, this::updateOutboxStatus)
            }
            log.info("{} OrderApprovalOutboxMessage sent to message bus!", outboxMessages.size)
        }
    }

    private fun updateOutboxStatus(orderApprovalOutboxMessage: OrderApprovalOutboxMessage, outboxStatus: OutboxStatus) {
        orderApprovalOutboxMessage.outboxStatus = outboxStatus
        approvalOutboxHelper.save(orderApprovalOutboxMessage)
        log.info("OrderApprovalOutboxMessage is updated with outbox status: {}", outboxStatus.name)
    }
}
