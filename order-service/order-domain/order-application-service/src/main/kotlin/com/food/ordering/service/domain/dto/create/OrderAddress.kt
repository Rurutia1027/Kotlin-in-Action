package com.food.ordering.system.order.service.domain.dto.create

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.NotNull

data class OrderAddress(
    @field:NotNull @field:Max(50) val street: String,
    @field:NotNull @field:Max(10) val postalCode: String,
    @field:NotNull @field:Max(50) val city: String,
) {
    class Builder {
        private var street: String? = null
        private var postalCode: String? = null
        private var city: String? = null

        fun street(value: String) = apply { street = value }
        fun postalCode(value: String) = apply { postalCode = value }
        fun city(value: String) = apply { city = value }

        fun build() = OrderAddress(
            street = street!!,
            postalCode = postalCode!!,
            city = city!!,
        )
    }

    companion object {
        @JvmStatic
        fun builder(): Builder = Builder()
    }
}
