package com.food.ordering.system.order.service.domain.dto.create

import com.food.ordering.system.domain.valueobject.OrderStatus
import jakarta.validation.constraints.NotNull
import java.util.*

data class CreateOrderResponse(
    @field:NotNull val orderTrackingId: UUID,
    @field:NotNull val orderStatus: OrderStatus,
    @field:NotNull val message: String,
) {
    class Builder {
        private var orderTrackingId: UUID? = null
        private var orderStatus: OrderStatus? = null
        private var message: String? = null

        fun orderTrackingId(value: UUID) = apply { orderTrackingId = value }
        fun orderStatus(value: OrderStatus) = apply { orderStatus = value }
        fun message(value: String) = apply { message = value }

        fun build() = CreateOrderResponse(
            orderTrackingId = orderTrackingId!!,
            orderStatus = orderStatus!!,
            message = message!!,
        )
    }

    companion object {
        @JvmStatic
        fun builder(): Builder = Builder()
    }
}
