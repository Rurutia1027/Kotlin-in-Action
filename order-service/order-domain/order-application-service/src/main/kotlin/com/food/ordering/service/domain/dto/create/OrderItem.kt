package com.food.ordering.system.order.service.domain.dto.create

import jakarta.validation.constraints.NotNull
import java.math.BigDecimal
import java.util.*

data class OrderItem(
    @field:NotNull val productId: UUID,
    @field:NotNull val quantity: Int,
    @field:NotNull val price: BigDecimal,
    @field:NotNull val subTotal: BigDecimal,
) {
    class Builder {
        private var productId: UUID? = null
        private var quantity: Int? = null
        private var price: BigDecimal? = null
        private var subTotal: BigDecimal? = null

        fun productId(value: UUID) = apply { productId = value }
        fun quantity(value: Int) = apply { quantity = value }
        fun price(value: BigDecimal) = apply { price = value }
        fun subTotal(value: BigDecimal) = apply { subTotal = value }

        fun build() = OrderItem(
            productId = productId!!,
            quantity = quantity!!,
            price = price!!,
            subTotal = subTotal!!,
        )
    }

    companion object {
        @JvmStatic
        fun builder(): Builder = Builder()
    }
}
