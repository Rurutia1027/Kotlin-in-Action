package com.food.ordering.system.restaurant.service.domain.outbox.model

import java.time.ZonedDateTime

data class OrderEventPayload(
    val orderId: String,
    val restaurantId: String,
    val createdAt: ZonedDateTime,
    val orderApprovalStatus: String,
    val failureMessages: List<String>,
) {
    companion object {
        fun builder(): Builder = Builder()
    }

    class Builder {
        private var orderId: String? = null
        private var restaurantId: String? = null
        private var createdAt: ZonedDateTime? = null
        private var orderApprovalStatus: String? = null
        private var failureMessages: List<String>? = null

        fun orderId(value: String) = apply { orderId = value }
        fun restaurantId(value: String) = apply { restaurantId = value }
        fun createdAt(value: ZonedDateTime) = apply { createdAt = value }
        fun orderApprovalStatus(value: String) = apply { orderApprovalStatus = value }
        fun failureMessages(value: List<String>) = apply { failureMessages = value }

        fun build(): OrderEventPayload = OrderEventPayload(
            orderId = orderId!!,
            restaurantId = restaurantId!!,
            createdAt = createdAt!!,
            orderApprovalStatus = orderApprovalStatus!!,
            failureMessages = failureMessages!!,
        )
    }
}
