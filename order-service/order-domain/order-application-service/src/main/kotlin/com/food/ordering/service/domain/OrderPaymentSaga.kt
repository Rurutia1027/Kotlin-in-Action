package com.food.ordering.system.order.service.domain

import com.food.ordering.system.domain.constants.DomainConstants.UTC
import com.food.ordering.system.domain.valueobject.OrderId
import com.food.ordering.system.domain.valueobject.OrderStatus
import com.food.ordering.system.domain.valueobject.PaymentStatus
import com.food.ordering.system.order.service.domain.dto.message.PaymentResponse
import com.food.ordering.system.order.service.domain.entity.Order
import com.food.ordering.system.order.service.domain.event.OrderPaidEvent
import com.food.ordering.system.order.service.domain.exception.OrderDomainException
import com.food.ordering.system.order.service.domain.exception.OrderNotFoundException
import com.food.ordering.system.order.service.domain.mapper.OrderDataMapper
import com.food.ordering.system.order.service.domain.outbox.model.approval.OrderApprovalOutboxMessage
import com.food.ordering.system.order.service.domain.outbox.model.payment.OrderPaymentOutboxMessage
import com.food.ordering.system.order.service.domain.outbox.scheduler.approval.ApprovalOutboxHelper
import com.food.ordering.system.order.service.domain.outbox.scheduler.payment.PaymentOutboxHelper
import com.food.ordering.system.order.service.domain.ports.output.repository.OrderRepository
import com.food.ordering.system.outbox.OutboxStatus
import com.food.ordering.system.saga.SagaStatus
import com.food.ordering.system.saga.SagaStep
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.UUID

