package com.food.ordering.system.restaurant.service.domain.entity

import com.food.ordering.system.domain.entity.BaseEntity
import com.food.ordering.system.domain.valueobject.OrderApprovalStatus
import com.food.ordering.system.domain.valueobject.OrderId
import com.food.ordering.system.domain.valueobject.RestaurantId
import com.food.ordering.system.restaurant.service.domain.valueobject.OrderApprovalId

class OrderApproval private constructor(
    val restaurantId: RestaurantId,
    val orderId: OrderId,
    val approvalStatus: OrderApprovalStatus
) : BaseEntity<OrderApprovalId>() {
    class Builder {
        private var orderApprovalId: OrderApprovalId? = null
        private var restaurantId: RestaurantId? = null
        private var orderId: OrderId? = null
        private var approvalStatus: OrderApprovalStatus? = null

        fun orderApprovalId(value: OrderApprovalId) = apply { orderApprovalId = value }
        fun restaurantId(value: RestaurantId) = apply { restaurantId = value }
        fun orderId(value: OrderId) = apply { orderId = value }
        fun approvalStatus(value: OrderApprovalStatus) = apply { approvalStatus = value }

        fun build(): OrderApproval =
            OrderApproval(
                restaurantId = restaurantId!!,
                orderId = orderId!!,
                approvalStatus = approvalStatus!!,
            ).also { it.id = orderApprovalId }
    }

    companion object {
        @JvmStatic
        fun builder(): Builder = Builder()
    }
}