package com.food.ordering.system.restaurant.service.domain.outbox.scheduler

import com.food.ordering.system.outbox.OutboxScheduler
import com.food.ordering.system.outbox.OutboxStatus
import com.food.ordering.system.restaurant.service.domain.ports.outputs.message.publisher.RestaurantApprovalResponseMessagePublisher
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class OrderOutboxScheduler(
    private val orderOutboxHelper: OrderOutboxHelper,
    private val responseMessagePublisher: RestaurantApprovalResponseMessagePublisher
) : OutboxScheduler {

    private val log = LoggerFactory.getLogger(OrderOutboxScheduler::class.java)

    @Scheduled(
        fixedRateString = "10000",
        initialDelayString = "10000",
    )
    @Transactional
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
                responseMessagePublisher.publish(
                    orderOutboxMessage,
                    orderOutboxHelper::updateOutboxStatus,
                )
            }

            log.info("{} OrderOutboxMessage sent to message bus!", messages.size)
        }
    }

}