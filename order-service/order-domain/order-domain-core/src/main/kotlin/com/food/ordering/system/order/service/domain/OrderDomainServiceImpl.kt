package com.food.ordering.system.order.service.domain

import com.food.ordering.system.domain.constants.DomainConstants.UTC
import com.food.ordering.system.order.service.domain.entity.Order
import com.food.ordering.system.order.service.domain.entity.Restaurant
import com.food.ordering.system.order.service.domain.event.OrderCancelledEvent
import com.food.ordering.system.order.service.domain.event.OrderCreatedEvent
import com.food.ordering.system.order.service.domain.event.OrderPaidEvent
import com.food.ordering.system.order.service.domain.exception.OrderDomainException
import org.slf4j.LoggerFactory
import java.time.ZoneId
import java.time.ZonedDateTime

class OrderDomainServiceImpl : OrderDomainService {

    private val log = LoggerFactory.getLogger(OrderDomainServiceImpl::class.java)

    override fun validateAndInitiateOrder(order: Order, restaurant: Restaurant): OrderCreatedEvent {
        validateRestaurant(restaurant)
        setOrderProductInformation(order, restaurant)
        order.validateOrder()
        order.initializeOrder()
        log.info("Order with id: {} is initiated", order.id!!.value)
        return OrderCreatedEvent(order, ZonedDateTime.now(ZoneId.of(UTC)))
    }

    override fun payOrder(order: Order): OrderPaidEvent {
        order.pay()
        log.info("Order with id: {} is paid", order.id!!.value)
        return OrderPaidEvent(order, ZonedDateTime.now(ZoneId.of(UTC)))
    }

    override fun approveOrder(order: Order) {
        order.approve()
        log.info("Order with id: {} is approved", order.id!!.value)
    }

    override fun cancelOrderPayment(order: Order, failureMessages: List<String>): OrderCancelledEvent {
        order.initCancel(failureMessages)
        log.info("Order payment is cancelling for order id: {}", order.id!!.value)
        return OrderCancelledEvent(order, ZonedDateTime.now(ZoneId.of(UTC)))
    }

    override fun cancelOrder(order: Order, failureMessages: List<String>) {
        order.cancel(failureMessages)
        log.info("Order with id: {} is cancelled", order.id!!.value)
    }

    private fun validateRestaurant(restaurant: Restaurant) {
        if (!restaurant.active) {
            throw OrderDomainException(
                "Restaurant with id ${restaurant.id!!.value} is currently not active!",
            )
        }
    }

    private fun setOrderProductInformation(order: Order, restaurant: Restaurant) {
        order.items.forEach { orderItem ->
            restaurant.products.forEach { restaurantProduct ->
                val currentProduct = orderItem.product
                if (currentProduct == restaurantProduct) {
                    currentProduct.updateWithConfirmedNameAndPrice(
                        restaurantProduct.name!!,
                        restaurantProduct.price!!,
                    )
                }
            }
        }
    }
}
