package com.food.ordering.system.order.service.domain.dto.message

import com.food.ordering.system.domain.valueobject.OrderApprovalStatus
import java.time.Instant

data class RestaurantApprovalResponse(
    val id: String? = null,
    val sagaId: String? = null,
    val orderId: String? = null,
    val restaurantId: String? = null,
    val createdAt: Instant? = null,
    val orderApprovalStatus: OrderApprovalStatus? = null,
    val failureMessages: List<String>? = null,
)
