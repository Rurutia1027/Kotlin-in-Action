package com.food.ordering.system.order.service.domain

import com.food.ordering.system.order.service.domain.dto.create.CreateOrderCommand
import com.food.ordering.system.order.service.domain.entity.Customer
import com.food.ordering.system.order.service.domain.entity.Order
import com.food.ordering.system.order.service.domain.entity.Restaurant
import com.food.ordering.system.order.service.domain.event.OrderCreatedEvent
import com.food.ordering.system.order.service.domain.exception.OrderDomainException
import com.food.ordering.system.order.service.domain.mapper.OrderDataMapper
import com.food.ordering.system.order.service.domain.ports.output.repository.CustomerRepository
import com.food.ordering.system.order.service.domain.ports.output.repository.OrderRepository
import com.food.ordering.system.order.service.domain.ports.output.repository.RestaurantRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Component
class OrderCreateHelper(
    private val orderDomainService: OrderDomainService,
    private val orderRepository: OrderRepository,
    private val customerRepository: CustomerRepository,
    private val restaurantRepository: RestaurantRepository,
    private val orderDataMapper: OrderDataMapper,
) {
    private val log = LoggerFactory.getLogger(OrderCreateHelper::class.java)

    @Transactional
    fun persistOrder(createOrderCommand: CreateOrderCommand): OrderCreatedEvent {
        checkCustomer(createOrderCommand.customerId)
        val restaurant = checkRestaurant(createOrderCommand)
        val order = orderDataMapper.createOrderCommandToOrder(createOrderCommand)
        val orderCreatedEvent = orderDomainService.validateAndInitiateOrder(order, restaurant)
        saveOrder(order)
        log.info("Order is created with id: {}", orderCreatedEvent.order.id!!.value)
        return orderCreatedEvent
    }

    private fun checkRestaurant(createOrderCommand: CreateOrderCommand): Restaurant {
        val restaurant = orderDataMapper.createOrderCommandToRestaurant(createOrderCommand)
        val optionalRestaurant = restaurantRepository.findRestaurantInformation(restaurant)
        if (optionalRestaurant.isEmpty) {
            log.warn("Could not find restaurant with restaurant id: {}", createOrderCommand.restaurantId)
            throw OrderDomainException(
                "Could not find restaurant with restaurant id: ${createOrderCommand.restaurantId}",
            )
        }
        return optionalRestaurant.get()
    }

    private fun checkCustomer(customerId: UUID) {
        val customer = customerRepository.findCustomer(customerId)
        if (customer.isEmpty) {
            log.warn("Could not find customer with customer id: {}", customerId)
            throw OrderDomainException("Could not find customer with customer id: $customerId")
        }
    }

    private fun saveOrder(order: Order): Order {
        val orderResult = orderRepository.save(order)
            ?: throw OrderDomainException("Could not save order!")
        log.info("Order is saved with id: {}", orderResult.id!!.value)
        return orderResult
    }
}
