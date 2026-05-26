package com.food.ordering.system.restaurant.service.domain

import com.food.ordering.system.domain.constants.DomainConstants.UTC
import com.food.ordering.system.domain.valueobject.OrderApprovalStatus
import com.food.ordering.system.restaurant.service.domain.entity.Restaurant
import com.food.ordering.system.restaurant.service.domain.event.OrderApprovalEvent
import com.food.ordering.system.restaurant.service.domain.event.OrderApprovedEvent
import com.food.ordering.system.restaurant.service.domain.event.OrderRejectedEvent
import org.slf4j.LoggerFactory
import java.time.ZoneId
import java.time.ZonedDateTime

class RestaurantDomainServiceImpl : RestaurantDomainService {
    private val log = LoggerFactory.getLogger(RestaurantDomainServiceImpl::class.java)


    override fun validateOrder(
        restaurant: Restaurant,
        failureMessages: MutableList<String>
    ): OrderApprovalEvent {
        restaurant.validateOrder(failureMessages)
        log.info("Validating order with id: {}", restaurant.orderDetail.id!!.value)

        return if (failureMessages.isEmpty()) {
            log.info("Order is approved for order id: {}", restaurant.orderDetail.id!!.value)
            restaurant.constructOrderApproval(OrderApprovalStatus.APPROVED)
            OrderApprovedEvent(
                restaurant.orderApproval!!,
                restaurant.id!!,
                failureMessages,
                ZonedDateTime.now(ZoneId.of(UTC))
            )
        } else {
            log.info("Order is rejected for order id: {}", restaurant.orderDetail.id!!.value)
            restaurant.constructOrderApproval(OrderApprovalStatus.REJECTED)
            OrderRejectedEvent(
                restaurant.orderApproval!!,
                restaurant.id!!,
                failureMessages,
                ZonedDateTime.now(ZoneId.of(UTC))
            )
        }
    }
}