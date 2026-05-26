package com.food.ordering.system.order.service.domain

import com.food.ordering.system.domain.valueobject.OrderId
import com.food.ordering.system.domain.valueobject.OrderStatus
import com.food.ordering.system.order.service.domain.entity.Order
import com.food.ordering.system.order.service.domain.exception.OrderNotFoundException
import com.food.ordering.system.order.service.domain.ports.output.repository.OrderRepository
import com.food.ordering.system.saga.SagaStatus
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class OrderSagaHelper(
    private val orderRepository: OrderRepository,
) {
    private val log = LoggerFactory.getLogger(OrderSagaHelper::class.java)

    fun findOrder(orderId: String): Order {
        val orderResponse = orderRepository.findById(OrderId(UUID.fromString(orderId)))
        if (orderResponse.isEmpty) {
            log.error("Order with id: {} could not be found!", orderId)
            throw OrderNotFoundException("Order with id $orderId could not be found!")
        }
        return orderResponse.get()
    }

    fun saveOrder(order: Order) {
        orderRepository.save(order)
    }

    fun orderStatusToSagaStatus(orderStatus: OrderStatus): SagaStatus =
        when (orderStatus) {
            OrderStatus.PAID -> SagaStatus.PROCESSING
            OrderStatus.APPROVED -> SagaStatus.SUCCEEDED
            OrderStatus.CANCELLING -> SagaStatus.COMPENSATING
            OrderStatus.CANCELLED -> SagaStatus.COMPENSATED
            else -> SagaStatus.STARTED
        }
}
