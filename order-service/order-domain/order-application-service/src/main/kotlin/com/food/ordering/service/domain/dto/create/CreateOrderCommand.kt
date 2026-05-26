package com.food.ordering.system.order.service.domain.dto.create

import jakarta.validation.constraints.NotNull
import java.math.BigDecimal
import java.util.*

data class CreateOrderCommand(
    @field:NotNull val customerId: UUID,
    @field:NotNull val restaurantId: UUID,
    @field:NotNull val price: BigDecimal,
    @field:NotNull val items: List<OrderItem>,
    @field:NotNull val address: OrderAddress,
) {
    class Builder {
        private var customerId: UUID? = null
        private var restaurantId: UUID? = null
        private var price: BigDecimal? = null
        private var items: List<OrderItem>? = null
        private var address: OrderAddress? = null

        fun customerId(value: UUID) = apply { customerId = value }
        fun restaurantId(value: UUID) = apply { restaurantId = value }
        fun price(value: BigDecimal) = apply { price = value }
        fun items(value: List<OrderItem>) = apply { items = value }
        fun address(value: OrderAddress) = apply { address = value }

        fun build() = CreateOrderCommand(
            customerId = customerId!!,
            restaurantId = restaurantId!!,
            price = price!!,
            items = items!!,
            address = address!!,
        )
    }

    companion object {
        @JvmStatic
        fun builder(): Builder = Builder()
    }
}
