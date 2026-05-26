package com.food.ordering.system.order.service.domain

import com.food.ordering.system.domain.constants.DomainConstants.UTC
import com.food.ordering.system.domain.valueobject.OrderStatus
import com.food.ordering.system.order.service.domain.dto.message.RestaurantApprovalResponse
import com.food.ordering.system.order.service.domain.entity.Order
import com.food.ordering.system.order.service.domain.event.OrderCancelledEvent
import com.food.ordering.system.order.service.domain.exception.OrderDomainException
import com.food.ordering.system.order.service.domain.mapper.OrderDataMapper
import com.food.ordering.system.order.service.domain.outbox.model.approval.OrderApprovalOutboxMessage
import com.food.ordering.system.order.service.domain.outbox.model.payment.OrderPaymentOutboxMessage
import com.food.ordering.system.order.service.domain.outbox.scheduler.approval.ApprovalOutboxHelper
import com.food.ordering.system.order.service.domain.outbox.scheduler.payment.PaymentOutboxHelper
import com.food.ordering.system.outbox.OutboxStatus
import com.food.ordering.system.saga.SagaStatus
import com.food.ordering.system.saga.SagaStep
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*

@Component
class OrderApprovalSaga(
    private val orderDomainService: OrderDomainService,
    private val orderSagaHelper: OrderSagaHelper,
    private val paymentOutboxHelper: PaymentOutboxHelper,
    private val approvalOutboxHelper: ApprovalOutboxHelper,
    private val orderDataMapper: OrderDataMapper,
) : SagaStep<RestaurantApprovalResponse> {

    private val log = LoggerFactory.getLogger(OrderApprovalSaga::class.java)

    @Transactional
    override fun process(restaurantApprovalResponse: RestaurantApprovalResponse) {
        val orderApprovalOutboxMessageResponse = approvalOutboxHelper.getApprovalOutboxMessageBySagaIdAndSagaStatus(
            UUID.fromString(restaurantApprovalResponse.sagaId),
            SagaStatus.PROCESSING,
        )
        if (orderApprovalOutboxMessageResponse.isEmpty) {
            log.info(
                "An outbox message with saga id: {} is already processed!",
                restaurantApprovalResponse.sagaId,
            )
            return
        }

        val orderApprovalOutboxMessage = orderApprovalOutboxMessageResponse.get()
        val order = approveOrder(restaurantApprovalResponse)
        val sagaStatus = orderSagaHelper.orderStatusToSagaStatus(order.orderStatus!!)

        approvalOutboxHelper.save(
            getUpdatedApprovalOutboxMessage(orderApprovalOutboxMessage, order.orderStatus!!, sagaStatus),
        )

        paymentOutboxHelper.save(
            getUpdatedPaymentOutboxMessage(
                restaurantApprovalResponse.sagaId!!,
                order.orderStatus!!,
                sagaStatus,
            ),
        )

        log.info("Order with id: {} is approved", order.id!!.value)
    }

    @Transactional
    override fun rollback(restaurantApprovalResponse: RestaurantApprovalResponse) {
        val orderApprovalOutboxMessageResponse = approvalOutboxHelper.getApprovalOutboxMessageBySagaIdAndSagaStatus(
            UUID.fromString(restaurantApprovalResponse.sagaId),
            SagaStatus.PROCESSING,
        )
        if (orderApprovalOutboxMessageResponse.isEmpty) {
            log.info(
                "An outbox message with saga id: {} is already roll backed!",
                restaurantApprovalResponse.sagaId,
            )
            return
        }

        val orderApprovalOutboxMessage = orderApprovalOutboxMessageResponse.get()
        val domainEvent = rollbackOrder(restaurantApprovalResponse)
        val sagaStatus = orderSagaHelper.orderStatusToSagaStatus(domainEvent.order.orderStatus!!)

        approvalOutboxHelper.save(
            getUpdatedApprovalOutboxMessage(
                orderApprovalOutboxMessage,
                domainEvent.order.orderStatus!!,
                sagaStatus,
            ),
        )

        paymentOutboxHelper.savePaymentOutboxMessage(
            orderDataMapper.orderCancelledEventToOrderPaymentEventPayload(domainEvent),
            domainEvent.order.orderStatus!!,
            sagaStatus,
            OutboxStatus.STARTED,
            UUID.fromString(restaurantApprovalResponse.sagaId),
        )

        log.info("Order with id: {} is cancelling", domainEvent.order.id!!.value)
    }

    private fun approveOrder(restaurantApprovalResponse: RestaurantApprovalResponse): Order {
        log.info("Approving order with id: {}", restaurantApprovalResponse.orderId)
        val order = orderSagaHelper.findOrder(restaurantApprovalResponse.orderId!!)
        orderDomainService.approveOrder(order)
        orderSagaHelper.saveOrder(order)
        return order
    }

    private fun getUpdatedApprovalOutboxMessage(
        orderApprovalOutboxMessage: OrderApprovalOutboxMessage,
        orderStatus: OrderStatus,
        sagaStatus: SagaStatus,
    ): OrderApprovalOutboxMessage {
        orderApprovalOutboxMessage.processedAt = ZonedDateTime.now(ZoneId.of(UTC))
        orderApprovalOutboxMessage.orderStatus = orderStatus
        orderApprovalOutboxMessage.sagaStatus = sagaStatus
        return orderApprovalOutboxMessage
    }

    private fun getUpdatedPaymentOutboxMessage(
        sagaId: String,
        orderStatus: OrderStatus,
        sagaStatus: SagaStatus,
    ): OrderPaymentOutboxMessage {
        val orderPaymentOutboxMessageResponse = paymentOutboxHelper.getPaymentOutboxMessageBySagaIdAndSagaStatus(
            UUID.fromString(sagaId),
            SagaStatus.PROCESSING,
        )
        if (orderPaymentOutboxMessageResponse.isEmpty) {
            throw OrderDomainException(
                "Payment outbox message cannot be found in ${SagaStatus.PROCESSING.name} state",
            )
        }
        val orderPaymentOutboxMessage = orderPaymentOutboxMessageResponse.get()
        orderPaymentOutboxMessage.processedAt = ZonedDateTime.now(ZoneId.of(UTC))
        orderPaymentOutboxMessage.orderStatus = orderStatus
        orderPaymentOutboxMessage.sagaStatus = sagaStatus
        return orderPaymentOutboxMessage
    }

    private fun rollbackOrder(restaurantApprovalResponse: RestaurantApprovalResponse): OrderCancelledEvent {
        log.info("Cancelling order with id: {}", restaurantApprovalResponse.orderId)
        val order = orderSagaHelper.findOrder(restaurantApprovalResponse.orderId!!)
        val domainEvent = orderDomainService.cancelOrderPayment(
            order,
            restaurantApprovalResponse.failureMessages ?: emptyList(),
        )
        orderSagaHelper.saveOrder(order)
        return domainEvent
    }
}
