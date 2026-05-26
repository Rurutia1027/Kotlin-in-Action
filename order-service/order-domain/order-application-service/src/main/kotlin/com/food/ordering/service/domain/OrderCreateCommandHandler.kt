package com.food.ordering.system.order.service.domain

import com.food.ordering.system.order.service.domain.dto.create.CreateOrderCommand
import com.food.ordering.system.order.service.domain.dto.create.CreateOrderResponse
import com.food.ordering.system.order.service.domain.mapper.OrderDataMapper
import com.food.ordering.system.order.service.domain.outbox.scheduler.payment.PaymentOutboxHelper
import com.food.ordering.system.outbox.OutboxStatus
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Component
class OrderCreateCommandHandler(
    private val orderCreateHelper: OrderCreateHelper,
    private val orderDataMapper: OrderDataMapper,
    private val paymentOutboxHelper: PaymentOutboxHelper,
    private val orderSagaHelper: OrderSagaHelper,
) {
    private val log = LoggerFactory.getLogger(OrderCreateCommandHandler::class.java)

    @Transactional
    fun createOrder(createOrderCommand: CreateOrderCommand): CreateOrderResponse {
        val orderCreatedEvent = orderCreateHelper.persistOrder(createOrderCommand)
        log.info("Order is created with id: {}", orderCreatedEvent.order.id!!.value)

        val createOrderResponse = orderDataMapper.orderToCreateOrderResponse(
            orderCreatedEvent.order,
            "Order created successfully",
        )

        paymentOutboxHelper.savePaymentOutboxMessage(
            orderDataMapper.orderCreatedEventToOrderPaymentEventPayload(orderCreatedEvent),
            orderCreatedEvent.order.orderStatus!!,
            orderSagaHelper.orderStatusToSagaStatus(orderCreatedEvent.order.orderStatus!!),
            OutboxStatus.STARTED,
            UUID.randomUUID(),
        )

        log.info("Returning CreateOrderResponse with order id: {}", orderCreatedEvent.order.id)

        return createOrderResponse
    }
}
