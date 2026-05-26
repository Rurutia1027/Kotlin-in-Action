package com.food.ordering.system.restaurant.service.domain

import com.food.ordering.system.restaurant.service.domain.dto.RestaurantApprovalRequest
import com.food.ordering.system.restaurant.service.domain.ports.inputs.message.listener.RestaurantApprovalRequestMessageListener

class RestaurantApprovalRequestMessageListenerImpl(
    private val restaurantApprovalRequestHelper: RestaurantApprovalRequestHelper
) : RestaurantApprovalRequestMessageListener {
    override fun approvalOrder(restaurantApprovalRequest: RestaurantApprovalRequest) {
        TODO("Not yet implemented")
    }
}