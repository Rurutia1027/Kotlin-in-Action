package com.food.ordering.system.order.service.domain.outbox.model.approval

import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal
import java.time.ZonedDateTime

data class OrderApprovalEventPayload(
    @JsonProperty val orderId: String? = null,
    @JsonProperty val restaurantId: String? = null,
    @JsonProperty val price: BigDecimal? = null,
    @JsonProperty val createdAt: ZonedDateTime? = null,
    @JsonProperty val restaurantOrderStatus: String? = null,
    @JsonProperty val products: List<OrderApprovalEventProduct>? = null,
) {
    class Builder {
        private var orderId: String? = null
        private var restaurantId: String? = null
        private var price: BigDecimal? = null
        private var createdAt: ZonedDateTime? = null
        private var restaurantOrderStatus: String? = null
        private var products: List<OrderApprovalEventProduct>? = null

        fun orderId(value: String) = apply { orderId = value }
        fun restaurantId(value: String) = apply { restaurantId = value }
        fun price(value: BigDecimal) = apply { price = value }
        fun createdAt(value: ZonedDateTime) = apply { createdAt = value }
        fun restaurantOrderStatus(value: String) = apply { restaurantOrderStatus = value }
        fun products(value: List<OrderApprovalEventProduct>) = apply { products = value }

        fun build() = OrderApprovalEventPayload(
            orderId = orderId,
            restaurantId = restaurantId,
            price = price,
            createdAt = createdAt,
            restaurantOrderStatus = restaurantOrderStatus,
            products = products,
        )
    }

    companion object {
        @JvmStatic
        fun builder(): Builder = Builder()
    }
}