@Component
class OrderPaymentSaga(
    private val orderDomainService: OrderDomainService,
    private val orderRepository: OrderRepository,
    private val paymentOutboxHelper: PaymentOutboxHelper,
    private val approvalOutboxHelper: ApprovalOutboxHelper,
    private val orderSagaHelper: OrderSagaHelper,
    private val orderDataMapper: OrderDataMapper,
) : SagaStep<PaymentResponse> {

    private val log = LoggerFactory.getLogger(OrderPaymentSaga::class.java)

    @Transactional
    override fun process(paymentResponse: PaymentResponse) {
        val orderPaymentOutboxMessageResponse = paymentOutboxHelper.getPaymentOutboxMessageBySagaIdAndSagaStatus(
            UUID.fromString(paymentResponse.sagaId),
            SagaStatus.STARTED,
        )
        if (orderPaymentOutboxMessageResponse.isEmpty) {
            log.info("An outbox message with saga id: {} is already processed!", paymentResponse.sagaId)
            return
        }

        val orderPaymentOutboxMessage = orderPaymentOutboxMessageResponse.get()
        val domainEvent = completePaymentForOrder(paymentResponse)
        val sagaStatus = orderSagaHelper.orderStatusToSagaStatus(domainEvent.order.orderStatus!!)

        paymentOutboxHelper.save(
            getUpdatedPaymentOutboxMessage(
                orderPaymentOutboxMessage,
                domainEvent.order.orderStatus!!,
                sagaStatus,
            ),
        )

        approvalOutboxHelper.saveApprovalOutboxMessage(
            orderDataMapper.orderPaidEventToOrderApprovalEventPayload(domainEvent),
            domainEvent.order.orderStatus!!,
            sagaStatus,
            OutboxStatus.STARTED,
            UUID.fromString(paymentResponse.sagaId),
        )

        log.info("Order with id: {} is paid", domainEvent.order.id!!.value)
    }

    @Transactional
    override fun rollback(paymentResponse: PaymentResponse) {
        val orderPaymentOutboxMessageResponse = paymentOutboxHelper.getPaymentOutboxMessageBySagaIdAndSagaStatus(
            UUID.fromString(paymentResponse.sagaId),
            *getCurrentSagaStatus(paymentResponse.paymentStatus!!),
        )
        if (orderPaymentOutboxMessageResponse.isEmpty) {
            log.info("An outbox message with saga id: {} is already roll backed!", paymentResponse.sagaId)
            return
        }

        val orderPaymentOutboxMessage = orderPaymentOutboxMessageResponse.get()
        val order = rollbackPaymentForOrder(paymentResponse)
        val sagaStatus = orderSagaHelper.orderStatusToSagaStatus(order.orderStatus!!)

        paymentOutboxHelper.save(
            getUpdatedPaymentOutboxMessage(orderPaymentOutboxMessage, order.orderStatus!!, sagaStatus),
        )

        if (paymentResponse.paymentStatus == PaymentStatus.CANCELLED) {
            approvalOutboxHelper.save(
                getUpdatedApprovalOutboxMessage(
                    paymentResponse.sagaId!!,
                    order.orderStatus!!,
                    sagaStatus,
                ),
            )
        }

        log.info("Order with id: {} is cancelled", order.id!!.value)
    }

    private fun findOrder(orderId: String): Order {
        val orderResponse = orderRepository.findById(OrderId(UUID.fromString(orderId)))
        if (orderResponse.isEmpty) {
            log.error("Order with id: {} could not be found!", orderId)
            throw OrderNotFoundException("Order with id $orderId could not be found!")
        }
        return orderResponse.get()
    }

    private fun getUpdatedPaymentOutboxMessage(
        orderPaymentOutboxMessage: OrderPaymentOutboxMessage,
        orderStatus: OrderStatus,
        sagaStatus: SagaStatus,
    ): OrderPaymentOutboxMessage {
        orderPaymentOutboxMessage.processedAt = ZonedDateTime.now(ZoneId.of(UTC))
        orderPaymentOutboxMessage.orderStatus = orderStatus
        orderPaymentOutboxMessage.sagaStatus = sagaStatus
        return orderPaymentOutboxMessage
    }

    private fun completePaymentForOrder(paymentResponse: PaymentResponse): OrderPaidEvent {
        log.info("Completing payment for order with id: {}", paymentResponse.orderId)
        val order = findOrder(paymentResponse.orderId!!)
        val domainEvent = orderDomainService.payOrder(order)
        orderRepository.save(order)
        return domainEvent
    }

    private fun getCurrentSagaStatus(paymentStatus: PaymentStatus): Array<SagaStatus> =
        when (paymentStatus) {
            PaymentStatus.COMPLETED -> arrayOf(SagaStatus.STARTED)
            PaymentStatus.CANCELLED -> arrayOf(SagaStatus.PROCESSING)
            PaymentStatus.FAILED -> arrayOf(SagaStatus.STARTED, SagaStatus.PROCESSING)
        }

    private fun rollbackPaymentForOrder(paymentResponse: PaymentResponse): Order {
        log.info("Cancelling order with id: {}", paymentResponse.orderId)
        val order = findOrder(paymentResponse.orderId!!)
        orderDomainService.cancelOrder(order, paymentResponse.failureMessages ?: emptyList())
        orderRepository.save(order)
        return order
    }

    private fun getUpdatedApprovalOutboxMessage(
        sagaId: String,
        orderStatus: OrderStatus,
        sagaStatus: SagaStatus,
    ): OrderApprovalOutboxMessage {
        val orderApprovalOutboxMessageResponse = approvalOutboxHelper.getApprovalOutboxMessageBySagaIdAndSagaStatus(
            UUID.fromString(sagaId),
            SagaStatus.COMPENSATING,
        )
        if (orderApprovalOutboxMessageResponse.isEmpty) {
            throw OrderDomainException(
                "Approval outbox message could not be found in ${SagaStatus.COMPENSATING.name} status!",
            )
        }
        val orderApprovalOutboxMessage = orderApprovalOutboxMessageResponse.get()
        orderApprovalOutboxMessage.processedAt = ZonedDateTime.now(ZoneId.of(UTC))
        orderApprovalOutboxMessage.orderStatus = orderStatus
        orderApprovalOutboxMessage.sagaStatus = sagaStatus
        return orderApprovalOutboxMessage
    }
}
