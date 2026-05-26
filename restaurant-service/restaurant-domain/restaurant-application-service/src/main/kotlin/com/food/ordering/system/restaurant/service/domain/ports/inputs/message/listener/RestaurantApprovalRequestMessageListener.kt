package com.food.ordering.system.restaurant.service.domain.ports.inputs.message.listener

import com.food.ordering.system.restaurant.service.domain.dto.RestaurantApprovalRequest

interface RestaurantApprovalRequestMessageListener {
    fun approvalOrder(restaurantApprovalRequest: RestaurantApprovalRequest): Unit
}