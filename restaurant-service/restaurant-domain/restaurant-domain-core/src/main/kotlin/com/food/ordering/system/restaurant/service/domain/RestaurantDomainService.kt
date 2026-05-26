package com.food.ordering.system.restaurant.service.domain

import com.food.ordering.system.restaurant.service.domain.entity.Restaurant
import com.food.ordering.system.restaurant.service.domain.event.OrderApprovalEvent

interface RestaurantDomainService {
    fun validateOrder(restaurant: Restaurant, failureMessages: MutableList<String>): OrderApprovalEvent
}
