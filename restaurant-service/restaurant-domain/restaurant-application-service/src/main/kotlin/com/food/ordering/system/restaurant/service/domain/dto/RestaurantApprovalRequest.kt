package com.food.ordering.system.restaurant.service.domain.dto

import com.food.ordering.system.domain.valueobject.RestaurantOrderStatus
import com.food.ordering.system.restaurant.service.domain.entity.Product
import java.math.BigDecimal
import java.time.Instant

data class RestaurantApprovalRequest(
    val id: String,
    val sagaId: String,
    val restaurantId: String,
    val orderId: String,
    val restaurantOrderStatus: RestaurantOrderStatus,
    val products: List<Product>,
    val price: BigDecimal,
    val createdAt: Instant
) {
    companion object {
        fun builder(): Builder = Builder()
    }

    class Builder {
        private var id: String? = null
        private var sagaId: String? = null
        private var restaurantId: String? = null
        private var orderId: String? = null
        private var restaurantOrderStatus: RestaurantOrderStatus? = null
        private var products: List<Product>? = null
        private var price: BigDecimal? = null
        private var createdAt: Instant? = null

        fun id(value: String) = apply { id = value }
        fun sagaId(value: String) = apply { sagaId = value }
        fun restaurantId(value: String) = apply { restaurantId = value }
        fun orderId(value: String) = apply { orderId = value }
        fun restaurantOrderStatus(value: RestaurantOrderStatus) = apply { restaurantOrderStatus = value }
        fun products(value: List<Product>) = apply { products = value }
        fun price(value: BigDecimal) = apply { price = value }
        fun createdAt(value: Instant) = apply { createdAt = value }

        fun build(): RestaurantApprovalRequest = RestaurantApprovalRequest(
            id = id!!,
            sagaId = sagaId!!,
            restaurantId = restaurantId!!,
            orderId = orderId!!,
            restaurantOrderStatus = restaurantOrderStatus!!,
            products = products!!,
            price = price!!,
            createdAt = createdAt!!,
        )
    }
}